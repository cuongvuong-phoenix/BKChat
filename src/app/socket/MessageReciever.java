package app.socket;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import app.models.Message;
import app.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class MessageReciever implements Runnable {

    private DataInputStream is;
    private PeerHandler peerHandler;
    private StringBuilder fileStringBuilder = new StringBuilder();
    private String fileName;

    public MessageReciever(PeerHandler peerHandler, DataInputStream is) {
        this.is = is;
        this.peerHandler = peerHandler;
    }

    @Override
    public void run() {
        try {
            String line = null;
            String[] tokens = null;
            Boolean isDisconnect = false;
            while (!isDisconnect) {
                line = is.readUTF();
                System.out.println(line);
                tokens = StringUtils.split(line, ',');
                System.out.println(tokens[0]);
                if (tokens != null && tokens.length > 0) {
                    switch (tokens[0]) {
                        case "Message": {
                            String content = line.substring(line.indexOf(",") + 1);
                            Message messageObject = new Message(new Timestamp(System.currentTimeMillis()), content, peerHandler.getPeerUser(), peerHandler.getClient().getLoggedUser());
                            peerHandler.getMessageObservableList().add(messageObject);
                            break;
                        }
                        case "File": {
                            String content = line.substring(line.indexOf(",") + 1);
                            fileStringBuilder.append(content);
                            break;
                        }
                        case "Endfile": {
                            String content = line.substring(line.indexOf(",") + 1);
                            String finalcontent = content.substring(content.indexOf(",") + 1);
                            fileStringBuilder.append(finalcontent);
                            fileName = tokens[1];
                            Message messageObject = new Message(new Timestamp(System.currentTimeMillis()), "File :" + System.getProperty("user.dir") + "/" + fileName, peerHandler.getPeerUser(), peerHandler.getClient().getLoggedUser());
                            peerHandler.getMessageObservableList().add(messageObject);
                            Task<Void> writeFile = new FileTask(Base64.getDecoder().decode(fileStringBuilder.toString()), System.getProperty("user.dir"), fileName);
                            Thread fileThread = new Thread(writeFile);
                            fileThread.setDaemon(true);
                            fileThread.start();
                            fileStringBuilder = new StringBuilder();
                            break;
                        }
                        case "Disconnect": {
                            isDisconnect = true;
                            break;
                        }
                    }
                }
                System.out.println("Recv : " + line);
            }
        } catch (Exception e) {
            try {
                peerHandler.getPeer().close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
