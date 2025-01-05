package com.example.Proje3.Controllers;

public class Semafor {
    private int permits;

    // Constructor: Semaforu başlatıyoruz, kaç iş parçacığına izin verileceğini belirliyoruz
    public Semafor(int permits) {
        this.permits = permits;
    }

    // acquire() metodu: Eğer semafor izin veriyorsa, iş parçacığını başlatır.
    // Eğer izin yoksa, iş parçacığı beklemeye alınır.
    public synchronized void acquire() throws InterruptedException {
        while (permits <= 0) {
            wait(); // Eğer izin yoksa, bekleriz
        }
        permits--; // İzin alındı, bir permit azalır
    }

    // release() metodu: Bir iş parçacığı işlemi tamamladıktan sonra, semaforu serbest bırakır
    public synchronized void release() {
        permits++; // İzin geri verilir, permit sayısı artırılır
        notify(); // Eğer bekleyen iş parçacıkları varsa, birini uyandırır
    }
}
