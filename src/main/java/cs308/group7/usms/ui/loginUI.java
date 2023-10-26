package cs308.group7.usms.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;

import java.util.Stack;

public class loginUI extends Application {
    Stage currentStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        loginScene();
    }

    public void displayScene(Scene scene) {
        String css = this.getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);
        currentStage.sizeToScene();
        currentStage.show();
    }

    public void loginScene() {

        VBox formContent = loginFields();

        VBox title = setTitle("LOGIN");

        Button cardTest = new Button("hi i am some text");
        cardTest.getStyleClass().add("card");

        formContent.getChildren().add(cardTest);

        Button Submit = new Button("SUBMIT");
        Button New = new Button("NEW USER");

        New.getStyleClass().add("outline-button");

        HBox formBtns = new HBox(Submit, New);
        formBtns.setSpacing(20.0);
        formBtns.setAlignment(Pos.BOTTOM_CENTER);

        Submit.setOnAction(goToStudent());
        New.setOnAction(goToSignUp());

        BorderPane root = new BorderPane(formContent);

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        root.setBottom(formBtns);
        root.setTop(title);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
    }



    public void signUpScene(){
        BorderPane root = new BorderPane(signUpFieldsStudent());

        Button Submit=new Button("Submit");
        HBox formBtns = new HBox(Submit);
        Submit.setOnAction(goToLogin());

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        root.setBottom(formBtns);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
    }


    private VBox loginFields() {
        Label email=new Label("E-MAIL");
        Label password=new Label("PASSWORD");
        TextField tf1=new TextField();
        TextField tf2=new TextField();

        VBox emailField = new VBox(email, tf1);
        emailField.setPadding(new Insets(10));

        VBox passwordField = new VBox(password, tf2);
        passwordField.setPadding(new Insets(10));

        VBox root = new VBox(emailField, passwordField);
        root.setPadding(new Insets(10));
        root.setSpacing(8);
        return root;
    }

    private VBox setTitle(String titleText) {
        StackPane iconStack = new StackPane();
        Circle appGraphicBack = new Circle(35);
        appGraphicBack.getStyleClass().add("login-back");
        FontIcon appGraphic = new FontIcon(FontAwesome.GRADUATION_CAP);
        appGraphic.getStyleClass().add("login-graphic");

        iconStack.getChildren().addAll(appGraphicBack, appGraphic);


        Text titleName = new Text(titleText);
        titleName.getStyleClass().add("login-title");

        VBox title = new VBox(iconStack, titleName);
        title.setAlignment(Pos.TOP_CENTER);
        return title;
    }

    private GridPane signUpFieldsStudent() {
        Label email=new Label("E-MAIL");
        Label forename=new Label("FORENAME");
        Label surname=new Label("SURNAME");
        Label password=new Label("PASSWORD");
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



    public EventHandler<ActionEvent> goToSignUp() {
        return (arg0 -> signUpScene());

    }

    public EventHandler<ActionEvent> goToLogin() {
        return (arg0 -> loginScene());

    }

    public EventHandler<ActionEvent> goToStudent() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                studentUI stu = new studentUI("aa", currentStage);
            }
        };

    }
}


