package app.ultilies;

import app.models.Message;
import app.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MessageContainer {
    private MessageContainer() {
    }

    private static class MessageContainerHolder {
        private static final MessageContainer INSTANCE = new MessageContainer();
    }

    public static MessageContainer getInstance() {
        return MessageContainerHolder.INSTANCE;
    }

    @FXML
    private Circle circle_senderAvatar;

    @FXML
    private Label lbl_senderName;

    @FXML
    private Label lbl_timeSent;

    @FXML
    private Text txt_messageContent;

    private FXMLLoader fxmlLoader;

    public Node createMessageContainer(Message message) {
        Node result = null;

        if (message == null) {
            return null;
        }

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../views/MessageContainer.fxml"));
            fxmlLoader.setController(this);

            try {
                result = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        circle_senderAvatar.getStyleClass().add("avatar--default");
        lbl_senderName.setText(message.getUserFrom().getUserNickname());
        lbl_timeSent.setText(message.getTimeSent().toString());
        txt_messageContent.setText(message.getContent());

        return result;
    }

    public Node createMessageContainerRAW(Message message, User loggedUser) {
        HBox messageContainer = new HBox();
        // Message's Box
        HBox messageBox = new HBox();
        messageBox.setMinWidth(25);
        messageBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        messageBox.setMaxWidth(700);
        messageBox.setAlignment(Pos.BOTTOM_LEFT);
        messageBox.setPadding(new Insets(6, 16, 6, 16));
        messageBox.setSpacing(8);
        // Sender's Avatar
        Circle messageBox_Avatar = new Circle(20, 20, 20);
//        try {
//            FileInputStream imageFile = new FileInputStream(new File("assets/user-avatar/" + message.getUserFrom().getUserAvatar()));
//            messageBox_Avatar.setFill(new ImagePattern(new Image(imageFile)));
//        } catch (Exception e) {
//            messageBox_Avatar.getStyleClass().add("avatar--default");
//        }
        messageBox_Avatar.getStyleClass().add("avatar--default");
        // Sender's Message detail
        VBox detailBox = new VBox();
        HBox.setHgrow(detailBox, Priority.ALWAYS);
        detailBox.setPadding(new Insets(16));
        detailBox.setSpacing(16);
        detailBox.getStyleClass().add("detail-box");
        // Sender's Message Info
        HBox detailBox_InfoBox = new HBox();
        detailBox_InfoBox.setAlignment(Pos.CENTER_LEFT);
        Label lbl_userFromName = new Label(message.getUserFrom().getUserNickname());
        lbl_userFromName.getStyleClass().add("detail-box__senderName");
        Pane betweenPane = new Pane();
        HBox.setHgrow(betweenPane, Priority.ALWAYS);
        betweenPane.setMinWidth(24);
        Date timeSent = message.getTimeSent();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//        String timeSentString = dateTimeFormatter.format(timeSent.toInstant());
        String timeSentString = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(timeSent);
        Label lbl_timeSent = new Label(timeSentString);
        lbl_timeSent.getStyleClass().add("detail-box__timeSent");
        // Sender's Message Content
        TextFlow tf_messageContent = new TextFlow();
        Text messageContent = new Text(message.getContent());
        messageContent.getStyleClass().add("detail-box__message-content");
        tf_messageContent.getChildren().add(messageContent);
        // Styles
        if (loggedUser.equals(message.getUserFrom())) {
            messageContainer.setAlignment(Pos.TOP_RIGHT);
            messageBox.getStyleClass().add("message-box--logged-user");
        } else {
            messageContainer.setAlignment(Pos.TOP_LEFT);
            messageBox.getStyleClass().add("message-box--guest-user");
        }
        // Wrapping up
        detailBox_InfoBox.getChildren().addAll(lbl_userFromName, betweenPane, lbl_timeSent);
        detailBox.getChildren().addAll(detailBox_InfoBox, tf_messageContent);
        if (loggedUser.equals(message.getUserFrom())) {
            messageBox.getChildren().addAll(detailBox, messageBox_Avatar);
        } else {
            messageBox.getChildren().addAll(messageBox_Avatar, detailBox);
        }
        messageContainer.getChildren().add(messageBox);

        return messageContainer;
    }
}
