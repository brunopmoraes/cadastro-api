package br.com.lagoinha.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailSender {
    public static void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
        // Configurações do servidor SMTP
        String host = "smtp.gmail.com"; // Por exemplo, smtp.gmail.com para Gmail
        final String user = "certificado.start@gmail.com"; // O seu email
        final String password = "vcyz xdqq dpje actv"; // A sua senha de email

        // Configuração de propriedades
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // Para TLS: 587, para SSL: 465

        // Criação da sessão
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // Criar uma mensagem
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Parte do corpo do e-mail
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Criação da parte de anexo
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(attachmentPath));

            // Criação da Multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            // Adicionando o multipart à mensagem
            message.setContent(multipart);

            // Enviar a mensagem
            Transport.send(message);

            System.out.println("E-mail enviado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
