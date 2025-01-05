package com.example.Proje3.Controllers;

import com.example.Proje3.Repositorys.CustomersRepository;
import com.example.Proje3.Repositorys.OrdersRepository;
import com.example.Proje3.Repositorys.ProductsRepository;
import com.example.Proje3.Repositorys.UnapprovedOrdersRepository;
import com.example.Proje3.Tablolar.Customers;
import com.example.Proje3.Tablolar.Orders;
import com.example.Proje3.Tablolar.Products;
import com.example.Proje3.Tablolar.UnapprovedOrders;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Controller
public class CustomerController {


    private final CustomersRepository customersRepository;
    private final ProductsRepository productsRepository;
    private final LogController logController;
    private final UnapprovedOrdersRepository unapprovedOrdersRepository;
    private OrdersRepository ordersRepository;
    @Autowired
    private adminController adminController;

    public CustomerController(CustomersRepository customersRepository, ProductsRepository productsRepository, OrdersRepository ordersRepository, LogController logController, UnapprovedOrdersRepository unapprovedOrdersRepository) {
        this.customersRepository = customersRepository;
        this.productsRepository = productsRepository;
        this.ordersRepository = ordersRepository;
        this.logController = logController;
        this.unapprovedOrdersRepository = unapprovedOrdersRepository;
    }

    @GetMapping("/customer")
    public String customer(Model model, HttpSession session) {
        List<Customers> customersList = customersRepository.findAll();
        List<Products> productsList = productsRepository.findAll();
        String CustomerName = (String) session.getAttribute("CustomerName");

        Customers customers = customersRepository.findByCustomerName(CustomerName);
        model.addAttribute("customerOrder", customers);
        model.addAttribute("customers", customersList);
        model.addAttribute("productsList", productsList);
        model.addAttribute("CustomerName", CustomerName);

        return "customer";
    }

    @GetMapping("/placeOrder")
    public String placeOrder(Model model, HttpSession session) {
        String CustomerName = (String) session.getAttribute("CustomerName");
        Customers customers = customersRepository.findByCustomerName(CustomerName);
        model.addAttribute("customerOrder", customers);
        return "customer";
    }


    @PostMapping("/placeOrder")
    @ResponseBody
    public String placeOrder(
            @RequestParam("customerId") int customerId,
            @RequestParam("productId") int productId,
            @RequestParam("orderQuantity") double quantity) {

        // Ürün ve müşteri bilgilerini al
        Products product = productsRepository.findById(productId).orElse(null);
        Customers customer = customersRepository.findById((long) customerId).orElse(null);

        if (product == null || customer == null) {
            return "Hata: Ürün veya müşteri bulunamadı.";
        }

        List<Orders> customerOrders = ordersRepository.findByCustomerIDAndProductID(customerId, productId);
        double totalOrderedQuantity = customerOrders.stream().mapToDouble(Orders::getQuantity).sum();

        // Maksimum alınabilecek miktar kontrolü
        if (totalOrderedQuantity + quantity > 5) {
            logController.addLog(customerId, product.getProductName(), (int) quantity, "Uyarı", customer.getCustomerName() + " müşterisinin siparişi 5 adetten fazla almaya çalıştığı için iptal edildi", customer.getCustomerType());
            return "Hata: Bu üründen en fazla 5 adet satın alabilirsiniz. Mevcut siparişlerinizle birlikte toplam: " + (totalOrderedQuantity + quantity);
        }

        // Stok kontrolü
        if (product.getStock().doubleValue() < quantity) {
            logController.addLog(customerId, product.getProductName(), (int) quantity, "Hata", customer.getCustomerName() + " müşterisinin siparişi stok yetersizliğinden iptal edildi", customer.getCustomerType());
            return "Hata: Ürün stoğu yetersiz. Mevcut stok: " + product.getStock();
        }

        // Siparişin toplam fiyatı
        double totalPrice = quantity * product.getPrice().doubleValue();

        // Bütçe kontrolü
        if (customer.getBudget().doubleValue() < totalPrice) {
            logController.addLog(customerId, product.getProductName(), (int) quantity, "Hata", customer.getCustomerName() + " müşterisinin siparişi bakiye yetersizliğinden iptal edildi", customer.getCustomerType());
            return "Hata: Bütçe yetersiz. Toplam Fiyat: " + totalPrice + " TL, Müşteri Bütçesi: " + customer.getBudget() + " TL";
        }

        // Siparişi işleme al
        logController.addLog(customerId, product.getProductName(), (int) quantity, "Bilgilendirme", customer.getCustomerName() + " müşterisinin siparişi İŞLEME alındı", customer.getCustomerType());

        // Onaylanmamış siparişi oluştur ve kaydet
        UnapprovedOrders newOrder = new UnapprovedOrders();
        newOrder.setCustomerID(customerId);
        newOrder.setProductID(productId);
        newOrder.setQuantity((int) quantity);
        newOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        newOrder.setOrderDate(new Date(System.currentTimeMillis()));
        newOrder.setOrderStatus(UnapprovedOrders.OrderStatus.valueOf("Isleniyor"));
        newOrder.setWaitingTime(new Timestamp(System.currentTimeMillis()));

        // Müşterinin totalSpent ve budget değerlerini güncelle
        BigDecimal newTotalSpent = customer.getTotalSpent().add(BigDecimal.valueOf(totalPrice));

        if (newTotalSpent.compareTo(customer.getBudget()) > 0) {
            logController.addLog(customerId, product.getProductName(), (int) quantity, "Hata", customer.getCustomerName() + " müşterisinin siparişi bakiye yetersizliğinden iptal edildi", customer.getCustomerType());
            return "Hata: Bütçe yetersiz. Toplam Fiyat: " + totalPrice + " TL, Müşteri Bütçesi: " + customer.getBudget() + " TL";
        }

        customer.setTotalSpent(newTotalSpent);
        customer.setBudget(customer.getBudget().subtract(BigDecimal.valueOf(totalPrice)));

        // Veritabanına kayıt
        unapprovedOrdersRepository.save(newOrder);
        customersRepository.save(customer);

        // Öncelik puanlarını güncelle
        adminController.updatePriorityScores();

        return "Müşterinin siparişi işleme alındı: " + "Sipariş edilen ürün adedi " + quantity + " Müşteri Kalan Bütçesi: " + customer.getBudget() + " TL";
    }

}
