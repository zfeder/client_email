//CONTROLLER
package client;

import app.*;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import client.Client;
import app.Email;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class ClientController implements Initializable {
    @FXML
    private Button rispondi;
    @FXML
    private Button inoltra;
    @FXML
    private Button risponditutti;
    @FXML
    private Button elimina;
    @FXML
    private Label usermail;
    @FXML
    private Label oggetto;
    @FXML
    private Label mittente;
    @FXML
    private Label data;
    @FXML
    private Label testomail;
    @FXML
    private ListView<Email> listemailricevuta;
    @FXML
    private ListView<Email> listemailinviata;
    @FXML
    private ListView<String> listnotifiche;
    @FXML
    private Label altridestinatari;

    private Client client;
    private Email emailSelected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String userMail = ClientApp.getEmailUser();
        client = ClientApp.getClient();
        usermail.setText(userMail);
        client.downloadEmail();
        rispondi.setVisible(false);
        inoltra.setVisible(false);
        risponditutti.setVisible(false);
        elimina.setVisible(false);
        //listemailinviata.itemsProperty().bind(client.getListSend());
        listemailinviata.setItems(client.getListEmailSend());
        listemailinviata.setCellFactory(param -> new ListCell<Email>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if(empty || email == null || email.getObject() == null) {
                    setText("");
                }
                else {
                    setText(email.getObject() + ", a: " + email.getReceiver());
                }
            }});
        listemailinviata.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Email email_send;
                email_send = listemailinviata.getSelectionModel().getSelectedItem();
                emailSelected = email_send;
                getInformationEmail(email_send);
                rispondi.setVisible(false);
                risponditutti.setVisible(false);
                inoltra.setVisible(false);
                elimina.setVisible(false);
            }
        });
        //listemailricevuta.itemsProperty().bind(client.getListReceive());
        listemailricevuta.setItems(client.getListEmailReceive());
        listemailricevuta.setCellFactory(param -> new ListCell<Email>() {
         @Override
         protected void updateItem(Email email, boolean empty) {
             super.updateItem(email, empty);
             if(empty || email == null || email.getObject() == null) {
                 setText("");
             }
             else {
                 setText(email.getObject() + ", da: " + email.getSender());
             }
         }});
        listemailricevuta.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Email email_receive;
                email_receive = listemailricevuta.getSelectionModel().getSelectedItem();
                emailSelected = email_receive;
                getInformationEmail(email_receive);
                rispondi.setVisible(true);
                inoltra.setVisible(true);
                elimina.setVisible(true);
                risponditutti.setVisible(true);
                }
        });
        System.out.println("Avvio del Client: " + client.getEmailUser());
        listnotifiche.itemsProperty().bind(this.client.getListNotify());
    }
    public void getInformationEmail(Email email) {
        if(email != null) {
            oggetto.setText(email.getObject());
            mittente.setText(email.getSender());
            data.setText(email.getDate());
            testomail.setText(" " + email.getText());
            altridestinatari.setText(email.getOther_dest());
        }
    }
    @FXML
    private void handleNewEmail(ActionEvent actionEvent) throws IOException {
        OperationMail operationMailSend = new OperationMail(client);
        System.out.println("Comando: nuova email nel client: " + client.getEmailUser());
    }
    @FXML
    private void handleReplyEmail(ActionEvent actionEvent) throws IOException {
        OperationMail operationMailReply = new OperationMail(mittente.getText(), oggetto.getText(), testomail.getText(), client);
        System.out.println("Comando: rispondi email nel client: " + client.getEmailUser());
    }
    @FXML
    private void handleForwardEmail(ActionEvent actionEvent) throws IOException {
        OperationMail operationMailForward = new OperationMail(client, testomail.getText(), mittente.getText(), oggetto.getText());
        System.out.println("Comando: inoltra email nel client: " + client.getEmailUser());
    }
    @FXML
    private void handleReplyAll(ActionEvent actionEvent) throws IOException {
        OperationMail operationMailReplyAll = new OperationMail(mittente.getText(), oggetto.getText(), testomail.getText(), altridestinatari.getText(), client);
        System.out.println("Comando: rispondi a tutti email nel client: " + client.getEmailUser());
    }
    @FXML
    private void handleRemoveEmail(ActionEvent actionEvent) throws IOException{
        if (client.getListEmailReceive().contains(this.emailSelected)) {
            client.removeEmailReceive(this.emailSelected);
        }
        System.out.println("Comando: rimuovi email nel client: " + client.getEmailUser());
    }
}
