package cs308.group7.usms.controller;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
        Map<String, String> output= new HashMap<>();
        //while (output.containsKey("Exiting")){
            PasswordManager pass = new PasswordManager(currentStage);
            //output = pass.response();
        //}

    }
    public void displayScene(Scene scene) {
        //Scene scene = scenes.pop();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
