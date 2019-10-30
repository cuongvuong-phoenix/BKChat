package app.models;

import java.sql.Timestamp;

public class Message {
    private int id;

    private Timestamp timeSent;

    private Timestamp timeRecv;

    private String content;

    private String userFrom_name;

    private String userTo_name;

    private User userFrom;

    private User userTo;

    public Message(int id, Timestamp timeSent, Timestamp timeRecv, String content, String userFrom_name, String userTo_name) {
        this.id = id;
        this.timeSent = timeSent;
        this.timeRecv = timeRecv;
        this.content = content;
        this.userFrom_name = userFrom_name;
        this.userTo_name = userTo_name;
    }

    public Message(int id, Timestamp timeSent, Timestamp timeRecv, String content, User userFrom, User userTo) {
        this.id = id;
        this.timeSent = timeSent;
        this.timeRecv = timeRecv;
        this.content = content;
        this.userFrom = userFrom;
        this.userTo = userTo;
    }

    public Message(Timestamp timeSent, String content, User userFrom, User userTo) {
        this.timeSent = timeSent;
        this.content = content;
        this.userFrom = userFrom;
        this.userTo = userTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Timestamp timeSent) {
        this.timeSent = timeSent;
    }

    public Timestamp getTimeRecv() {
        return timeRecv;
    }

    public void setTimeRecv(Timestamp timeRecv) {
        this.timeRecv = timeRecv;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserFrom_name() {
        return userFrom_name;
    }

    public void setUserFrom_name(String userFrom_name) {
        this.userFrom_name = userFrom_name;
    }

    public String getUserTo_name() {
        return userTo_name;
    }

    public void setUserTo_name(String userTo_name) {
        this.userTo_name = userTo_name;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }
}
