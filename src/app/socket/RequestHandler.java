package app.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import app.controllers.ChatRoomController;
import app.controllers.UserController;
import app.models.User;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class RequestHandler implements Runnable {

    private DataInputStream is;
    private Client client;
    private ObservableList<User> userObservableList = FXCollections.observableArrayList();

    public RequestHandler(Client client, DataInputStream is) {
        this.is = is;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            String line = null;
            String[] tokens = null;
            while (true) {
                line = is.readUTF();
                System.out.println("server:" + line);
                tokens = StringUtils.split(line, ',');
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("quit".equalsIgnoreCase(cmd)) {
                        break;
                    } else if (cmd.equals("Login")) {
                        String[] finalTokens = tokens;
                        client.setResultMessage(finalTokens[1]);

                        if (finalTokens[1].equals("Success")) {
                            client.setLoggedUser(new User(finalTokens[2], finalTokens[3]));
                        } else {
                            client.setLoggedUser(null);
                        }
                    } else if (cmd.equals("Signup")) {
                        client.setResultMessage(tokens[1]);
                    } else if (cmd.equals("Logout")) {
                        break;
                    } else if (cmd.equals("UserList")) {
                        List<User> userList = new ArrayList<>();
                        String[] usernameList = ArrayUtils.remove(tokens, 0);
                        for (String username : usernameList) {
                            if (!client.getLoggedUser().getUserName().equals(username)) {
                                User user = new User(username);
                                userList.add(user);
                            }
                        }
//                        client.setUserList(userList);
                        // Task<Void> updateUserList = new UpdateUserListWorker(client,userObservableList);
                        // ((UpdateUserListWorker) updateUserList).call();//client.setThreadUserObservableList(userObservableList);
                        Platform.runLater(() -> {
                            userObservableList.setAll(userList);
                            client.setUserObservableList(userObservableList);
                        });
                    } else if ("Connect".equalsIgnoreCase(cmd)) {
                        handleConnect(client, tokens);
                    } else if ("Listen".equalsIgnoreCase(cmd)) {
                        handleListen(client, tokens);
                    } else {
                        String msg = "unknown " + cmd;
                        System.out.println(msg);
                    }
                }
            }

        } catch (Exception e) {

        }

    }

    private void handleConnect(Client client, String[] tokens) throws SQLException {
        String peer = tokens[1];
        User peerUser = UserController.getInstance().getUser(peer);
        try {
            client.peerHost(peerUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleListen(Client client, String[] tokens) throws SQLException {
        int port = Integer.parseInt(tokens[1]);
        String host = tokens[3];
        String peer = tokens[2];
        User peerUser = UserController.getInstance().getUser(peer);
        client.peerListen(port, peerUser, host);
    }
}
