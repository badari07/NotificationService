package com.example.notificationservice.configs;


import com.example.notificationservice.DTOs.SendEmailMsgDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
@AllArgsConstructor
public class kafkaConsumerConfig {

    private final ObjectMapper objectMapper;



    @KafkaListener(topics = "send-email", groupId = "notification-service-group")
    public void handleSendEmailEvent(String message) {
        // Logic to handle sending email based on the message
        System.out.println("Received message for sending email: " + message);
        // Add email sending logic here

        SendEmailMsgDTO emailMsgDTO = null;
        try {
            emailMsgDTO = objectMapper.readValue(message, SendEmailMsgDTO.class);

            System.out.println("Deserialized Email Message:");
            System.out.println("To: " + emailMsgDTO.getTo());
            System.out.println("From: " + emailMsgDTO.getFrom());
            System.out.println("Subject: " + emailMsgDTO.getSubject());
            System.out.println("Body: " + emailMsgDTO.getBody());

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("fromEmail", "password");
                }
            };
            Session session = Session.getInstance(props, auth);

            EmailUtil.sendEmail(session, emailMsgDTO.getTo(),emailMsgDTO.getSubject(), emailMsgDTO.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }

    }
}
