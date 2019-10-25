package app.socket;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import app.controllers.DBController;
import app.controllers.UserController;
import app.models.User;
import org.apache.commons.lang3.StringUtils;

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
                    if ("quit".equalsIgnoreCase(cmd)) {
                        break;
                    } else if ("login".equalsIgnoreCase(cmd)) {
                        handleLogin(os, tokens);
                    } else if ("signup".equals(cmd)) {
                        handleSignup(os, tokens);
                    } else if ("list".equalsIgnoreCase(cmd)) {
                        handleShowList(os);
                    } else if ("connect".equalsIgnoreCase(cmd)) {
                        handleConnect(os, tokens);
                    } else if ("logout".equalsIgnoreCase(cmd)) {
                        handleLogout(os, tokens);
                    } else if ("agree".equalsIgnoreCase(cmd)) {
                        handleAgree(os, tokens);
                    } else {
                        String msg = "unknown" + cmd + "\n";
                        os.writeUTF(msg);
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
        String toUser = tokens[2];
        List<ClientHandler> clientList = server.getClientList();
        for (ClientHandler clientOne : clientList) {
            if (clientOne.getUser().equals(toUser)) {
                //writer.write("host 9000");
                clientOne.send("listen " + port + " " + toUser);
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
            List<ClientHandler> clientList = server.getClientList();
            for (ClientHandler client : clientList) {
                if (client.getUser().getUserName().equals(username)) {
                    isLogged = true;
                    break;
                }
            }
            if (resultSet.next() && (isLogged.equals(false))) {
                this.user = user;
                this.isLoggedIn = true;
                String msg = "Login,Success," + user.getUserName() + "," + user.getUserNickname();
                writer.writeUTF(msg);
                server.addUser(this);
                handleShowList(writer);
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

    private void handleShowList(DataOutputStream writer) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UserList");
        List<ClientHandler> clientList = server.getClientList();
        for (ClientHandler client : clientList) {
            stringBuilder.append(",");
            stringBuilder.append(client.getUser().getUserName());
        }
        System.out.println(clientList.size());
        for (ClientHandler client : clientList) {
            client.send(stringBuilder.toString());
        }
    }

    private void handleLogout(DataOutputStream writer, String[] tokens) throws IOException {
        this.user = null;
        this.isLoggedIn = false;
        server.removeUser(this);
        client.close();
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
                    clientOne.send("connection " + fromUser + " " + toUser);
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

    public void send(String req) throws IOException {
        System.out.println(req);
        os.writeUTF(req);
    }
}
