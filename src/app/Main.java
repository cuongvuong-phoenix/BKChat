package app;

import app.socket.Client;
import app.ultilies.BypassMessage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Main extends Application {
    @FXML
    private TextField tf_HostAddress;

    @FXML
    private Label lbl_bypassMessage;

    @FXML
    private Button btn_ConnectServer;

    private BypassMessage bypassMessage;

    @FXML
    void signin(ActionEvent event) {

    }

    private void connectServer(Stage primaryStage) throws Exception {
        String hostAddress = tf_HostAddress.getText();
        try {
            Client client = new Client(hostAddress, 9998, primaryStage);
            if (!client.connect()) {
                bypassMessage.setBypassMessage("Địa chỉ IP của Server không đúng", false);
                System.out.println("Connection failed");
            } else {
                System.out.println("Connection succeed");
                client.start();
                client.loadSigninFXML();
            }
        } catch (Exception e) {
            bypassMessage.setBypassMessage("Địa chỉ IP của Server không đúng", false);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/views/connectserver.fxml"));
        fxmlLoader.setController(this);
        Parent root = fxmlLoader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        bypassMessage = new BypassMessage(lbl_bypassMessage);

        lbl_bypassMessage.setText("");

        tf_HostAddress.textProperty().addListener((observableValue, oldValue, newValue) -> {
            lbl_bypassMessage.setText("");
        });

        tf_HostAddress.setOnAction(event -> {
            try {
                connectServer(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btn_ConnectServer.setOnAction(event -> {
            try {
                connectServer(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
