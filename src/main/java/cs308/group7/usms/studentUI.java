package cs308.group7.usms;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class studentUI extends Application {
    Student current;

    public studentUI(String name){
        current = new Student(name);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(greeting());
        primaryStage.show();
    }

    public Scene greeting(){
        Label test=new Label("Hello " + current.getName());
        GridPane root=new GridPane();
        root.setHgap(20);
        root.setVgap(15);
        Scene scene = new Scene(root,500,300);
        root.addRow(0, test);
        return scene;
    }
}
