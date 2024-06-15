//package fr.utc.sr03.chat_admin.service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//
//@Service
//public class EmailSend {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendConfirmationEmail(String to, String subject, String text) {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//
//        try {
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text, true); // true indicates HTML content
//
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            // Handle the exception appropriately
//        }
//    }
//}

