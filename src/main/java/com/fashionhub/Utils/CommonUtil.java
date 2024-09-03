package com.fashionhub.Utils;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {
    @Autowired
    private  JavaMailSender mailSender;
    
    public  Boolean sendMail(String url,String recipientEmail) throws UnsupportedEncodingException, MessagingException{
         MimeMessage message = mailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message);
         helper.setFrom("mahajanharsh2601@gmail.com","Fashion Hub");
         helper.setTo(recipientEmail);
         String content = "<p>Hello,</p>"+"<p>You have requested to reset Password</P>"
         +"<p>Click the link below to change your password </p>"+"<p> <a href=\"" + url + "\">Change my Password </a></p>";
         helper.setSubject("Password Reset");
         helper.setText(content,true);
         mailSender.send(message);
         return true;
    }

    public static String geneateURL(HttpServletRequest request) {
        String siteUrl= request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(),""); 
    }    
}
