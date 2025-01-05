package com.example.Proje3.Controllers;

import com.example.Proje3.Repositorys.*;
import com.example.Proje3.Tablolar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class adminController {
    @Autowired
    ProductsRepository productsRepository;

    @Autowired
    LogsRepository logsRepository;
    @Autowired
     UnapprovedOrdersRepository unapprovedOrdersRepository;

    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
     CustomersRepository customersRepository;

    @Autowired
    private LogController logController;
    @Autowired
    private InitializationService initializationService;

    @Autowired
    private BotController botController;



    public adminController() {}

    public adminController(ProductsRepository productsRepository, LogsRepository logsRepository, UnapprovedOrdersRepository unapprovedOrdersRepository, OrdersRepository ordersRepository, CustomersRepository customersRepository, LogController logController) {
        this.productsRepository = productsRepository;
        this.logsRepository = logsRepository;
        this.unapprovedOrdersRepository = unapprovedOrdersRepository;
        this.ordersRepository = ordersRepository;
        this.customersRepository = customersRepository;
        this.logController = logController;
    }





    @GetMapping("/urunGuncelle/{id}")
    @ResponseBody
    public String urunGuncelleForm(@PathVariable("id") int id, Model model) {
        if (productsRepository.existsById(id)) {
            Products product = productsRepository.findById(id).orElse(null);
            model.addAttribute("product", product);

        }

        return "admin";
    }

    @GetMapping("/admin")
    public String showAdminPage(Model model) {

        List<Products> productsList = productsRepository.findAll();

        if(!unapprovedOrdersRepository.findAll().isEmpty()) {
            System.out.println("saaaaa");
            // Siparişleri, her siparişin müşterisinin PriorityScore değerine göre sıralıyoruz
            List<UnapprovedOrders> orders = unapprovedOrdersRepository.findAll().stream().sorted((o1, o2) -> {
                double score1 = customersRepository.findByCustomerId(o1.getCustomerID()).getPriorityScore();
                double score2 = customersRepository.findByCustomerId(o2.getCustomerID()).getPriorityScore();
                return Double.compare(score2, score1);  // Büyükten küçüğe sıralama
            }).toList();
            model.addAttribute("unapprovedOrders", orders);
        }



        model.addAttribute("productsList", productsList);

        return "admin";
    }

    @PostMapping("/sistemibaslat")
    public String initializeSystem() throws InterruptedException {
        initializationService.initializeCustomers();
        botController.startBot();
        return "redirect:/admin";
    }


    @PostMapping("/urunEkle")
    public String urunEkle(@RequestParam("ProductName") String ProductName,@RequestParam("Stock") int Stock, @RequestParam("ProductPrice") double ProductPrice, Model model) {
        Products products = new Products();
        products.setProductName(ProductName);
        products.setStock(Stock);
        products.setPrice(BigDecimal.valueOf(ProductPrice));
        productsRepository.save(products);


        return "redirect:/admin";
    }


    @PostMapping("/urunSil/{id}")
    public String urunSil(@PathVariable("id") int id) {
        if (productsRepository.existsById(id)) {
            productsRepository.deleteById(id);
        }

        // Silme işleminden sonra admin sayfasına yönlendirme yap
        return "redirect:/admin";
    }




    @PostMapping("/urunGuncelle/{id}")
    public String urunGuncelle(@PathVariable("id") int id, @RequestParam("ProductName") String ProductName,@RequestParam("Stock") int Stock, @RequestParam("ProductPrice") double ProductPrice,Model model) {

        Products product = productsRepository.findById(id).orElse(null);
        assert product != null;
        model.addAttribute("product", product);
            System.out.println(product);
            product.setProductName(ProductName);
            product.setStock(Stock);
            product.setPrice(BigDecimal.valueOf(ProductPrice));
            productsRepository.save(product);

        return "redirect:/admin";
    }


    @PostMapping("/tumSiparisleriOnayla")
    public String approveAllOrders() {
        List<UnapprovedOrders> orders = unapprovedOrdersRepository.findAll().stream().sorted((o1, o2) -> {
                    // Müşterilerin PriorityScore değerlerini karşılaştır
                    double score1 = customersRepository.findByCustomerId(o1.getCustomerID()).getPriorityScore();
                    double score2 = customersRepository.findByCustomerId(o2.getCustomerID()).getPriorityScore();
                    return Double.compare(score2, score1);  // Büyükten küçüğe sıralama
                }).toList();

        for (UnapprovedOrders unapprovedOrder : orders) {
            Products product = productsRepository.findByProductId(unapprovedOrder.getProductID());
            Customers customer = customersRepository.findByCustomerId(unapprovedOrder.getCustomerID().longValue());
            logController.addLog(unapprovedOrder.getCustomerID(), product.getProductName(), unapprovedOrder.getQuantity(),
                    "Bilgilendirme", customer.getCustomerName() + " müşterisinin siparişi admin tarafından ONAYLANDI", customer.getCustomerType());


            if (product.getStock().doubleValue() < unapprovedOrder.getQuantity()) {
                logController.addLog(unapprovedOrder.getCustomerID(), product.getProductName(), unapprovedOrder.getQuantity(),
                        "Hata", customer.getCustomerName() + " müşterisinin siparişi stok yetersizliğinden iptal edildi", customer.getCustomerType());
                continue; // Bu sipariş iptal edilir, bir sonraki sipariş onaylanır
            }




            // Siparişi onayla
            Orders newOrder = new Orders();
            newOrder.setCustomerID(unapprovedOrder.getCustomerID());
            newOrder.setProductID(unapprovedOrder.getProductID());
            newOrder.setQuantity(unapprovedOrder.getQuantity());
            newOrder.setTotalPrice(unapprovedOrder.getTotalPrice());
            newOrder.setOrderDate(unapprovedOrder.getOrderDate());
            newOrder.setOrderStatus(Orders.OrderStatus.valueOf("Tamamlandı"));

            // Stok güncelle
            product.setStock(product.getStock() - unapprovedOrder.getQuantity());
            productsRepository.save(product);

            // Siparişi kaydet ve onaylanmamış siparişi sil
            ordersRepository.save(newOrder);
            unapprovedOrdersRepository.delete(unapprovedOrder);
        }

        return "redirect:/admin";
    }


    @PostMapping("/onaylanmamisEtkinlikSil/{id}")
    public String deleteUnapprovedOrder(@PathVariable Integer id) {
        Optional<UnapprovedOrders> order = unapprovedOrdersRepository.findById(id);
        if(order.isPresent()){
            UnapprovedOrders unapprovedOrder = order.get();
            Products product = productsRepository.findByProductId(unapprovedOrder.getProductID());
            Customers customer = customersRepository.findByCustomerId(unapprovedOrder.getCustomerID().longValue());

            logController.addLog(unapprovedOrder.getCustomerID(), product.getProductName(), (int) unapprovedOrder.getQuantity(), "Bilgilendirme", customer.getCustomerName() + " müşterisinin siparişi admin tarafından SİLİNDİ", customer.getCustomerType());

            // OrderID ile siparişi silme işlemleri
            unapprovedOrdersRepository.deleteById(id);
        }
        return "redirect:/admin";
    }


    @GetMapping("/productStock")
    public String productStock(Model model) {
        List<Products> productsList = productsRepository.findAll();
        model.addAttribute("productsList", productsList);
        return "productStock";
    }

    @GetMapping("/logs")
    public String viewLogs(Model model) {
        List<Logs> logsList = logsRepository.findAll();
        model.addAttribute("logs",logsList);
        return "logs";
    }

    public  void updatePriorityScores() {
        List<UnapprovedOrders> unapprovedOrders = unapprovedOrdersRepository.findAll();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis()); // Şu anki zamanın Timestamp formatında alınması

        for (UnapprovedOrders order : unapprovedOrders) {
            Customers customer = customersRepository.findByCustomerId(order.getCustomerID().longValue());
                Timestamp orderTimestamp = new Timestamp(order.getOrderDate().getTime());
                long waitingTimeInMillis = currentTime.getTime() - orderTimestamp.getTime();
                double waitingTimeInHours = waitingTimeInMillis / (1000.0 * 60.0 * 60.0); // Saat cinsine çevrilmesi
                double waitingWeight = 0.5; // Bekleme süresi ağırlığı

                // Öncelik Skorunu Hesapla
                double priorityScore = customer.getPriorityScore() + (waitingTimeInHours * waitingWeight);
                BigDecimal priorityScoreDecimal = new BigDecimal(priorityScore).setScale(2, RoundingMode.HALF_UP);
                customer.setPriorityScore(priorityScoreDecimal.doubleValue());
                customersRepository.save(customer);

        }
    }


}
