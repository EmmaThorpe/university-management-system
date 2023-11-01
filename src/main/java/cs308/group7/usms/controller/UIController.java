package cs308.group7.usms.controller;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import java.util.Map;

public class UIController{
    Stage currentStage = new Stage();
    String css = this.getClass().getResource("/css/style.css").toExternalForm();


    /**  Display the first scene and shows the stage
     * @param scene - First scene to be displayed
     */
    public void displayFirstScene(Scene scene) {
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);
        currentStage.showAndWait();

    }


    /**Switches out scenes to display a new one
     * @param scene -scene to be displayed
     */
    public void displayScene(Scene scene) {
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);

    }

    /**
     * Close the current stage
     */
    public void hideStage() {
        currentStage.close();

    }
}
