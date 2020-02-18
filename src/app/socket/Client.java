package app.socket;

import app.controllers.ChatRoomController;
import app.controllers.SigninController;
import app.controllers.SignupController;
import app.models.Message;
import app.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private final String serverHost;
    private final int serverPort;
    private Socket socket;
    private OutputStream os;

    public ObservableList<PeerHandler> peerList = FXCollections.observableArrayList();
    private int enablePort = 9000;
    private InputStream is;
    private RequestHandler requestHandler;
    private RequestSender requestSender;
    private BufferedReader bufferedReader;
    private Thread readerThread;
    private Thread writerThread;
    // Primary Stage
    private Stage primaryStage;
    // Controllers
    private SigninController signinController = new SigninController(this);
    private SignupController signupController = new SignupController(this);

    private ChatRoomController chatRoomController = new ChatRoomController(this);
    // result message
    private String resultMessage;
    private StringProperty addFriendResult = new SimpleStringProperty();
    private StringProperty acceptFriend = new SimpleStringProperty();

    private User loggedUser;
    private List<User> userList = new ArrayList<>();
    private ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private ObservableList<User> threadUserObservableList = FXCollections.observableArrayList();

    public ObservableMap<PeerHandler, ObservableList<Message>> mapPeerMessageList = FXCollections.observableHashMap();

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
        readerThread = new Thread(requestHandler);
        writerThread = new Thread(requestSender);
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

    public void connectToPeer(String fromUser, String toUser) {
        String message = "connect," + fromUser + "," + toUser;
        requestSender.send(message);
    }

    public void friendResponse(Boolean res, String fromUser, String toUser) {
        if (res) {
            String message = "friendAccept," + "agree," + fromUser + "," + toUser;
            requestSender.send(message);
        } else {
            String message = "friendAccept," + "disagree," + fromUser + "," + toUser;
            requestSender.send(message);
        }
    }

    public void logout() throws IOException {
        for (PeerHandler peerHandler : peerList) {
            peerHandler.disconnectMessage();
            //peerHandler.getPeer().close();
        }
        requestSender.send("logout");
        loggedUser = null;
        peerList = FXCollections.observableArrayList();
        mapPeerMessageList = FXCollections.observableHashMap();
    }

    public void addFriend(String friend) {
        requestSender.send("friend," + loggedUser.getUserName() + "," + friend);
    }

    public boolean connect() {
        try {
            this.socket = new Socket();
//            this.socket.setSoTimeout(5000);
            this.socket.connect(new InetSocketAddress(serverHost, serverPort), 3000);
            this.os = socket.getOutputStream();
            this.is = socket.getInputStream();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateMessageList(PeerHandler peerHandler, ObservableList<Message> messageObservableList) {

    }

    public void resetChatPane() {
        chatRoomController.resetChatPane();
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
            boolean isPeerExists = false;
            for (PeerHandler peerHandler : peerList) {
                if (peerUser.getUserName().equals(peerHandler.getPeerUser().getUserName())) {
                    isPeerExists = true;
                    break;
                }
            }
            if (isPeerExists) {
                return;
            }

            peerHost = new ServerSocket(enablePort);
            String msg = "agree," + String.valueOf(enablePort) + "," + peerUser.getUserName() + "," + loggedUser.getUserName();
            enablePort += 1;
            requestSender.send(msg);

            try {
                ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
                Socket peerListen = peerHost.accept();
                PeerHandler peerHandler = new PeerHandler(this, peerListen, peerUser, messageObservableList);
                Thread peerThread = new Thread(peerHandler);
                peerThread.start();
                mapPeerMessageList.put(peerHandler, messageObservableList);
                peerList.add(peerHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (SocketException e) {
            enablePort += 1;
            peerHost(peerUser);
        }
    }

    public void peerListen(int port, User peerUser, String host) throws SQLException {
        Socket peerListen;
        try {
            boolean isPeerExists = false;
            for (PeerHandler peerHandler : peerList) {
                if (peerUser.getUserName().equals(peerHandler.getPeerUser().getUserName())) {
                    isPeerExists = true;
                    break;
                }
            }

            if (!isPeerExists) {
                ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
                peerListen = new Socket(host, port);
                PeerHandler peerHandlerSuccess = new PeerHandler(this, peerListen, peerUser, messageObservableList);
                Thread peerThread = new Thread(peerHandlerSuccess);
                peerThread.start();
                mapPeerMessageList.put(peerHandlerSuccess, messageObservableList);
                peerList.add(peerHandlerSuccess);
            }
        } catch (IOException e) {
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

    public void update() {
        userObservableList = threadUserObservableList;
    }

    public ObservableList<User> getThreadUserObservableList() {
        synchronized (this) {
            return threadUserObservableList;
        }
    }

    public void setThreadUserObservableList(ObservableList<User> threadUserObservableList) {
        synchronized (this) {
            this.threadUserObservableList = threadUserObservableList;
        }
    }

    public String getAddFriendResult() {
        return addFriendResult.get();
    }

    public StringProperty addFriendResultProperty() {
        return addFriendResult;
    }

    public void setAddFriendResult(String addFriendResult) {
        this.addFriendResult.set(addFriendResult);
    }

    public String getAcceptFriend() {
        return acceptFriend.get();
    }

    public StringProperty acceptFriendProperty() {
        return acceptFriend;
    }

    public void setAcceptFriend(String acceptFriend) {
        this.acceptFriend.set(acceptFriend);
    }
}
