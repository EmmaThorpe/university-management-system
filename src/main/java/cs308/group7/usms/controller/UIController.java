package cs308.group7.usms.controller;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;

public class UIController extends Application{
    Stage primaryStage;
    String css;

    public UIController(){}

    public UIController(Stage stage){
        primaryStage = stage;
        css = this.getClass().getResource("/css/style.css").toExternalForm();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage currentStage = primaryStage;
        PasswordManager pass = new PasswordManager(currentStage);
    }
    public void displayScene(Scene scene) {
        //Scene scene = scenes.pop();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
