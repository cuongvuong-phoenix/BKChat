package app.views;

import app.models.Message;
import app.socket.PeerHandler;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ChatPane extends AnchorPane {
    private PeerHandler peerHandler;

    private ObservableList<Message> messageObservableList;

    private KeyCodeCombination key_ShiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);

    public ChatPane(PeerHandler peerHandler, ObservableList<Message> messageObservableList) {
        super();
        this.peerHandler = peerHandler;
        this.messageObservableList = messageObservableList;
        this.getStylesheets().add(getClass().getResource("/app/styles/custom-scrollpane.css").toExternalForm());
        this.getStylesheets().add(getClass().getResource("/app/styles.css").toExternalForm());

        createChatPane();
    }

    private void createChatPane() {
        HBox topHBox = new HBox();
        ChatPane.setTopAnchor(topHBox, 0.0);
        ChatPane.setLeftAnchor(topHBox, 0.0);
        ChatPane.setRightAnchor(topHBox, 0.0);
        topHBox.setPrefHeight(80);
        topHBox.setMaxHeight(80);
        topHBox.setAlignment(Pos.CENTER);
        topHBox.getStyleClass().add("hb_TopRight");
        Label lbl_Username = new Label();
        lbl_Username.setText(peerHandler.getPeerUser().getUserName());
        lbl_Username.getStyleClass().add("lbl_ChatUser");
        topHBox.getChildren().add(lbl_Username);

        ScrollPane scrollPane = new ScrollPane();
        ChatPane.setRightAnchor(scrollPane, 0.0);
        ChatPane.setLeftAnchor(scrollPane, 0.0);
        ChatPane.setTopAnchor(scrollPane, 80.0);
        ChatPane.setBottomAnchor(scrollPane, 80.0);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox middleVBox = new VBox();
        middleVBox.setFillWidth(true);
        middleVBox.getStyleClass().add("scroll-pane__inside");
        middleVBox.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            scrollPane.setVvalue(1.0d);
        });
        for (Message message : messageObservableList) {
            MessageContainer messageContainer = new MessageContainer(message, peerHandler.getClient().getLoggedUser());
            middleVBox.getChildren().add(messageContainer);
        }

        messageObservableList.addListener((ListChangeListener<Message>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Message message : change.getAddedSubList()) {
                        MessageContainer messageContainer = new MessageContainer(message, peerHandler.getClient().getLoggedUser());
                        Platform.runLater(() -> {
                            middleVBox.getChildren().add(messageContainer);
                        });
                    }
                }
            }
        });
        scrollPane.setContent(middleVBox);

        HBox botHBox = new HBox();
        ChatPane.setBottomAnchor(botHBox, 0.0);
        ChatPane.setLeftAnchor(botHBox, 0.0);
        ChatPane.setRightAnchor(botHBox, 0.0);
        botHBox.setPrefWidth(80);
        botHBox.setPrefHeight(80);
        botHBox.setMaxHeight(80);
        botHBox.getStyleClass().add("hb_BottomRight");
        HBox chooseFileBox = new HBox();
        chooseFileBox.setPrefWidth(80);
        chooseFileBox.setPrefHeight(80);
        chooseFileBox.setAlignment(Pos.CENTER);
        FileChooser fileChooser = new FileChooser();
        MaterialDesignIconView btn_ChooseFile = new MaterialDesignIconView();
        btn_ChooseFile.setGlyphName("PLUS_CIRCLE");
        btn_ChooseFile.setSize("48");
        btn_ChooseFile.setCursor(Cursor.HAND);
        btn_ChooseFile.getStyleClass().addAll("avatar--default", "font--MaterialDesign");
        btn_ChooseFile.setOnMouseClicked(event -> {
            Node node = (Node) event.getSource();

            Window stage = node.getScene().getWindow();

            File fileChoosen = fileChooser.showOpenDialog(stage);
            if (fileChoosen != null) {
                String filePath = fileChoosen.getAbsolutePath();
                String fileName = fileChoosen.getName();
                peerHandler.sendFile(filePath, fileName);
            }
        });
        chooseFileBox.getChildren().add(btn_ChooseFile);

        TextArea textArea = new TextArea();
        HBox.setHgrow(textArea, Priority.ALWAYS);
        textArea.setPromptText("Nhập tin nhắn...");
        textArea.setPadding(new Insets(6, 0, 6, 0));
        textArea.setMaxHeight(80);
        textArea.setStyle("-fx-font-size: 24");
        textArea.getStyleClass().add("text-area--transparent");
        textArea.setOnKeyPressed(event -> {
            if (key_ShiftEnter.match(event)) {
                sendMessage(textArea.getText());
                textArea.clear();
            }
        });

        HBox sendMessageBox = new HBox();
        sendMessageBox.setPrefHeight(80);
        sendMessageBox.setPrefWidth(80);
        sendMessageBox.setAlignment(Pos.CENTER);
        MaterialDesignIconView btn_SendMessage = new MaterialDesignIconView();
        btn_SendMessage.setGlyphName("SEND");
        btn_SendMessage.setSize("48");
        btn_SendMessage.setCursor(Cursor.HAND);
        btn_SendMessage.getStyleClass().addAll("avatar--default", "font--MaterialDesign");
        btn_SendMessage.setOnMouseClicked(event -> {
            sendMessage(textArea.getText());
            textArea.clear();
        });
        sendMessageBox.getChildren().add(btn_SendMessage);

        botHBox.getChildren().addAll(chooseFileBox, textArea, sendMessageBox);

        this.getChildren().addAll(topHBox, scrollPane, botHBox);
        this.setVisible(false);
    }

    public void sendMessage(String message) {
        peerHandler.sendMessage(message);
    }

    public PeerHandler getPeerHandler() {
        return peerHandler;
    }

    public void setPeerHandler(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }
}
