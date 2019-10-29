package app.socket;

import app.controllers.DBController;
import app.controllers.UserController;
import app.models.User;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket client;
    private Boolean isLoggedIn = false;
    private Server server;
    private DataOutputStream os;
    private User user = null;
    private DataInputStream is;

    public ClientHandler(Server server, Socket client) {
        this.client = client;
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            this.os = new DataOutputStream(client.getOutputStream());
            this.is = new DataInputStream(client.getInputStream());

            String line = null;
            String[] tokens = null;
            while (true) {
                line = is.readUTF();
                System.out.println("Client : " + line);
                tokens = StringUtils.split(line, ',');
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    switch (cmd) {
                        case "login":
                            handleLogin(os, tokens);
                            break;
                        case "signup":
                            handleSignup(os, tokens);
                            break;
                        case "list":
                            handleShowList(os);
                            break;
                        case "connect":
                            handleConnect(os, tokens);
                            break;
                        case "agree":
                            handleAgree(os, tokens);
                            break;
                        case "logout":
                            handleLogout(os, tokens);
                            break;
                        case "friend":
                            handleAddFriend(os, tokens);
                        default:
                            System.out.println("Unknown " + cmd);
                    }
                }
                os.flush();
            }
		/*writer.write("end");
		writer.close();
		reader.close();
		client.close();*/
        } catch (Exception e) {

        }
    }

    private void handleAgree(DataOutputStream writer, String[] tokens) throws IOException {
        String port = tokens[1];
        String fromUser = tokens[2];
        String toUser = tokens[3];
        List<ClientHandler> clientList = server.getClientList();
        for (ClientHandler clientOne : clientList) {
            if (clientOne.getUser().getUserName().equals(fromUser)) {
                //writer.write("host 9000");
                clientOne.send("Listen," + port + "," + toUser + "," + this.client.getInetAddress().toString().substring(1));
            }
        }

    }

    private void handleLogin(DataOutputStream writer, String[] tokens) throws SQLException, IOException {

        if (tokens.length == 3) {
            String username = tokens[1];
            String password = tokens[2];
            User user = UserController.getInstance().getUser(username);
            String signinQuery = "SELECT * FROM tbl_user WHERE userName = ? AND userPassword = ?";
            ResultSet resultSet = DBController.getInstance().ExecQuery(signinQuery, username, password);
            Boolean isLogged = false;
            ClientHandler loggedUser = null;
            List<ClientHandler> clientList = server.getClientList();
            for (ClientHandler client : clientList) {
                if (client.getUser().getUserName().equals(username)) {
                    isLogged = true;
                    loggedUser = client;
                    break;
                }
            }
            if (resultSet.next() && (isLogged.equals(false))) {
                this.user = user;
                this.isLoggedIn = true;
                String msg = "Login,Success," + user.getUserName();
                writer.writeUTF(msg);
                server.addUser(this);
                for (ClientHandler client : clientList) {
                    client.handleShowList(client.getOs());
                }
            } else {
                String msg = "Login,Failed";
                writer.writeUTF(msg);
            }
        }
    }

    private void handleSignup(DataOutputStream writer, String[] tokens) throws IOException {
        if (tokens.length == 4) {
            String username = tokens[1];
            String password = tokens[2];
            String nickname = tokens[3];

            String callQuery = "CALL USP_Signup (?,?,?)";

            int resultUpdate = DBController.getInstance().ExecUpdate(callQuery, username, password, nickname);

            if (resultUpdate > 0) {
                String msg = "Signup,Success";
                writer.writeUTF(msg);
            } else {
                String msg = "Signup,Failed";
                writer.writeUTF(msg);
            }
        }
    }

    private void handleAddFriend(DataOutputStream writer, String[] tokens) throws IOException, SQLException {
        if (tokens.length == 3) {
            String user1 = tokens[1];
            String user2 = tokens[2];

            String addFriendQuery = "CALL BKChat.USP_AddFriend(?, ?)";
            int resultUpdate = DBController.getInstance().ExecUpdate(addFriendQuery, user1, user2);

            if (resultUpdate > 0) {
                String msg = "Friend,Success";
                writer.writeUTF(msg);

                List<ClientHandler> clientList = server.getClientList();
                for (ClientHandler client : clientList) {
                    client.handleShowList(client.getOs());
                }
            } else {
                String msg = "Friend,Failed";
                writer.writeUTF(msg);
            }
        }
    }

    public void handleShowList(DataOutputStream writer) throws IOException, SQLException {
        List<String> friendList = new ArrayList<>();
        if (user != null) {
            String queryFriendList = "SELECT * FROM BKChat.tbl_userFriend WHERE user1 = ?";
            ResultSet resultSet = DBController.getInstance().ExecQuery(queryFriendList, user.getUserName());
            while (resultSet.next()) {
                String friend = resultSet.getString(2);
                friendList.add(friend);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UserList");
        List<ClientHandler> clientList = server.getClientList();
        for (ClientHandler client : clientList) {
            if (friendList.indexOf(client.getUser().getUserName()) != -1) {
                stringBuilder.append(",");
                stringBuilder.append(client.getUser().getUserName());
                friendList.remove(client.getUser().getUserName());
            }
        }
        for (String offlineFriend : friendList) {
            stringBuilder.append(",$");
            stringBuilder.append(offlineFriend);
        }
//        System.out.println(stringBuilder.toString());
        this.send(stringBuilder.toString());
    }

    private void handleLogout(DataOutputStream writer, String[] tokens) throws IOException, SQLException {
        this.user = null;
        this.isLoggedIn = false;
        server.removeUser(this);

        List<ClientHandler> clientList = server.getClientList();
        for (ClientHandler client : clientList) {
            client.handleShowList(client.getOs());
        }
    }

    private void handleConnect(DataOutputStream writer, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String toUser = tokens[2];
            String fromUser = tokens[1];
            List<ClientHandler> clientList = server.getClientList();
            ClientHandler res = null;
            for (ClientHandler clientOne : clientList) {
                if (clientOne.getUser().getUserName().equals(toUser)) {
                    //writer.write("host 9000");
                    System.out.println(clientOne.getUser().getUserName());
                    clientOne.send("Connect," + fromUser + "," + toUser);
                    res = clientOne;
                }
            }
            if (res == null) {
                writer.writeUTF("User is offline!");
            }
        } else {
            String msg = "error";
            writer.writeUTF(msg);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void send(String req) throws IOException {
        System.out.println(req);
        os.writeUTF(req);
    }

    public DataOutputStream getOs() {
        return os;
    }

}
