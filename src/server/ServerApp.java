package server;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.ServerController;

public class ServerApp extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server.fxml"));
        Parent root = loader.load();
        stage.setTitle("Server panel");
        stage.setScene(new Scene(root, 500, 600));
        stage.show();
        ServerController server_controller = loader.getController();
        Server server = new Server();
        server_controller.setModel(server);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                server.serverClose();
            }
        });
    }

}
