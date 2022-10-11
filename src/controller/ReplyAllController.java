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

public class ReplyAllController implements Initializable{
    @FXML
    private Label altridestinatari;
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
        altridestinatari.setText(setReceiver(OperationMail.getOther_receiver(), OperationMail.getSender()));
    }
    public void handleReplyAllEmail(ActionEvent actionEvent) throws IOException {
        String divide_destinatari = setReceiver(OperationMail.getOther_receiver(), OperationMail.getSender());
        if(Objects.equals(OperationMail.getOther_receiver(), "")) {
            System.out.println("Errore: Altri destinatari vuoto\n");
        } else {
            String[] destinatari = divide_destinatari.split(",");
            for(int i = 0; i<destinatari.length; i++) {
                Email email = new Email(0, client.getEmailUser(), destinatari[i], oggetto.getText(), testoemail.getText(), Calendar.getInstance().getTime().toString(), divide_destinatari);
                client.sendNewEmail(email);
            }
        }
        user.getScene().getWindow().hide();
    }
    private String setReceiver(String destinatari, String mittente) {
        String[] altri_destinatari = destinatari.split(",");
        String result;
        int i;
        if(altri_destinatari.length > 1) {
            result = mittente + ",";
        } else {
            result = mittente;
        }
        for(i = 0; i<altri_destinatari.length; i++) {
            if(!client.getEmailUser().equals(altri_destinatari[i])) {
                if (i == altri_destinatari.length - 1) {
                    result = result + altri_destinatari[i];
                } else {
                    result = result + altri_destinatari[i] + ",";
                }
            }
        }
        return result;
    }
}
