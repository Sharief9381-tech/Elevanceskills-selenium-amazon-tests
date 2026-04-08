import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * Sends a Gmail price-drop alert via SMTP SSL.
 *
 * To enable:
 *   1. Go to myaccount.google.com/apppasswords
 *   2. Create an App Password for "Mail"
 *   3. Use that 16-char code as APP_PASSWORD below
 */
public class EmailNotifier {

    private final String senderEmail;
    private final String appPassword;
    private final String receiverEmail;

    public EmailNotifier(String senderEmail, String appPassword, String receiverEmail) {
        this.senderEmail   = senderEmail;
        this.appPassword   = appPassword;
        this.receiverEmail = receiverEmail;
    }

    public void sendAlert(String productTitle, String productUrl,
                          double currentPrice, double threshold) {
        Properties props = new Properties();
        props.put("mail.smtp.host",          "smtp.gmail.com");
        props.put("mail.smtp.port",          "465");
        props.put("mail.smtp.ssl.enable",    "true");
        props.put("mail.smtp.auth",          "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            msg.setSubject(String.format("Price Alert: '%s' dropped to Rs.%.0f",
                productTitle.substring(0, Math.min(40, productTitle.length())), currentPrice));

            String body = String.format(
                "Price Drop Alert!\n\n"
                + "Product : %s\n"
                + "Price   : Rs.%.0f\n"
                + "Target  : Rs.%.0f\n"
                + "URL     : %s\n\n"
                + "Act fast before the price goes back up!",
                productTitle, currentPrice, threshold, productUrl);

            msg.setText(body);
            Transport.send(msg);
            System.out.println("Email alert sent to: " + receiverEmail);

        } catch (MessagingException e) {
            System.out.println("Email send failed: " + e.getMessage());
            System.out.println("(Set a Gmail App Password at myaccount.google.com/apppasswords)");
        }
    }
}
