package org.example.Services;

import org.example.Entities.Products;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendLowStockAlert(String to, Products product) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Low stock alert: " + product.getProductName());
        message.setText("Product " + product.getProductName() + " is low on stock. Current quantity is: " + product.getProductStock());
        javaMailSender.send(message);
    }





}
