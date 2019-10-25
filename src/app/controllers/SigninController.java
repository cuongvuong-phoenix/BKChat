package app.controllers;

import app.models.User;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class SigninController implements Initializable {
    private Client client;

    private String result;

    public SigninController(Client client) {
        this.client = client;
    }

    @FXML
    private TextField tf_Username;

    @FXML
    private PasswordField pf_Password;

    @FXML
    private TextField tf_Hostname;

    @FXML
    private TextField tf_Port;

    @FXML
    private Label lbl_bypassMessage;
    private BypassMessage bypassMessage;

    @FXML
    private Button btnSignin;

    @FXML
    void signin(ActionEvent event) throws Exception {
        String username = tf_Username.getText();
        String password = pf_Password.getText();
        String hostname = tf_Hostname.getText();
        String port = tf_Port.getText();

        if (username.isEmpty()) {
            String usernameBypass_Failed = "Tên tài khoản không được trống!";
            bypassMessage.setBypassMessage(usernameBypass_Failed, false);
            return;
        } else if (password.isEmpty()) {
            String passwordBypass_Failed = "Mật khẩu không được trống!";
            bypassMessage.setBypassMessage(passwordBypass_Failed, false);
            return;
        } else if (hostname.isEmpty()) {
            String hostaddBypass_Failed = "Địa chỉ của Host không được trống!";
            bypassMessage.setBypassMessage(hostaddBypass_Failed, false);
            return;
        } else if (port.isEmpty()) {
            String portBypass_Failed = "Số Port không được trống!";
            bypassMessage.setBypassMessage(portBypass_Failed, false);
            return;
        }

        // Logic
        client.signin(username, password);
        String resultMessage;
        do {
            resultMessage = client.getResultMessage();
        } while (resultMessage == null);

        if (resultMessage.equals("Success")) {
            String bypassSuccess = "Đăng nhập thành công! Chat thôi nào...";
            bypassMessage.setBypassMessage(bypassSuccess, true);
            client.loadChatroomFXML();
        } else if (resultMessage.equals("Failed")) {
            String bypassFailed = "Sai tên tài khoản hoặc mật khẩu!";
            bypassMessage.setBypassMessage(bypassFailed, false);
        }
        client.setResultMessage(null);
    }

    @FXML
    void signup(MouseEvent event) throws IOException {
        client.loadSignupFXML();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tf_Hostname.setText("localhost");
        tf_Port.setText("7007");
        lbl_bypassMessage.setText("");
        bypassMessage = new BypassMessage(lbl_bypassMessage);

        // Handle Events
        List<TextField> textFieldList = new ArrayList<>();
        Collections.addAll(textFieldList, tf_Username, pf_Password, tf_Hostname, tf_Port);
        for (TextField textField : textFieldList) {
            textField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                lbl_bypassMessage.setText("");
            }));
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
