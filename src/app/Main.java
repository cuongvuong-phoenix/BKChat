package app;

import app.controllers.SigninController;
import app.socket.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client client = new Client("192.168.31.150", 9998, primaryStage);
        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection succeed");
            client.start();
            client.loadSigninFXML();

            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
