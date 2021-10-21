package uc3m.crypto.server.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private String sender;
    private String content;
    private Date dateSent;
    public Message(String sender, String content, Date dateSent) {
        this.sender = sender;
        this.content = content;
        this.dateSent = dateSent;
    }

    public Message(String message) {
        sender = message.substring(message.indexOf("sender")+8,
                message.indexOf("'", message.indexOf("sender")+8));
        content = message.substring(message.indexOf("content")+9,
                message.indexOf("'", message.indexOf("content")+9));
        String date = message.substring(message.indexOf("dateSent")+10,
                message.indexOf("'", message.indexOf("dateSent")+10));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateSent = dateFormatter.parse(date);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String toString() {
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

}
