package cs308.group7.usms.controller;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;

public class UIController extends Application{
    Stage primaryStage;
    String css;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage currentStage = primaryStage;
        String css = this.getClass().getResource("/css/style.css").toExternalForm();

        PasswordManager passwordM = new PasswordManager(currentStage);


    }

    public UIController(Stage pri){
        primaryStage =pri;
    }
    public void displayScene(Scene scene) {
        //Scene scene = scenes.pop();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
}
