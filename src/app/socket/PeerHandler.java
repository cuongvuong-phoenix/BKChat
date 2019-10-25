package app.socket;

import app.models.Message;
import app.models.User;
import app.socket.MessageReciever;
import app.socket.MessageSender;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PeerHandler implements Runnable {

    private Socket peer;

    private Client client;
    private OutputStream os;
    private PrintWriter writer;
    private User peerUser;
    private MessageReciever messageReciever;
    private MessageSender messageSender;
    private List<Message> messageList = new ArrayList<>();
    private ObservableList<Message> messageObservableList = FXCollections.observableArrayList();

    public PeerHandler(Client client, Socket peerListen, User peerUser) {
        this.client = client;
        this.peer = peerListen;
        this.peerUser = peerUser;
    }

    @Override
    public void run() {
        messageObservableList.addListener((ListChangeListener<Message>) change -> {
            while (change.next()){

            }
        });

        try {
            this.os = peer.getOutputStream();
            this.writer = new PrintWriter(os);
            InputStream is = peer.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            messageReciever = new MessageReciever(this, reader);
            messageSender = new MessageSender(this, writer);
            Thread readerThread = new Thread(messageReciever);
            Thread writerThread = new Thread(messageSender);
            readerThread.start();
            writerThread.start();
        } catch (Exception e) {

        }
    }

    public void sendMessage(String content) {
        if (messageSender != null) {
            messageSender.send(content);
//            Message message = new Message(new Timestamp(System.currentTimeMillis()), content, client.getLoggedUser(), peerUser);
//            messageObservableList.add(message);
        }
    }

    public User getPeerUser() {
        return peerUser;
    }

    public void setPeerUser(User peerUser) {
        this.peerUser = peerUser;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ObservableList<Message> getMessageObservableList() {
        return messageObservableList;
    }

    public void setMessageObservableList(ObservableList<Message> messageObservableList) {
        this.messageObservableList = messageObservableList;
    }
}
