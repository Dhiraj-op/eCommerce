package com.example.eCommerce.service;


import com.example.eCommerce.model.Order;
import com.example.eCommerce.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("spring.mail.username")
    private String fromEmail;

    public void sendOrderConformation(Order order){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order Conform");
        message.setText("Your Order has been conformed, Order ID: "+ order.getId());
        mailSender.send(message);
    }

    public void sendConfirmationCode(User user){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Confirm for mail");
        message.setText("please confirm your mail by entering this code: "+ user.getConformationCode());
        mailSender.send(message);
    }
}
