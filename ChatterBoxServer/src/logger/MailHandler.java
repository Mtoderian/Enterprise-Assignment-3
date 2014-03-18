package logger;

import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Handler for sending critical log records by mail.
 */
public class MailHandler extends Handler {

    private String from;
    private String[] to;
    private String server;
    private int port;
    private boolean auth;
    private boolean tls;
    private boolean ssl;
    private String username;
    private String password;

    /**
     * Constructor which initializes all fields
     *
     * @param from sender mail address
     * @param to receiver mail addresses
     * @param server server ip
     * @param port port of server
     * @param username username to log in on server
     * @param password password to log in on server
     * @param auth whether or not it's an authenticated login
     * @param tls whether or not tls is used
     * @param ssl whether or not ssl is used
     */
    public MailHandler(String from, String[] to, String server, int port, String username, String password, boolean auth, boolean tls, boolean ssl) {
        this.from = from;
        this.to = to;
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.tls = tls;
        this.ssl = ssl;
    }

    /**
     * sends the log record by mail if it is SEVERE
     */
    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().equals(Level.SEVERE)) {
            try {
                Properties prop = new Properties();
                prop.put("mail.smtp.host", server);
                prop.put("mail.smtp.port", port);
                prop.put("mail.smtp.auth", auth ? "true" : "false");
                prop.put("mail.smtp.starttls.enable", tls ? "true" : "false");
                prop.put("mail.smtp.ssl.enable", ssl ? "true" : "false");
                Authenticator au = null;
                if (auth) {
                    au = new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    };
                }
                Session session = Session.getInstance(prop, au);
                session.setDebug(true);
                Message mimeMsg = new MimeMessage(session);
                Address addressFrom = new InternetAddress(from);
                mimeMsg.setFrom(addressFrom);
                Address[] toAddr = new InternetAddress[to.length];
                for (int i = 0; i < to.length; i++) {
                    toAddr[i] = new InternetAddress(to[i]);
                }

                mimeMsg.setRecipients(Message.RecipientType.TO, toAddr);
                mimeMsg.setSubject("Server log");
                mimeMsg.setText(record.getMessage());
                Transport.send(mimeMsg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
