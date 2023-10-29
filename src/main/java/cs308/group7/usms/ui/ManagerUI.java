package cs308.group7.usms.ui;

import cs308.group7.usms.Student;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ManagerUI {
    static Student current;

    //public ManagerUI(String name, Stage stage) {
        //currentStage = stage;
        //css = this.getClass().getResource("/css/style.css").toExternalForm();
    //}


    /*@Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(greeting());
        primaryStage.show();
    }*/

    public void greeting(){
        Label test=new Label("Hello " + current.getName());
        GridPane root=new GridPane();
        root.setHgap(20);
        root.setVgap(15);
        Scene scene = new Scene(root,500,300);
        root.addRow(0, test);
        //displayScene(scene);
    }

    public void home(){
        Label test=new Label("Hello Manager");
        GridPane root=new GridPane();
        root.setHgap(20);
        root.setVgap(15);
        Scene scene = new Scene(root,500,300);
        root.addRow(0, test);
        //displayScene(scene);
    }

}
