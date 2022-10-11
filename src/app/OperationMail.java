package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.Client;

import java.io.IOException;
import java.util.Objects;

public class OperationMail {
    private static Client client;
    private static String textMail;
    private static String receiverMail;
    private static String sender;
    private static String object;
    private static String other_receiver;

    //Forward
    public OperationMail(Client clientForward, String text, String user, String obj) throws IOException {
        textMail = text;
        client = clientForward;
        receiverMail = user;
        object = obj;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/forward.fxml")));
        Scene scene = new Scene(root, 613, 400);
        Stage stage = new Stage();
        stage.setTitle("Inoltra email - " + client.getEmailUser());
        stage.setScene(scene);
        stage.show();
    }

    //Reply
    public OperationMail(String send, String obj, String txtMail, Client clientReply) throws IOException {
        sender = send;
        object = obj;
        textMail = txtMail;
        client = clientReply;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/reply.fxml")));
        Scene scene = new Scene(root, 600, 450);
        Stage stage = new Stage();
        stage.setTitle("Rispondi - " + client.getEmailUser());
        stage.setScene(scene);
        stage.show();
    }

    //ReplyAll
    public OperationMail(String send, String obj, String txtMail, String otherReceive, Client clientReply) throws IOException {
        sender = send;
        object = obj;
        textMail = txtMail;
        other_receiver = otherReceive;
        client = clientReply;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/replyall.fxml")));
        Scene scene = new Scene(root, 600, 450);
        Stage stage = new Stage();
        stage.setTitle("Rispondi a tutti - " + client.getEmailUser());
        stage.setScene(scene);
        stage.show();
    }

    //Send
    public OperationMail(Client clientSend) throws IOException {
        client = clientSend;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/send.fxml")));
        Scene scene = new Scene(root, 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Invia email - " + client.getEmailUser());
        stage.setScene(scene);
        stage.show();
    }

    public static Client getClient() {
        return client;
    }
    public static String getTextMail() {
        return textMail;
    }
    public static String getReceiverMail() {
        return receiverMail;
    }
    public static String getSender() {
        return sender;
    }
    public static String getObject() {
        return object;
    }
    public static String getOther_receiver() {
        return other_receiver;
    }

}
