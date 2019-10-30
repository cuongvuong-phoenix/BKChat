package app.views;

import app.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class UserListViewCell extends ListCell<User> {

    @FXML
    private HBox hb_UserContainer;

    @FXML
    private Circle circle_UserAvatar;

    @FXML
    private VBox vb_UserDetail;

    @FXML
    private Label lbl_UserName;

    @FXML
    private Circle circle_UserStatus;

    @FXML
    private Label lbl_UserStatus;

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/app/views/UserListViewCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            try {
//                String imagePath = new File("assets/user-avatar/" + user.getUserAvatar()).toURI().toString();
//                circle_UserAvatar.setFill(new ImagePattern(new Image(imagePath)));
//            } catch (Exception e) {
//                circle_UserAvatar.getStyleClass().add("avatar--default");
//            }

            circle_UserAvatar.getStyleClass().add("avatar--default");

            lbl_UserName.setText(user.getUserName());

            switch (user.getUserStatus()) {
                case "Online": {
                    circle_UserStatus.setFill(Color.valueOf("#42b72a"));
                    break;
                }
                case "Offline": {
                    circle_UserStatus.setFill(Color.valueOf("#ffffff"));
                    break;
                }
                default: {
                    break;
                }
            }

            lbl_UserStatus.setText(user.getUserStatus());

            setText(null);
            setGraphic(hb_UserContainer);
        }
    }

}
