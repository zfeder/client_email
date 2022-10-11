package controller;

import app.OperationMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import client.Client;
import app.Email;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReplyController implements Initializable{
    @FXML
    private Label destinatario;
    @FXML
    private Label oggetto;
    @FXML
    private Label inserimentoerrore;
    @FXML
    private Label user;
    @FXML
    private TextArea testoemail;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = OperationMail.getClient();
        oggetto.setText(OperationMail.getObject());
        testoemail.setText(OperationMail.getTextMail());
        destinatario.setText(OperationMail.getSender());
    }
    public void handleReplyEmail(ActionEvent actionEvent) throws IOException {
        Email email = new Email(0, client.getEmailUser(), destinatario.getText(), oggetto.getText(), testoemail.getText(), Calendar.getInstance().getTime().toString(), destinatario.getText());
        client.sendNewEmail(email);
        user.getScene().getWindow().hide();
    }


}
