package uc3m.crypto.server.model;

import uc3m.crypto.security.SHA;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Message implements Serializable {
    private String sender;
    private String content;
    private Date dateSent;
    private String hmac;

    public Message(String sender, String content, Date dateSent) {
        this.sender = sender;
        this.content = content;
        this.dateSent = dateSent;
        this.hmac = getHmacString();
    }

    public Message(String message) {
        sender = message.substring(message.indexOf("sender") + 8,
                message.indexOf("'", message.indexOf("sender") + 8));
        content = message.substring(message.indexOf("content") + 9,
                message.indexOf("'", message.indexOf("content") + 9));
        String date = message.substring(message.indexOf("dateSent") + 10,
                message.indexOf("'", message.indexOf("dateSent") + 10));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateSent = dateFormatter.parse(date);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        hmac = message.substring(message.indexOf("hmac") + 6,
                message.indexOf("'", message.indexOf("hmac") + 6));
        if (!checkHmac()) throw new SecurityException("Message integrity compromised");
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", dateSent='" + formatter.format(dateSent) + '\'' +
                ", hmac='" + hmac + '\'' +
                '}';
    }

    public String toStringWithoutHmac() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", dateSent='" + formatter.format(dateSent) + '\'' +
                '}';
    }

    public String toUIString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return "(" + formatter.format(dateSent) + ")[" + sender + "]: " + content;
    }

    public String getHmacString() {
        return Base64.getEncoder().encodeToString(SHA.digest(this.toStringWithoutHmac()));
    }
    public boolean checkHmac() {
        return getHmacString().equals(hmac);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }
}
