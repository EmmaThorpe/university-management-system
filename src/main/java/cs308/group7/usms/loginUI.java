package cs308.group7.usms;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class loginUI extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(loginScene(primaryStage));
        primaryStage.show();
    }

    public Scene loginScene(Stage primaryStage){
        Label email=new Label("Email");
        Label password=new Label("Password");
        TextField tf1=new TextField();
        TextField tf2=new TextField();

        Button Submit=new Button("Submit");
        Submit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                primaryStage.setScene(signUpScene(tf1.getText().toString()));
                primaryStage.show();
            }
        });



        GridPane root=new GridPane();
        root.setHgap(20);
        root.setVgap(15);
        Scene scene = new Scene(root,500,300);
        root.addRow(0, email,tf1);
        root.addRow(1, password,tf2);
        root.addRow(2, Submit);

        //Adding CSS file to the root
        root.getStylesheets().add("Style.css");
        return scene;
    }

    public Scene signUpScene(String tex){
        Label test=new Label(tex);
        GridPane root=new GridPane();
        root.setHgap(20);
        root.setVgap(15);
        Scene scene = new Scene(root,500,300);
        root.addRow(0, test);
        return scene;
    }


}
