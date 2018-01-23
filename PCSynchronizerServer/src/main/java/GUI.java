import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application{

    private final String MAIN_SCREEN_FXML = "view/main_screen.fxml";
    private static final String DEFAULT_PATH = "D:/PCSynchronized";
    private static  String CURRENT_PATH = DEFAULT_PATH;
    private static Server server;

    @Override
    public void start(Stage primaryStage) throws Exception {
        server = new Server(CURRENT_PATH);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(MAIN_SCREEN_FXML));
        loader.setController(new MainScreenController());
        Parent root = loader.load();
        Scene scene = new Scene(root, 360, 190);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("PCSynchronizer Server");
        primaryStage.show();

        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                showAlert("Trouble with starting server!");
            }
        }).start();

    }

    public static String getCurrentPath() {
        return CURRENT_PATH;
    }

    public static void setCurrentPath(String currentPath) {
        CURRENT_PATH = currentPath;
        server.setPath(CURRENT_PATH);
    }

    public static void setDefault() {
        CURRENT_PATH = DEFAULT_PATH;
        server.setPath(CURRENT_PATH);
    }
    private static void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        if (server != null) {
            try {
                server.stop();
            } catch (IOException e) {
                showAlert("Trouble with stopping server!");
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
