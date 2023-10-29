package cs308.group7.usms.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class UIController {
    Stage primaryStage;
    String css;

    public UIController(Stage pri){
        primaryStage =pri;
    }
    public void displayScene(Scene scene) {
        //Scene scene = scenes.pop();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
}
