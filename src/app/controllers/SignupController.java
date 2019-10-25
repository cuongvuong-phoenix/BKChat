package app.controllers;

import app.socket.Client;
import app.ultilies.BypassMessage;
import app.ultilies.ScenesUltilies;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class SignupController implements Initializable {
    private Client client;

    public SignupController(Client client) {
        this.client = client;
    }

    @FXML
    private TextField tf_Username;

    @FXML
    private PasswordField pf_Password;

    @FXML
    private TextField tf_Nickname;

    @FXML
    private Button btnSignup;

    @FXML
    private Label lbl_bypassMessage;
    private BypassMessage bypassMessage;

    @FXML
    void signup(ActionEvent event) throws Exception {
        String username = tf_Username.getText();
        String password = pf_Password.getText();
        String nickname = tf_Nickname.getText();

        if (username.isEmpty()) {
            String emptyUsername = "Tên tài khoản không được trống!";
            bypassMessage.setBypassMessage(emptyUsername, false);

            return;
        } else if (password.isEmpty()) {
            String emptyPassword = "Mật khẩu không được trống!";
            bypassMessage.setBypassMessage(emptyPassword, false);

            return;
        } else if (nickname.length() < 2) {
            String nicknameBypassError = "Tên người dùng phải có từ ít nhất 2 kí tự trở lên!";
            bypassMessage.setBypassMessage(nicknameBypassError, false);

            return;
        }

        client.signup(username, password, nickname);
        String resultMessage;
        do {
            resultMessage = client.getResultMessage();
        } while (resultMessage == null);

        if (resultMessage.equals("Success")) {
            String bypassSuccess = "Đăng ký tài khoản mới thành công! Mời bạn đăng nhập.";
            bypassMessage.setBypassMessage(bypassSuccess, true);
        } else if (resultMessage.equals("Failed")) {
            String bypassFailed = "Tên tài khoản bị trùng! Hãy chọn tên khác!";
            bypassMessage.setBypassMessage(bypassFailed, false);
        }

        client.setResultMessage(null);
    }

    @FXML
    void signin(MouseEvent event) throws IOException {
        client.loadSigninFXML();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_bypassMessage.setText("");
        bypassMessage = new BypassMessage(lbl_bypassMessage);

        // Handle Events
        List<TextField> textFieldList = new ArrayList<>();
        Collections.addAll(textFieldList, tf_Username, pf_Password, tf_Nickname);
        for (TextField textField : textFieldList) {
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                lbl_bypassMessage.setText("");
            });
        }
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
