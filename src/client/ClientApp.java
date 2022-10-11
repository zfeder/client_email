//VIEW
package client;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import client.Client;


import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;


public class ClientApp {
    private static String emailUser;
    private static Client client;
    private ExecutorService executorService;
    public ClientApp(String email, Client c, ExecutorService excServ) {
        emailUser = email;
        client = c;
        executorService = excServ;
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/client.fxml")));
            Scene scene = new Scene(root, 800, 500);
            Stage stage = new Stage();
            stage.setTitle("Client: " + emailUser);
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    Thread.currentThread().interrupt();
                    executorService.shutdown();
                }
            });
        } catch (Exception e) {
            System.out.println("ERRORE: Generato nel ClientApp, " + e);
        }
    }

    public static String getEmailUser() {
        return emailUser;
    }

    public static Client getClient() {
        return client;
    }
}
