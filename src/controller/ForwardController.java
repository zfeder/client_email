package controller;

import app.OperationMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import client.Client;
import app.Email;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

public class ForwardController implements Initializable{
    @FXML
    private TextField destinatario;
    @FXML
    private TextField oggetto;
    @FXML
    private TextArea testoemail;
    @FXML
    private Label inserimentoerrore;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = OperationMail.getClient();
        testoemail.setText("Email inoltrata da: " + OperationMail.getReceiverMail() + " || Testo email: " + OperationMail.getTextMail());
        oggetto.setText("Oggetto email: " + OperationMail.getObject());
    }
    public void handleSendMail(ActionEvent actionEvent) throws IOException {
        String[] destinatari_email = destinatario.getText().split(",");
        boolean error = true;
        if(!Objects.equals(destinatari_email[0], "")) {
            for(int i = 0; i<destinatari_email.length; i++) {
                if(!Email.verificationEmailAddress(destinatari_email[i])) {
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
            for(int i = 0; i<destinatari_email.length; i++) {
                Email email = new Email(0, client.getEmailUser(), destinatari_email[i], oggetto.getText(), testoemail.getText(), Calendar.getInstance().getTime().toString(), destinatario.getText());
                client.sendNewEmail(email);
            }
            inserimentoerrore.getScene().getWindow().hide();
        }
    }
}
