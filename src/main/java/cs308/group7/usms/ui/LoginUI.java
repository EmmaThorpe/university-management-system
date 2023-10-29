package cs308.group7.usms.ui;

import cs308.group7.usms.controller.PasswordManager;
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

public class LoginUI {
    Stage currentStage;
    Stack<Scene> scenes;
    String css;
    PasswordManager pass;

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        css = this.getClass().getResource("/css/style.css").toExternalForm();
        pass = new PasswordManager();
        scenes = new Stack<>();
        initialScene(loginScene());
    }

    /*public void addScene(Scene scene){
        scene.getStylesheets().add(css);
        scenes.add(scene);
    }

    public void removeScenes(Scene scene, int amount){
        for(int i=0; i<amount; i++){
            scenes.pop();
        }
    }*/

    public void initialScene(Scene scene) {
        //Scene scene = scenes.pop();
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);
        currentStage.sizeToScene();
        currentStage.show();
    }

    public void displayScene(Scene scene) {
        //Scene scene = scenes.pop();
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);
    }

    public Scene loginScene() {
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

        Submit.setOnAction(logIn(emailTF, passwordF, validHandler));

        //Submit.setOnAction(goToStudent());
        New.setOnAction(goToSignUp());


        formBtns.setPadding(new Insets(20, 0, 0, 0));

        BorderPane root = new BorderPane(formContent);
        root.setBottom(new VBox(formBtns, validHandler));
        root.setTop(title);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }


    public void signUpScene() {
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
        Submit.setOnAction(goToLogin());
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

        displayScene(new Scene(root));
    }

    public void unactivatedAccount(){
        Text message = new Text();
        message.setText("Sorry your account has not been activated yet. Try again later.");

        Button Submit = new Button("Return to Login");
        HBox formBtns = new HBox(Submit);
        Submit.setOnAction(goToLogin());


        formBtns.setPadding(new Insets(20, 0, 0, 0));

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);

        root.addRow(1, message);
        root.addRow(2, formBtns);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
    }

    private VBox loginFields() {
        Label email = new Label("E-MAIL");
        Label password = new Label("PASSWORD");
        TextField tf1 = new TextField();
        TextField tf2 = new TextField();

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


        public EventHandler<ActionEvent> goToSignUp () {
            return (arg0 -> signUpScene());
        }

        public EventHandler<ActionEvent> goToLogin () {
            return (arg0 -> loginScene());
        }

        public EventHandler<ActionEvent> logIn (TextField email, PasswordField password, Text validHandler){
            return new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    Map<String, String> result = pass.login(email.getText(), password.getText());
                    //System.out.println(result.toString());
                    if (result == null) {
                        validHandler.setText("Incorrect Details");
                    }else if(result.get("activated").equals("True")){
                        switch(result.get("role")){
                            case("Student"):
                                System.out.println("s");
                                StudentUI stuUI = new StudentUI(result.get("userID"), currentStage);
                                System.out.println("a");
                                stuUI.home();
                                goToLogin();
                                break;
                            case("Lecturer"):
                                LecturerUI lecUI = new LecturerUI(result.get("userID"), currentStage);
                                lecUI.home();
                                goToLogin();
                                break;
                            case("Manager"):
                                ManagerUI manUI = new ManagerUI(result.get("userID"), currentStage);
                                manUI.home();
                                goToLogin();
                                break;
                        }
                    }else{
                        unactivatedAccount();
                    }
                }
            };

        }

}


