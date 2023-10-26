package cs308.group7.usms.ui;

import cs308.group7.usms.controller.PasswordManager;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;




import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Map;

public class loginUI extends Application {
    Stage currentStage;
    String css;
    PasswordManager pass;

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        css = this.getClass().getResource("/css/style.css").toExternalForm();
        pass= new PasswordManager();
        loginScene();
    }

    public void displayScene(Scene scene) {
        scene.getStylesheets().add(css);
        currentStage.setScene(scene);
        currentStage.sizeToScene();
        currentStage.show();
    }

    public void loginScene() {
        Label email=new Label("Email");
        TextField emailTF=new TextField();

        Label password=new Label("Password");
        TextField passwordTF=new TextField();



        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);
        root.addRow(0, email, emailTF);
        root.addRow(1, password, passwordTF);

        Button Submit = new Button("Submit");
        Button New = new Button("New User");

        HBox formBtns = new HBox(Submit, New);

        formBtns.setPadding(new Insets(20, 0, 0, 0));
        Text validHandler = new Text();


        Submit.setOnAction(logIn(emailTF, passwordTF, validHandler));

        New.setOnAction(goToSignUp());


        root.addRow(2,formBtns);
        root.addRow(3,validHandler);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
    }



    public void signUpScene(){
        Label email=new Label("Email");
        TextField emailTF =new TextField();
        Text emailHandler = new Text();

        Label forename=new Label("Forename");
        TextField nameTF =new TextField();

        Label surname=new Label("Surname");
        TextField surnameTF =new TextField();

        Label password=new Label("Password");
        TextField passwordTF =new TextField();


        Button Submit=new Button("Submit");
        HBox formBtns = new HBox(Submit);
        Submit.setOnAction(goToLogin());
        Submit.setDisable(true);


        emailTF.textProperty().addListener((obs, oldText, newText) -> {
            if(!EmailValidator.getInstance().isValid(newText)){
                emailHandler.setText("Not Valid Email Format");
                Submit.setDisable(true);
            }else{
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
        root.addRow(5,formBtns);

        root.setPadding(new Insets(10));

        displayScene(new Scene(root));
    }








    public EventHandler<ActionEvent> goToSignUp() {
        return (arg0 -> signUpScene());

    }

    public EventHandler<ActionEvent> goToLogin() {
        return (arg0 -> loginScene());

    }

    public EventHandler<ActionEvent> logIn(TextField email, TextField password, Text validHandler) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                Map<String,String> result= pass.login(email.getText(), password.getText());
                if(result == null){
                    validHandler.setText("Incorrect Details");
                }else{
                    validHandler.setText(result.toString());
                    if (result.get("activated")=="True"){

                    }else{

                    }
                }
            }
        };

    }
}


