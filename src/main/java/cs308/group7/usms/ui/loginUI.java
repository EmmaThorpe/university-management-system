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
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class loginUI extends Application {
    Stage currentStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        loginScene();
    }

    public void displayScene(Scene scene){
        String css = this.getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);
        currentStage.sizeToScene();
        currentStage.show();
    }

    public void loginScene(){

        GridPane formContent = loginFields();

        Button Submit=new Button("Submit");
        Button New=new Button("New User");

        HBox formBtns = new HBox(Submit, New);
        Submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                studentUI stu = new studentUI("aa", currentStage);
            }
        });

        New.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                signUpScene();
            }
        });

        BorderPane root = new BorderPane(formContent);

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        root.setBottom(formBtns);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
    }

    public void signUpScene(){
        BorderPane root = new BorderPane(signUpFieldsStudent());

        Button Submit=new Button("Submit");
        HBox formBtns = new HBox(Submit);
        Submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                loginScene();
            }
        });

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        root.setBottom(formBtns);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
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

    private GridPane signUpFieldsStudent() {
        Label email=new Label("Email");
        Label forename=new Label("Forename");
        Label surname=new Label("Surname");
        Label password=new Label("Password");
        TextField tf1=new TextField();
        TextField tf2=new TextField();
        TextField tf3=new TextField();
        TextField tf4=new TextField();

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);

        root.addRow(0, email, tf1);
        root.addRow(1, password, tf2);
        root.addRow(2, forename, tf3);
        root.addRow(3, surname, tf4);
        return root;
    }
}


