package app.socket;

import app.models.Message;
import org.apache.commons.lang3.StringUtils;

import java.io.DataOutputStream;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

public class MessageSender implements Runnable {

    private DataOutputStream os;
    private PeerHandler peerHandler;
    private Queue<String> messageQueue = new LinkedList<>();

    public MessageSender(PeerHandler peerHandler, DataOutputStream os) {
        this.peerHandler = peerHandler;
        this.os = os;
    }

    @Override
    public void run() {
        try {
            Boolean isDisconnect = false;
            while (!isDisconnect) {
                synchronized (this) {
                    while (!messageQueue.isEmpty()) {
                        String message = messageQueue.remove();
                        System.out.println("Send :" + message);
                        String[] tokens = StringUtils.split(message, ',');
                        os.writeUTF(message);
                        os.flush();
                        if (message.equals("Disconnect")) {
                            isDisconnect = true;
                            break;
                        }
                        if (tokens[0].equals("Endfile")) {
                            Message messageObject = new Message(new Timestamp(System.currentTimeMillis()), "File: " + tokens[1], peerHandler.getClient().getLoggedUser(), peerHandler.getPeerUser());
                            peerHandler.getMessageObservableList().add(messageObject);
                        } else if (tokens[0].equals("Message")) {
                            Message messageObject = new Message(new Timestamp(System.currentTimeMillis()), message.substring(message.indexOf(",") + 1), peerHandler.getClient().getLoggedUser(), peerHandler.getPeerUser());
                            peerHandler.getMessageObservableList().add(messageObject);
                        } else {

                        }
                    }
                }
            }
            System.out.println("snder off");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        synchronized (this) {
            messageQueue.add(msg);
        }
    }
}
