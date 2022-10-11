package server;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController {
    @FXML
    private Button on;
    @FXML
    private Button off;
    @FXML
    private Button reset;
    @FXML
    private TableView<Server.logsView> logview;
    @FXML
    private TableColumn<Server.logsView, String> time;
    @FXML
    private TableColumn<Server.logsView, String> event;
    private Server server;
    public void setModel(Server s) {
        on.setVisible(false);
        this.server = s;

        time.setCellValueFactory(log->
                new SimpleStringProperty(log.getValue().getTableTime()));
        event.setCellValueFactory(log->
                new SimpleStringProperty(log.getValue().getTableEvent()));


        logview.setItems(this.server.getLogView());
    }
    public void handleOn(ActionEvent actionEvent) {
        server.setOn();
        on.setVisible(false);
        off.setVisible(true);
    }

    public void handleOff(ActionEvent actionEvent) {
        server.setOff();
        on.setVisible(true);
        off.setVisible(false);
    }

    public void handleReset(ActionEvent actionEvent) {
        reset.setVisible(true);
        logview.getItems().clear();
    }
}
