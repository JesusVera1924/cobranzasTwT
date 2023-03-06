package com.twt.appcobranzas.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Devecua on 21/07/2016.
 */
public class SendMail {
    private String user_smtp;
    private String pwd_smtp;
    private String from;
    private String to;
    private String subject;
    private String text;
    private static InternetAddress[] addressTo;
    private static InternetAddress[] addressCc;

    public SendMail() {
    }

    public SendMail(String user_smtp, String pwd_smtp) {
        this.user_smtp = user_smtp;
        this.pwd_smtp = pwd_smtp;
    }


    public static boolean send(String to, String cc, String subject, String body, String body2) {
        boolean ok = false;
        /*String MAIL_SERVER = "smtp.gmail.com";
        String MAIL_PORT = "465";*/
        String MAIL_SERVER = "mail.cojapan.com.ec";
        String MAIL_PORT = "465";

        String USER_SMTP = "noreply@cojapan.com.ec";
        String PWD_SMTP = "noreply159";


        misDestinatarios(to);
        misDesticopias(cc);

        Properties props = new Properties();
        props.put("mail.smtp.host", MAIL_SERVER);
        props.put("mail.smtp.port", MAIL_PORT);
        props.put("mail.debug", "false");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        Authenticator auth = new SMTPAuthenticator(USER_SMTP, PWD_SMTP);
        Session mailSession = Session.getDefaultInstance(props, auth);
        try {

            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(USER_SMTP));
            message.setRecipients(Message.RecipientType.TO, addressTo);
            message.setRecipients(Message.RecipientType.CC, addressCc);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(body);
            message.setText(body2);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent("<table border=\"2px\">" +
                    "<caption><b>Lista de Pagos a Documentos</b></caption><br/>" +
                    "<tr>" +
                    "<td bgcolor=\"#61c5b6\">Documentos</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td>" + body2 + "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td bgcolor=\"#61c5b6\">Detalle de Pagos</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td>" + body + "</td>" +
                    "</tr>" +
                    "</table>", "text/html; charset=utf-8");

            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(body);


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            ok = true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sendTexto ...." + e.getMessage());
            ok = false;
        } finally {
            return ok;
        }

    }

    //Destinatarios
    public static void misDestinatarios(String mails){
        String[] tmp =mails.split(",");
        addressTo = new InternetAddress[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            try {
                addressTo[i] = new InternetAddress(tmp[i]);
            } catch (AddressException ex) {
                System.out.println(ex);
            }
        }
    }

    public static void misDesticopias(String mails){
        String[] tmp =mails.split(",");
        addressCc = new InternetAddress[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            try {
                addressCc[i] = new InternetAddress(tmp[i]);
            } catch (AddressException ex) {
                System.out.println(ex);
            }
        }
    }

}
