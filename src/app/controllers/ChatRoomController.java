package app.controllers;

import app.models.Message;
import app.models.User;
import app.socket.Client;
import app.socket.PeerHandler;
import app.views.ChatPane;
import app.views.UserListViewCell;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatRoomController implements Initializable {
    private User loggedUser;

    private User choosenUser = null;

    private Client client;

    private PeerHandler peerHandler;

    private ObservableList<Message> messageObservableList = FXCollections.observableArrayList();

    public ObservableList<User> userObservableList = FXCollections.observableArrayList();

    private User selectedUser;

    private User oldUser;

    @FXML
    private Label lbl_listUser;

    @FXML
    private ListView<User> lv_UserList;

    @FXML
    private StackPane stack_ChatPane;

    @FXML
    private Label lb_ChatUser;

    @FXML
    private ScrollPane sp_ChatBox;

    @FXML
    private VBox vb_ChatBox;

    @FXML
    private MaterialDesignIconView btn_ChooseFile;

    @FXML
    private TextArea ta_Message;

    @FXML
    private MaterialDesignIconView btn_SendMessage;

    private List<ChatPane> chatPaneList = new ArrayList<>();

    public ChatRoomController() {
    }

    public ChatRoomController(Client client) {
        this.client = client;
    }

    @FXML
    void chooseFile(MouseEvent event) {

    }

    @FXML
    void sendMessage(Event event) throws Exception {
        String messageContent = ta_Message.getText();

//        if (choosenUser == null) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Thông báo!");
//            alert.setHeaderText("Bạn phải chọn một người để chat trước khi gửi tin nhắn!");
//            alert.showAndWait();
//
//            return;
//        }
        if (peerHandler != null) {
            peerHandler.sendMessage(ta_Message.getText());
        }
    }


    private KeyCodeCombination shiftEnterKey = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);

    @FXML
    void ta_messageKeyPressed(KeyEvent event) throws Exception {
        if (this.shiftEnterKey.match(event)) {
            this.sendMessage(event);
        }
    }

    public void loadMessageList(ObservableList<Message> messageObservableList) throws URISyntaxException {
//        Platform.runLater(() -> vb_ChatBox.getChildren().clear());
//
//        if (messageObservableList.isEmpty()) {
//            return;
//        }
//
//        for (Message message : messageObservableList) {
//            Node messageContainer = MessageContainer123.getInstance().createMessageContainerRAW(message, this.loggedUser);
//
//            Platform.runLater(() -> vb_ChatBox.getChildren().add(messageContainer));
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loggedUser = client.getLoggedUser();
        Platform.runLater(() -> {
            lv_UserList.setItems(client.getUserObservableList());
            lv_UserList.setCellFactory(userListView -> new UserListViewCell());
        });

        lv_UserList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldUser, newUser) -> {
            client.connectToPeer(loggedUser.getUserName(), newUser.getUserName());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<ChatPane> chatPaneList = new ArrayList<>();
            for (Map.Entry<PeerHandler, ObservableList<Message>> entry : client.mapPeerMessageList.entrySet()) {
                ChatPane chatPane = new ChatPane(entry.getKey(), entry.getValue());
                chatPaneList.add(chatPane);
            }
            stack_ChatPane.getChildren().setAll(chatPaneList);

            for (Node node : stack_ChatPane.getChildren()) {
                ChatPane chatPane = (ChatPane) node;
                if (chatPane.getPeerHandler().getPeerUser().getUserName().equals(newUser.getUserName())) {
                    chatPane.setVisible(true);
                } else if (chatPane.getPeerHandler().getPeerUser().getUserName().equals(oldUser.getUserName())) {
                    chatPane.setVisible(false);
                }
            }
        });
    }

    private void welcome() {
        VBox welcomeBox = new VBox();
        VBox.setVgrow(welcomeBox, Priority.ALWAYS);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.getStyleClass().add("welcome-box");

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        Text welcomeMessage_1 = new Text("Xin chào ");
        Text welcomeMessage_user = new Text(this.loggedUser.getUserNickname());
        Text welcomeMessage_2 = new Text("!");
        welcomeMessage_user.setStyle("-fx-fill: linear-gradient(to bottom right, #FC5C7D, #6A82FB)");
        hBox.getChildren().addAll(welcomeMessage_1, welcomeMessage_user, welcomeMessage_2);
        Text welcomeMessage_3 = new Text("Hãy chọn một người bạn để chat thôi nào!");

        welcomeBox.getChildren().addAll(hBox, welcomeMessage_3);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PeerHandler getPeerHandler() {
        return peerHandler;
    }

    public void setPeerHandler(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }

//    public List<User> getUserList() {
//        return userList;
//    }
//
//    public void setUserList(List<User> userList) {
//        this.userList = userList;
//    }

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
}
