package com.example.Proje3.Controllers;


import com.example.Proje3.Repositorys.CustomersRepository;
import com.example.Proje3.Tablolar.Customers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class LoginController {

@Autowired
    CustomersRepository customersRepository;

@PostMapping("/login")
public String login(@RequestParam("CustomerName") String CustomerName, Model model, HttpSession session) {
    if (CustomerName.equals("admin")) {
        return "redirect:/admin";//kullanıcı adı ve sifre adminle ilgiliyse admin sayfasına at
    }
    Customers customers = customersRepository.findByCustomerName(CustomerName);
    if (customers != null) {
        // Kullanıcı adını oturuma ekliyoruz
        session.setAttribute("CustomerName", CustomerName);
        return "redirect:/customer";
    } else {
        model.addAttribute("error", "Kullanıcı adı veya şifre hatalı");
        return "login";
    }

}



    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
