package emails;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import security.ServletContextHolder;

import java.io.*;
import java.util.Properties;

public class Mailer {

    private static String HOST;
    private static String EMAIL;
    private static String PASSWORD;
    private static String PATH;
    private static final Session SESSION;

    static {
        Properties prop = new Properties();
        PATH = System.getProperty("user.dir")+ "\\src\\main\\webapp";
        System.out.println(PATH);
        try (FileInputStream fis = new FileInputStream(PATH + "\\WEB-INF\\config\\mailer.config")) {
            prop.load(fis);
        } catch (IOException ignored) {
            PATH = ServletContextHolder.getServletContext().getRealPath("");
            String filename = ServletContextHolder.getServletContext().getRealPath("/WEB-INF/config/mailer.config");
            try (FileInputStream fis = new FileInputStream(filename)) {
                prop.load(fis);
            } catch (IOException ex) {
                System.out.println("IOException: Failed to open email.config file containing email account credentials.\n" + ex.getMessage());
            }
        }
        HOST = prop.getProperty("mailer.host");
        EMAIL = prop.getProperty("mailer.email");
        PASSWORD = prop.getProperty("mailer.password");

        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        SESSION = Session.getInstance(properties, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });
        // Used to debug SMTP issues
        SESSION.setDebug(false);
    }

    public static void sendEmail(String to, String subject, String HTMLContent) throws MessagingException {
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(SESSION);

        message.setFrom(new InternetAddress(EMAIL));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(HTMLContent,"text/html");

        // Create the multipart object and add the HTML body part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Set the multipart as the message's content
        message.setContent(multipart);

        System.out.println("sending email notification...");
        // Send message
        Transport.send(message);
        System.out.println("Sent message successfully....");
    }

    private static String loadHTMLFile(String path) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(path));
        String str;
        while ((str = in.readLine()) != null) {
            contentBuilder.append(str);
        }
        in.close();
        return contentBuilder.toString();
    }

    public static void sendPasswordResetEmail(String to, String resetLink) throws IOException, MessagingException {
        Document emailHtml = Jsoup.parse(loadHTMLFile(PATH + "\\email\\passwordResetNotification.html"));
        emailHtml.getElementById("reset-button-link").attr("href",resetLink);
        sendEmail(to, "Password Reset Request", emailHtml.html());
    }
}
