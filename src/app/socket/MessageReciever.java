package app.socket;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

import app.models.Message;
import app.models.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class MessageReciever implements Runnable {

    private PeerHandler peerHandler;
    private BufferedReader reader;
    private List<EventHandler<ActionEvent>> eventHandlerList;

    public void addEventHandler(EventHandler<ActionEvent> event) {
        eventHandlerList.add(event);
    }

    public MessageReciever(PeerHandler peerHandler, BufferedReader reader) {
        this.peerHandler = peerHandler;
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String line = null;
            String[] tokens = null;
            while (true) {
                line = reader.readLine();
                tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];

                    if (cmd.equals("Message")) {
                        String[] tokensMessage = ArrayUtils.remove(tokens, 1);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String token : tokensMessage) {
                            stringBuilder.append(token);
                            stringBuilder.append(" ");
                        }
//                        Message message = new Message(new Timestamp(System.currentTimeMillis()), stringBuilder.toString(), peerHandler.getPeerUser(), peerHandler.getClient().getLoggedUser());
//                        List<Message> messageList = peerHandler.getMessageList();
//                        messageList.add(message);
                        ObservableList<Message> messageObservableList = peerHandler.getMessageObservableList();
//                        messageObservableList.add(message);
                        peerHandler.setMessageObservableList(messageObservableList);
                        // Add listener
                    }
                }
                System.out.println("User : " + line);
            }
        } catch (Exception e) {

        }
    }
}
