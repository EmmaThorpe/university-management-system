package cs308.group7.usms.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class loginUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = loginScene(primaryStage);
        String css = this.getClass().getResource("/style/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public Scene loginScene(Stage primaryStage){

        GridPane formContent = loginFields();

        Button Submit=new Button("Submit");
        HBox formBtns = new HBox(Submit);
        Label l = new Label("button not selected");
        Submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                /*
                primaryStage.setScene(signUpScene("hello"));
                primaryStage.show();
                 */
                l.setText("selected");
            }
        });

        BorderPane root = new BorderPane(formContent);

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        root.setBottom(formBtns);

        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
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


    private GridPane loginFields() {
        Label email=new Label("Email");
        Label password=new Label("Password");
        TextField tf1=new TextField();
        TextField tf2=new TextField();

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);
        root.addRow(0, email, tf1);
        root.addRow(1, password, tf2);
        return root;
    }
}


