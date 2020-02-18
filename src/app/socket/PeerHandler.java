package app.socket;

import app.models.Message;
import app.models.User;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class PeerHandler implements Runnable {
    private Socket peer;
    private Client client;
    private DataOutputStream os;
    private DataInputStream is;
    private User peerUser;
    private MessageReciever messageReciever;
    private MessageSender messageSender;
    private List<Message> messageList = new ArrayList<>();
    private ObservableList<Message> messageObservableList;

    public PeerHandler(Client client, Socket peerListen, User peerUser, ObservableList<Message> messageObservableList) {
        this.client = client;
        this.peer = peerListen;
        this.peerUser = peerUser;
        this.messageObservableList = messageObservableList;
    }

    @Override
    public void run() {

        messageObservableList.addListener((ListChangeListener<Message>) change -> {
            while (change.next()) {
                Platform.runLater(() -> {
                    for (Map.Entry<PeerHandler, ObservableList<Message>> entry : client.mapPeerMessageList.entrySet()) {
                        if (entry.getKey().equals(this)) {
                            entry.setValue(messageObservableList);
                        }
                    }
                });
            }
        });

        try {
            this.os = new DataOutputStream(peer.getOutputStream());
            this.is = new DataInputStream(peer.getInputStream());
            this.messageReciever = new MessageReciever(this, is);
            this.messageSender = new MessageSender(this, os);
            Thread peerReaderThread = new Thread(messageReciever);
            Thread peerWriterThread = new Thread(messageSender);
            peerReaderThread.start();
            peerWriterThread.start();

        } catch (Exception e) {

        }
    }

    public void closeMessage() {
        messageSender.isDisconnect = true;
        messageReciever.isDisconnect =true;
        Platform.runLater(() -> {
            client.resetChatPane();
        });
    }

    public void sendMessage(String content) {
        messageSender.send("Message," + content);
//            Message message = new Message(new Timestamp(System.currentTimeMillis()), content, client.getLoggedUser(), peerUser);
//            messageObservableList.add(message);

    }
    public void disconnectMessage(){
        messageSender.send("Disconnect,"+ this.client.getLoggedUser().getUserName());
    }
    public void sendFile(String path, String fileName) {
        Task<Void> sendfile = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (InputStream in = new BufferedInputStream(new FileInputStream(path))) {
                    int len;
                    byte[] temp = new byte[12345];
                    while (((len = in.read(temp)) > 0)) {
                        if (len < 12345) {
                            byte[] extra;
                            extra = Arrays.copyOf(temp, len);
                            messageSender.send("Endfile," + fileName + "," + Base64.getEncoder().encodeToString(extra));
                        } else {
                            messageSender.send("File," + Base64.getEncoder().encodeToString(temp));
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        Thread th = new Thread(sendfile);
        th.setDaemon(true);
        th.start();

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

    public Socket getPeer() {
        synchronized (this) {
            return peer;
        }

    }

    public void setPeer(Socket peer) {
        synchronized (this) {
            this.peer = peer;
        }

    }
}
