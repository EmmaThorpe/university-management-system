package cs308.group7.usms.ui;

import cs308.group7.usms.model.Student;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LecturerUI extends MainUI{



    public Scene home(){
        Label test=new Label("Hello Lecturer");
        GridPane root=new GridPane();
        root.setHgap(20);
        root.setVgap(15);
        Scene scene = new Scene(root,500,300);
        root.addRow(0, test);
        return scene;
    }

}
