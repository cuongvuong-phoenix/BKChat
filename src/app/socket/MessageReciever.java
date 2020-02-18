package app.socket;

import app.models.Message;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.sql.Timestamp;
import java.util.Base64;

public class MessageReciever implements Runnable {

    public boolean isDisconnect = false;
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
            while (!isDisconnect) {
                line = is.readUTF();
                System.out.println(line);
                tokens = StringUtils.split(line, ',');
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
                            System.out.println(content);
                            String finalcontent = content.substring(content.indexOf(",") + 1);
                            System.out.println(finalcontent);
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
                            peerHandler.getClient().peerList.remove(peerHandler);
                            peerHandler.getClient().mapPeerMessageList.remove(peerHandler);
                            //peerHandler.disconnectMessage();
                            peerHandler.closeMessage();
                            peerHandler.getPeer().close();
                            break;
                        }
                    }
                }
                System.out.println("Recv : " + line);
            }
            System.out.println("End");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
