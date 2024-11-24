package com.mmsk.book_social_network.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.mail.javamail.MimeMessageHelper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    //something to be aadded
@Async
    public void sendEmail(String to,String username,EmailTemplateName emailTemplateName , String confirmationLink, String activationCode,String Subject) throws MessagingException {
        String templateName ;
        if(emailTemplateName ==  null){
            templateName =  "confirmation-email";
        }else{
            templateName = emailTemplateName.name();
        }
        MimeMessage mimeMessage =  mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationLink", confirmationLink);
        properties.put("activationCode", activationCode);
        Context context = new Context();
        context.setVariables(properties);
        helper.setFrom("contact@aliboucoding.com");
        helper.setTo(to);
        helper.setSubject(Subject);
        helper.setText(templateEngine.process(templateName, context), true);
        mailSender.send(mimeMessage);
    }

}
