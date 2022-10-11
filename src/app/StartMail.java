package app;

import client.ClientApp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.Client;
import java.io.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class StartMail extends Application {
    private ExecutorService executorService;
    @Override
    public void start(Stage startStage) {
        UserMail user_mail = new UserMail();
        user_mail.start();
    }
    class UserMail extends Thread {
        private final String[] user = {"giorgio@unito.it", "federico@unito.it"};
        @Override
        public void run() {
            int i = 0;
            int POOLTHREAD = 4;
            executorService = Executors.newFixedThreadPool(POOLTHREAD);
            while(i < user.length) {
                executorService.execute(new UserStartEmail(user[i]));
                i++;
            }
        }
        class UserStartEmail implements Runnable {
            private final String email;
            private final Client client;
            public UserStartEmail(String s) {
                email = s;
                client = new Client(email);
            }

            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new ClientApp(email, client, executorService);
                    }
                });
            }
        }
    }
}
