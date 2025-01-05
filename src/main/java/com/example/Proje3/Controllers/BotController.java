package com.example.Proje3.Controllers;


import com.example.Proje3.Repositorys.CustomersRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Random;

@Component
public class BotController {
    @Autowired
    CustomersRepository customersRepository;

    public BotController(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    private static final String LOGIN_URL = "http://localhost:8080/login";

    public String name;
    @Autowired
    private InitializationService initializationService;

    public void startBot() {
        int THREAD_COUNT = customersRepository.findAll().size();
        List<String> customerNames = initializationService.getCustomerNames();
        Semafor semaphore = new Semafor(THREAD_COUNT);

        for (String customerName : customerNames) {
            new Thread(() -> {
                try {
                    semaphore.acquire(); // Semaphore'dan izin al
                    performLogin(customerName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); // İşlem tamamlandığında semaforu serbest bırak
                }
            }).start();
        }

    }

    private void performLogin(String customerName) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\yigit\\Downloads\\chromedriver-win64\\chromedriver.exe"); // chromedriver'ın yolu

        WebDriver driver = new ChromeDriver();
        try {
            driver.get(LOGIN_URL); // Giriş sayfasını aç
            // Kullanıcı adı alanını bul ve kullanıcı adını gir
            Thread.sleep(3000);
            WebElement usernameField = driver.findElement(By.id("CustomerName"));
            usernameField.sendKeys(customerName);

            // Login butonuna tıkla
            WebElement loginButton = driver.findElement(By.xpath("//input[@type='submit']"));
            loginButton.click();

            // Giriş işlemi yapılırken bekleme
            Thread.sleep(3000); // 3 saniye bekle, sayfa yüklenene kadar

            placeOrder(driver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder(WebDriver driver) {
        try {
            Thread.sleep(1000);
            Random random = new Random();
            // Tablodaki tüm ürün satırlarını seç
            List<WebElement> productRows = driver.findElements(By.xpath("//tr[contains(@data-bs-toggle, 'modal')]"));

            // Rastgele 1 ila 3 sipariş oluştur
            int orderCount = random.nextInt(3) + 1;
            for (int i = 0; i < orderCount; i++) {
                Thread.sleep(3000);
                // Rastgele bir ürün satırı seç
                WebElement randomProductRow = productRows.get(random.nextInt(productRows.size()));
                randomProductRow.click(); // Sipariş modalını aç

                Thread.sleep(2000);

                // Modal içerisindeki alanları doldur
                WebElement quantityField = driver.findElement(By.id("orderQuantity"));
                int randomQuantity = random.nextInt(6) + 1; // 1 ile 5 arasında rastgele miktar
                quantityField.sendKeys(String.valueOf(randomQuantity)); // Miktarı gir
                Thread.sleep(1000);
                WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
                submitButton.click(); // Siparişi gönder


                Thread.sleep(2000); // Bir sonraki sipariş için bekle
                driver.get("http://localhost:8080/customer");  // Sayfayı yenileyin
                Thread.sleep(3000);  // Yenilenen sayfa için kısa bir bekleme

                // Ürün tablosunu tekrar bul
                productRows = driver.findElements(By.xpath("//tr[contains(@data-bs-toggle, 'modal')]"));


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
