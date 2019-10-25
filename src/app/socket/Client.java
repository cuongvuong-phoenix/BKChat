package app.socket;

import app.controllers.ChatRoomController;
import app.controllers.SigninController;
import app.controllers.SignupController;
import app.controllers.UserController;
import app.models.Message;
import app.models.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Client {
    private final String serverHost;
    private final int serverPort;
    private Socket socket;
    private OutputStream os;

    private List<PeerHandler> peerList = new ArrayList<>();
    private int enablePort = 9000;
    private InputStream is;
    private RequestHandler requestHandler;
    private RequestSender requestSender;
    private BufferedReader bufferedReader;
    // Primary Stage
    private Stage primaryStage;
    // Controllers
    private SigninController signinController = new SigninController(this);
    private SignupController signupController = new SignupController(this);

    private ChatRoomController chatRoomController = new ChatRoomController(this);
    // result message
    private String resultMessage;
    private User loggedUser;
    private List<User> userList = new ArrayList<>();
    private ObservableList<User> userObservableList = FXCollections.observableArrayList();

    public Client(String serverHost, int serverPort, Stage primaryStage) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.primaryStage = primaryStage;
    }

    public void start() {
        DataOutputStream writer = new DataOutputStream(os);
        DataInputStream reader = new DataInputStream(is);
        this.requestHandler = new RequestHandler(this, reader);
        this.requestSender = new RequestSender(this, writer);
        Thread readerThread = new Thread(requestHandler);
        Thread writerThread = new Thread(requestSender);
        readerThread.start();
        writerThread.start();

        userObservableList.addAll(userList);
    }

    public void loadSigninFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/signin.fxml"));
        loader.setController(signinController);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
    }

    public void loadSignupFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/signup.fxml"));
        loader.setController(signupController);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
    }

    public void loadChatroomFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/chatroom.fxml"));
        loader.setController(chatRoomController);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
    }

//    public static void main(String[] args) {
//        Client client = new Client("localhost", 9998);
//        if (!client.connect()) {
//            System.err.println("Connection failed");
//        } else {
//            System.out.println("Connection succeed");
//            client.start();
//        }
//    }

    public void addPeer(PeerHandler peer) {
        peerList.add(peer);
    }

    public void signin(String username, String password) throws IOException {
        String message = "login," + username + "," + password;
        requestSender.send(message);
    }

    public void signup(String username, String password, String nickname) {
        String message = "signup," + username + "," + password + "," + nickname;
        requestSender.send(message);
    }

    public void showUserList() {
        String message = "list";
        requestSender.send(message);
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverHost, serverPort);
            this.os = socket.getOutputStream();
            this.is = socket.getInputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*public void peer(int port, String cmd) {
        if("host".equalsIgnoreCase(cmd)) {
        }else if("listen".equalsIgnoreCase(cmd)) {
            Socket peerListen;
            try {
                peerListen = new Socket(serverHost,port);
                Thread peer = new Thread(new PeerHandler(this,peerListen));
                peer.start();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Unknonwn peer cmd");
        }
    }*/

    public void peerHost(User peerUser) throws IOException {
        ServerSocket peerHost;
        try {
            peerHost = new ServerSocket(enablePort);
            String msg = "agree " + String.valueOf(enablePort) + " " + peerUser.getUserName();
            enablePort += 1;
            requestSender.send(msg);
            System.out.println("???????");
            while (true) {
                try {
                    Socket peerListen = peerHost.accept();
                    Thread peerThread = new Thread(new PeerHandler(this, peerListen, peerUser));
                    peerThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            enablePort += 1;
            peerHost(peerUser);
        }
    }

    public void peerListen(int port, User peerUser) throws SQLException {
        Socket peerListen;
        try {
            peerListen = new Socket(serverHost, port);
            System.out.println("???????");
            Thread peerThread = new Thread(new PeerHandler(this, peerListen, peerUser));
            peerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PeerHandler> getPeerList() {
        synchronized (this) {
            return peerList;
        }
    }

    public void setPeerList(ArrayList<PeerHandler> peerList) {
        synchronized (this) {
            this.peerList = peerList;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public String getResultMessage() {
        synchronized (this) {
            return resultMessage;
        }
    }

    public void setResultMessage(String resultMessage) {
        synchronized (this) {
            this.resultMessage = resultMessage;
        }
    }

    public User getLoggedUser() {
        synchronized (this) {
            return loggedUser;
        }
    }

    public void setLoggedUser(User loggedUser) {
        synchronized (this) {
            this.loggedUser = loggedUser;
        }
    }

    public ObservableList<User> getUserObservableList() {
        synchronized (this) {
            return userObservableList;
        }
    }

    public void setUserObservableList(ObservableList<User> userObservableList) {
        synchronized (this) {
            this.userObservableList = userObservableList;
        }
    }

    public List<User> getUserList() {
        synchronized (this) {
            return userList;
        }
    }

    public void setUserList(List<User> userList) {
        synchronized (this) {
            this.userList = userList;
        }
    }

    public ChatRoomController getChatRoomController() {
        synchronized (this) {
            return chatRoomController;
        }
    }

    public void setChatRoomController(ChatRoomController chatRoomController) {
        synchronized (this) {
            this.chatRoomController = chatRoomController;
        }
    }
}
