package app.ultilies;

import app.models.Message;
import app.socket.PeerHandler;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ChatPane extends AnchorPane {
    private PeerHandler peerHandler;

    private ObservableList<Message> messageObservableList;

    public ChatPane(PeerHandler peerHandler, ObservableList<Message> messageObservableList) {
        super();
        this.peerHandler = peerHandler;
        this.messageObservableList = messageObservableList;

        //createChatPane();
        //getChildren().add(createChatPane());
    }

//    public AnchorPane createChatPane () {
//        AnchorPane anchorPane = new AnchorPane();
//        HBox topHBox = new HBox();
//        AnchorPane.setTopAnchor(topHBox, 0.0);
//        AnchorPane.setLeftAnchor(topHBox, 0.0);
//        AnchorPane.setRightAnchor(topHBox, 0.0);
//        topHBox.setPrefHeight(80);
//        topHBox.setMaxHeight(80);
//        topHBox.setAlignment(Pos.CENTER);
//        Label lbl_Username = new Label();
//        lbl_Username.setText(peerHandler.getPeerUser().getUserName());
//        lbl_Username.setId("lb_ChatUser");
//        topHBox.getChildren().add(lbl_Username);
//        anchorPane.getChildren().add(topHBox);
//
//        ScrollPane scrollPane = new ScrollPane();
//        AnchorPane.setRightAnchor(scrollPane, 0.0);
//        AnchorPane.setLeftAnchor(scrollPane, 0.0);
//        AnchorPane.setTopAnchor(scrollPane, 80.0);
//        AnchorPane.setBottomAnchor(scrollPane, 80.0);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setId("sp_ChatBox");
//        scrollPane.getStylesheets().add(getClass().getResource("/app/styles/custom-scrollpane.css").toExternalForm());
//        VBox middleVBox = new VBox();
//        middleVBox.setFillWidth(true);
//        middleVBox.getStyleClass().add("scroll-pane-inside");
//        scrollPane.setContent(middleVBox);
//        anchorPane.getChildren().add(scrollPane);
//
//        HBox botHBox = new HBox();
//        AnchorPane.setBottomAnchor(botHBox, 0.0);
//        AnchorPane.setLeftAnchor(botHBox, 0.0);
//        AnchorPane.setRightAnchor(botHBox, 0.0);
//        HBox chooseFileBox = new HBox();
//        chooseFileBox.setAlignment(Pos.CENTER);
//    }


    public ObservableList<Message> getMessageObservableList() {
        return messageObservableList;
    }

    public void setMessageObservableList(ObservableList<Message> messageObservableList) {
        this.messageObservableList = messageObservableList;
    }

    public PeerHandler getPeerHandler() {
        return peerHandler;
    }

    public void setPeerHandler(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }
}
