import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MainScreenController {



    @FXML
    private Pane path_pane;

    @FXML
    private Button path_button;

    @FXML
    private Button default_button;

    @FXML
    private Label path_label;

    @FXML
    public void initialize() {
        path_label.setText(GUI.getCurrentPath());

        path_button.setOnAction( event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(null);
            GUI.setCurrentPath(file.getAbsolutePath());
            path_label.setText(file.getAbsolutePath());
        });

        default_button.setOnAction(event -> {
            GUI.setDefault();
            path_label.setText(GUI.getCurrentPath());
        });
    }
}
