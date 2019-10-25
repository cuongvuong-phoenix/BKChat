package app.models;

import com.sun.org.glassfish.external.statistics.TimeStatistic;
import org.mariadb.jdbc.internal.com.send.parameters.TimestampParameter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Message {
    private int id;

    private Timestamp timeSent;

    private Timestamp timeRecv;

    private String content;

    private int userFromId;

    private int userToId;

    private User userFrom;

    private User userTo;

    public Message(int id, Timestamp timeSent, Timestamp timeRecv, String content, int userFromId, int userToId) {
        this.id = id;
        this.timeSent = timeSent;
        this.timeRecv = timeRecv;
        this.content = content;
        this.userFromId = userFromId;
        this.userToId = userToId;
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

    public int getuserFromId() {
        return userFromId;
    }

    public void setuserFromId(int userFromId) {
        this.userFromId = userFromId;
    }

    public int getuserToId() {
        return userToId;
    }

    public void setuserToId(int userToId) {
        this.userToId = userToId;
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
