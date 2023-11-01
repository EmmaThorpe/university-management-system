package cs308.group7.usms.ui;

import cs308.group7.usms.controller.PasswordManager;
import cs308.group7.usms.controller.UIController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.validator.routines.EmailValidator;

import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LoginUI{


    List<TextField> currentTextFields;

    List<Text> currentText;

    /**gets the text fields currently shown on the page being shown
     * @return List of text fields
     */
    public List<TextField> getCurrentTextFields(){
        return currentTextFields;
    }


    /** Sets the current text fields currently being shown on the page
     * @param curr -List of current text fields being shown
     */
    public void setCurrentTextFields(ArrayList<TextField> curr){
        currentTextFields = curr;
    }


    /** Gets the current text being shown on the stage currently
     * @return Current text displayed on scene
     */
    public List<Text> getCurrentText(){
        return currentText;
    }


    /**Sets the current text being shown on stage
     * @param curr Current text displayed on scene
     */
    public void setCurrentText(ArrayList<Text> curr){
        currentText = curr;
    }


    /** Returns the login scene so it can then be displayed
     * @param goToSignUp - method to go to signup page
     * @param Login - method to attempt to login
     * @return login scene
     */
    public Scene loginScene(Runnable goToSignUp, Runnable Login) {

        VBox formContent = loginFields();

        VBox title = setTitle("LOGIN");

        Button Submit = new Button("SUBMIT");
        Button New = new Button("NEW USER");

        New.getStyleClass().add("outline-button");

        HBox formBtns = new HBox(Submit, New);
        formBtns.setSpacing(20.0);
        formBtns.setAlignment(Pos.BOTTOM_CENTER);

        formBtns.setPadding(new Insets(20, 0, 0, 0));

        Text validHandler = new Text();
        ArrayList<Text> curr = new ArrayList<>();
        curr.add(validHandler);
        setCurrentText(curr);

        New.setOnAction(evt->goToSignUp.run());
        Submit.setOnAction(evt->Login.run());

        formBtns.setPadding(new Insets(20, 0, 0, 0));

        BorderPane root = new BorderPane(formContent);
        root.setBottom(new VBox(formBtns, validHandler));
        root.setTop(title);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }

    public Scene signUpScene() {
        VBox title = setTitle("SIGN UP");

        ToggleGroup userSelected = new ToggleGroup();

        ToggleButton studentOp = setToggleOption(userSelected, "student", new FontIcon(FontAwesomeSolid.USER));
        ToggleButton lecturerOp = setToggleOption(userSelected, "lecturer", new FontIcon(FontAwesomeSolid.CHALKBOARD_TEACHER));
        studentOp.setSelected(true);

        HBox.setHgrow(studentOp, Priority.ALWAYS);
        HBox.setHgrow(lecturerOp, Priority.ALWAYS);
        HBox userOptions = new HBox(studentOp, lecturerOp);
        userOptions.setSpacing(20.0);
        userOptions.setAlignment(Pos.BASELINE_CENTER);

        Label userFieldLabel = new Label("USER TYPE");
        VBox userField = new VBox(userFieldLabel, userOptions);
        userField.setPadding(new Insets(5));

        Label email = new Label("E-MAIL");
        TextField emailTF = new TextField();
        Text emailHandler = new Text();

        Label forename = new Label("FORENAME");
        TextField nameTF = new TextField();

        Label surname = new Label("SURNAME");
        TextField surnameTF = new TextField();

        Label password = new Label("PASSWORD");
        TextField passwordTF = new TextField();

        Label qualification = new Label("QUALIFICATION");
        TextField qualificationTF = new TextField();

        VBox emailField = new VBox(email, emailTF, emailHandler);
        VBox forenameField = new VBox(forename, nameTF);
        VBox surnameField = new VBox(surname, surnameTF);

        HBox.setHgrow(forenameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);
        HBox nameField = new HBox(forenameField, surnameField);
        nameField.setSpacing(10.0);

        VBox qualificationField = new VBox(qualification, qualificationTF);
        VBox passwordField = new VBox(password, passwordTF);

        VBox formContent = new VBox(userField, emailField, nameField, passwordField);
        formContent.setPadding(new Insets(5));
        formContent.setSpacing(5);

        Button Submit = new Button("Submit");
        HBox formBtns = new HBox(Submit);

        Submit.setDisable(true);
        formBtns.setPadding(new Insets(20, 0, 0, 0));

        emailTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!EmailValidator.getInstance().isValid(newText)) {
                emailHandler.setText("Not Valid Email Format");
                Submit.setDisable(true);
            } else {
                emailHandler.setText("");
                Submit.setDisable(false);
            }
        });

        userSelected.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle currentToggle, Toggle newToggle) {
                if (userSelected.getSelectedToggle().getUserData() == "lecturer") {
                    formContent.getChildren().add(qualificationField);
                } else {
                    formContent.getChildren().remove(qualificationField);
                }
            }
        });

        BorderPane root = new BorderPane(formContent);
        root.setBottom(formBtns);
        root.setTop(title);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }


    /** Retrun the unactivated account scene
     * @param goToLogin - method to go to login page
     * @return unactivated account scene
     */
    public Scene unactivatedAccount(Runnable goToLogin){
        VBox title = setTitle("NOTICE");

        HBox unactivatedNotfi = setNotficationCard("Sorry your account has not been activated yet.\r\nTry again later.");
        unactivatedNotfi.setFillHeight(false);

        Button returnBtn = new Button("Return to Login");
        returnBtn.setOnAction(evt->goToLogin.run());

        HBox btnContain = new HBox(returnBtn);
        btnContain.setPadding(new Insets(20, 0, 0, 0));
        btnContain.setAlignment(Pos.BOTTOM_CENTER);

        BorderPane root = new BorderPane(unactivatedNotfi);
        root.setBottom(btnContain);
        root.setTop(title);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }

    private VBox loginFields() {
        Label email=new Label("E-MAIL");
        Label password=new Label("PASSWORD");
        TextField emailTF=new TextField();
        TextField passwordTF=new TextField();

        ArrayList<TextField> curr = new ArrayList<>();
        curr.add(emailTF);
        curr.add(passwordTF);
        setCurrentTextFields(curr);

        VBox emailField = new VBox(email, emailTF);
        emailField.setPadding(new Insets(10));

        VBox passwordField = new VBox(password, passwordTF);
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
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        appGraphic.getStyleClass().add("login-graphic");

        iconStack.getChildren().addAll(appGraphicBack, appGraphic);


        Text titleName = new Text(titleText);
        titleName.getStyleClass().add("login-title");

        VBox title = new VBox(iconStack, titleName);
        title.setAlignment(Pos.TOP_CENTER);
        return title;
    }

    private HBox setNotficationCard (String msg) {
        FontIcon notfiGraphic = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        notfiGraphic.getStyleClass().add("notfi-graphic");

        Label notfiMsg = new Label(msg, notfiGraphic);
        notfiMsg.setPadding(new Insets(10));
        notfiMsg.setWrapText(true);
        notfiMsg.setMaxWidth(250.0);
        notfiMsg.setGraphicTextGap(20.0);

        HBox notfiCard = new HBox(notfiMsg);
        notfiCard.setPadding(new Insets(20));
        notfiCard.getStyleClass().add("card");

        return notfiCard;
    }

    private ToggleButton setToggleOption(ToggleGroup group, String operatorName, FontIcon icon) {
        String operatorNameDisplay = operatorName.toUpperCase();
        FontIcon opGraphic = icon;
        opGraphic.getStyleClass().add("card-graphic");
        ToggleButton op = new ToggleButton(operatorNameDisplay, opGraphic);
        op.setToggleGroup(group);
        op.setUserData(operatorName.toLowerCase());
        op.setContentDisplay(ContentDisplay.TOP);
        op.getStyleClass().add("card-toggle");
        return op;
    }
}


