package main;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Random;

public class SendMail {

    private static final String USER_NAME = "galleryartix@gmail.com";  // GMail user name (just the part before "@gmail.com")
    private static final String PASSWORD = "abood123"; // GMail password
    private static final String subject = "Thank You for joining Artix!";
    public static int passkey;
    private static Random random = new Random();


    public static int sendMail(String RECIPIENT) {
        String host = "smtp.gmail.com";
       Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", USER_NAME);
        props.put("mail.smtp.password", PASSWORD);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        int OP = random.nextInt(900000) + 100000;

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(USER_NAME));
            InternetAddress toAddress = new InternetAddress();
            toAddress = new InternetAddress(RECIPIENT);
            message.addRecipient(Message.RecipientType.TO, toAddress);

            message.setSubject(subject);
            message.setText(String.format("To verify your email address, please use the following One Time Password:\n" +
                    "\n" +
                    "%6d\n" +
                    "\n" +
                    "Do not share this OTP with anyone. Artix takes your account security very seriously. Artix will never ask you to disclose or verify your Artix password, OTP, credit card, or banking account number.\n" +
                    "\n" +
                    "Thank you for shopping with us! We hope to see you again soon.", OP));
            Transport transport = session.getTransport("smtp");
            transport.connect(host, USER_NAME, PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        return OP;

    }
}