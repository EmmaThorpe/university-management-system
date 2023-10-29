package cs308.group7.usms.ui;

import cs308.group7.usms.controller.PasswordManager;
import cs308.group7.usms.controller.UIController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Map;
import java.util.Stack;

public class LoginUI{

    public LoginUI(){
    }

    Stack<Scene> scenes;
    PasswordManager pass;

    /*@Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        css = this.getClass().getResource("/css/style.css").toExternalForm();
        pass = new PasswordManager();
        scenes = new Stack<>();
        initialScene(loginScene());
    }*/

    /*public void addScene(Scene scene){
        scene.getStylesheets().add(css);
        scenes.add(scene);
    }

    public void removeScenes(Scene scene, int amount){
        for(int i=0; i<amount; i++){
            scenes.pop();
        }
    }*/



    public Scene loginScene(Runnable goToSignUp) {
        Label email = new Label("Email");
        TextField emailTF = new TextField();


        //VBox formContent = loginFields();

        Label password = new Label("Password");
        PasswordField passwordF = new PasswordField();

        GridPane formContent = new GridPane();
        formContent.addRow(0, email, emailTF);
        formContent.addRow(1, password, passwordF);

        VBox title = setTitle("LOGIN");

        Button cardTest = new Button("hi i am some text");
        cardTest.getStyleClass().add("card");

        //formContent.getChildren().add(cardTest);

        Button Submit = new Button("SUBMIT");
        Button New = new Button("NEW USER");

        New.getStyleClass().add("outline-button");

        HBox formBtns = new HBox(Submit, New);
        formBtns.setSpacing(20.0);
        formBtns.setAlignment(Pos.BOTTOM_CENTER);

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        Text validHandler = new Text();

        //Submit.setOnAction(logIn(emailTF, passwordF, validHandler));

        //Submit.setOnAction(goToStudent());
        New.setOnAction(evt->goToSignUp.run());


        formBtns.setPadding(new Insets(20, 0, 0, 0));

        BorderPane root = new BorderPane(formContent);
        root.setBottom(new VBox(formBtns, validHandler));
        root.setTop(title);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }


    public Scene signUpScene() {
        Label email = new Label("Email");
        TextField emailTF = new TextField();
        Text emailHandler = new Text();

        Label forename = new Label("Forename");
        TextField nameTF = new TextField();

        Label surname = new Label("Surname");
        TextField surnameTF = new TextField();

        Label password = new Label("Password");
        TextField passwordTF = new TextField();


        Button Submit = new Button("Submit");
        HBox formBtns = new HBox(Submit);
        //Submit.setOnAction(goToLogin());
        Submit.setDisable(true);


        emailTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!EmailValidator.getInstance().isValid(newText)) {
                emailHandler.setText("Not Valid Email Format");
                Submit.setDisable(true);
            } else {
                emailHandler.setText("");
                Submit.setDisable(false);
            }

        });

        formBtns.setPadding(new Insets(20, 0, 0, 0));

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);

        root.addRow(0, email, emailTF);
        root.addRow(1, emailHandler);
        root.addRow(2, password, nameTF);
        root.addRow(3, forename, surnameTF);
        root.addRow(4, surname, passwordTF);
        root.addRow(5, formBtns);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }

    public Scene unactivatedAccount(){
        Text message = new Text();
        message.setText("Sorry your account has not been activated yet. Try again later.");

        Button Submit = new Button("Return to Login");
        HBox formBtns = new HBox(Submit);
        //Submit.setOnAction(goToLogin());


        formBtns.setPadding(new Insets(20, 0, 0, 0));

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);

        root.addRow(1, message);
        root.addRow(2, formBtns);

        root.setPadding(new Insets(10));

        return new Scene(root);
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




}


