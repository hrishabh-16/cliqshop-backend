package com.cliqshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("CliQshop - Your OTP for " + purpose);
        
        String emailContent = String.format(
            "Dear User,\n\n" +
            "Your One-Time Password (OTP) for %s is: %s\n\n" +
            "This OTP will expire in 10 minutes.\n\n" +
            "If you did not request this OTP, please ignore this email.\n\n" +
            "Best regards,\n" +
            "CliQshop Team",
            purpose, otp
        );
        
        message.setText(emailContent);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("CliQshop - Password Reset Request");
        
        String emailContent = String.format(
            "Dear User,\n\n" +
            "We received a request to reset your password. Click the link below to reset your password:\n\n" +
            "%s\n\n" +
            "This link will expire in 30 minutes.\n\n" +
            "If you did not request a password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "CliQshop Team",
            resetLink
        );
        
        message.setText(emailContent);
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to CliQshop!");
        
        String emailContent = String.format(
            "Dear %s,\n\n" +
            "Welcome to CliQshop! We're excited to have you as a new member.\n\n" +
            "You can now browse our products, add items to your cart, and place orders.\n\n" +
            "If you have any questions or need assistance, please don't hesitate to contact our support team.\n\n" +
            "Best regards,\n" +
            "CliQshop Team",
            name
        );
        
        message.setText(emailContent);
        mailSender.send(message);
    }
}
