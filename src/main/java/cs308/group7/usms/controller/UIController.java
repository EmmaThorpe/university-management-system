package cs308.group7.usms.controller;
import com.sun.javafx.css.StyleManager;
import cs308.group7.usms.ui.LoginUI;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Application;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class UIController  implements Observer {
    Stage currentStage = new Stage();
    String css = this.getClass().getResource("/css/style.css").toExternalForm();


    /**  Display the first scene and shows the stage
     * @param scene - First scene to be displayed
     */
    public void displayFirstScene(Scene scene) {
        Application.setUserAgentStylesheet(css);
        currentStage.setScene(scene);
        currentStage.showAndWait();

    }


    /**Switches out scenes to display a new one
     * @param scene -scene to be displayed
     */
    public void displayScene(Scene scene) {
        currentStage.setScene(scene);

    }

    /**
     * Close the current stage
     */
    public void hideStage() {
        currentStage.close();

    }

    public void observe(Observable o) {
        o.addObserver(this);
    }
    @Override
    public void update(Observable o, Object arg) {
        String someVariable = ((LoginUI) o).getEvent();
        System.out.println(someVariable);
    }
}
