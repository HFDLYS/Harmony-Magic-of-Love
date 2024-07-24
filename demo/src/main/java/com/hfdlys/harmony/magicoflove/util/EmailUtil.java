package com.hfdlys.harmony.magicoflove.util;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

/**
 * 邮件工具类
 * @author Jiasheng Wang
 * @since 2024-07-23
 */
public class EmailUtil {
    public static boolean sendEmail(String to, String subject, String content) {
        // 收件人邮箱
        String toEmail = to;

        // 发件人邮箱
        String fromEmail = "H-LoM@outlook.com";
        String fromPassword = "hlom2336";

        // 设置邮件服务器
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // 获取Session对象
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        try {
            // 创建邮件对象
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content);

            // 发送邮件
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
        }

        return false;
    }
}
