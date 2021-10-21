package uc3m.crypto.server.model;

import java.io.Serializable;
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

    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", dateSent=" + dateSent +
                '}';
    }

}
