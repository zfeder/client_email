package controller;

import app.OperationMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import client.Client;
import app.Email;


import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

public class SendController implements Initializable{
    @FXML
    private TextField destinatario;
    @FXML
    private TextField oggetto;
    @FXML
    private TextArea testoemail;
    @FXML
    private Label inserimentoerrore;
    @FXML
    private Label user;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = OperationMail.getClient();
        user.setText(client.getEmailUser());
    }

    public void handleSendNewEmail(ActionEvent actionEvent) throws IOException {
        String [] destinatari_email = destinatario.getText().split(",");
        boolean error = true;
        if(!Objects.equals(destinatari_email[0], "")) {
            for(String d: destinatari_email) {
                if(!Email.verificationEmailAddress(d)) {
                    error = false;
                    inserimentoerrore.setText("Email non corretta o inesistente");
                }
            }
        } else {
            error = false;
            inserimentoerrore.setText("Nessun destinatario inserito");
        }
        if(error) {
            inserimentoerrore.setText("");
            for(String d: destinatari_email) {
                Email email = new Email(0, client.getEmailUser(), d, oggetto.getText(), testoemail.getText(), Calendar.getInstance().getTime().toString(), destinatario.getText());
                client.sendNewEmail(email);
                inserimentoerrore.setVisible(false);
            }
            user.getScene().getWindow().hide();
        }
    }

}
