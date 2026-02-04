package org.example.Listener;


import org.example.Entities.Products;
import org.example.Event.StockChangeEvent;
import org.example.Services.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class StockEventListener {

    public StockEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleStockChange(StockChangeEvent event) {
        Products product = event.getProduct();
        System.out.println("Event received");

        if(product.getProductStock() < 5) {
            emailService.sendLowStockAlert("charlizecmendoza@gmail.com", product);
        }
    }



}
