package app.ultilies;

import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScenesUltilies {
    private ScenesUltilies() {
    }

    private static class HelpersHolder {
        private static final ScenesUltilies INSTANCE = new ScenesUltilies();
    }

    public static ScenesUltilies getInstance() {
        return HelpersHolder.INSTANCE;
    }

    public void ChangeScene(String fxml, Event event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        Node node = (Node) event.getSource();

        Stage stage = (Stage) node.getScene().getWindow();

        stage.setScene(new Scene(root));
    }

//    public void ChangeSceneWithController(String fxml, Event event, Class controller) {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
//        loader.setControllerFactory(initClass -> {
//            if (controller.getClass().equals(initClass)) {
//                return controller;
//            } else {
//
//            }
//        });
//    }
}
