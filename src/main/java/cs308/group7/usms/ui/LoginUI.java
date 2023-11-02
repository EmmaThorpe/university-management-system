package cs308.group7.usms.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginUI{

    Map<String, TextField> currentTextFields;

    Map<String, Text> currentText;

    /**gets the text fields currently shown on the page being shown
     * @return Map of text fields
     */
    public Map<String, TextField> getCurrentTextFields(){
        return currentTextFields;
    }


    /** Sets the current text fields currently being shown on the page
     * @param curr -List of current text fields being shown
     */
    public void setCurrentTextFields(Map<String, TextField> curr){
        currentTextFields = curr;
    }


    /** Gets the current text being shown on the stage currently
     * @return Current text displayed on scene
     */
    public Map<String, Text> getCurrentText(){
        return currentText;
    }


    /**Sets the current text being shown on stage
     * @param curr Current text displayed on scene
     */
    public void setCurrentText(Map<String, Text> curr){
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
        Map<String,Text> curr = new HashMap<>();
        curr.put("output", validHandler);
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

    public Scene signUpScene(Runnable signUp, Runnable goToLogin) {
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
        Text nameHandler = new Text();

        Label surname = new Label("SURNAME");
        TextField surnameTF = new TextField();
        Text surnameHandler = new Text();

        Label password = new Label("PASSWORD");
        TextField passwordTF = new TextField();
        Text passwordHandler = new Text();

        Label confirmPassword = new Label("CONFIRM PASSWORD");
        TextField confirmPasswordTF = new TextField();
        Text confirmPasswordHandler = new Text();

        Label qualification = new Label("QUALIFICATION");
        TextField qualificationTF = new TextField();
        Text qualificationHandler = new Text();

        VBox emailField = new VBox(email, emailTF, emailHandler);
        VBox forenameField = new VBox(forename, nameTF, nameHandler);
        VBox surnameField = new VBox(surname, surnameTF, surnameHandler);

        HBox.setHgrow(forenameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);
        HBox nameField = new HBox(forenameField, surnameField);
        nameField.setSpacing(10.0);

        VBox qualificationField = new VBox(qualification, qualificationTF, qualificationHandler);
        VBox passwordField = new VBox(password, passwordTF, passwordHandler);
        VBox confirmPasswordField = new VBox(confirmPassword, confirmPasswordTF, confirmPasswordHandler);

        VBox formContent = new VBox(userField, emailField, nameField, passwordField, confirmPasswordField);
        formContent.setPadding(new Insets(5));
        formContent.setSpacing(5);

        Button Submit = new Button("SUBMIT");
        Submit.setDisable(true);
        Submit.setOnAction(evt->signUp.run());

        Button returnBtn = new Button("RETURN TO LOGIN");
        returnBtn.getStyleClass().add("outline-button");

        HBox formBtns = new HBox(Submit, returnBtn);
        formBtns.setSpacing(20.0);
        formBtns.setAlignment(Pos.BOTTOM_CENTER);

        formBtns.setPadding(new Insets(20, 0, 0, 0));

        //Event handlers checking if valid details are entered
        AtomicBoolean validEmail = new AtomicBoolean(false);
        AtomicBoolean validName = new AtomicBoolean(false);
        AtomicBoolean validSurname = new AtomicBoolean(false);
        AtomicBoolean validPassword = new AtomicBoolean(false);
        AtomicBoolean validCheckedPassword = new AtomicBoolean(false);
        AtomicBoolean validQual = new AtomicBoolean(false);
        AtomicBoolean student = new AtomicBoolean(true);

        //email handler
        returnBtn.setOnAction(evt->goToLogin.run());

        emailTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!EmailValidator.getInstance().isValid(newText)) {
                emailHandler.setText("Not Valid Email Format");
                Submit.setDisable(true);
            } else if(newText.length()>20) {
                emailHandler.setText("Emails must be less than or equal to 20 character");
                validEmail.set(false);
                Submit.setDisable(true);
            }else {
                emailHandler.setText("");
                validEmail.set(true);
                if(validEmail.get() && validName.get() && validSurname.get() && validPassword.get() && validCheckedPassword.get() &&(validQual.get() || student.get())){
                    Submit.setDisable(false);
                }
            }

        });

        //forename handler
        nameTF.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty() || newText.length()>20) {
                nameHandler.setText("Names must be between 1 and 20 characters");
                validName.set(false);
                Submit.setDisable(true);
            } else {
                nameHandler.setText("");
                validName.set(true);
                if(validEmail.get() && validName.get() && validSurname.get() && validPassword.get() && validCheckedPassword.get() &&(validQual.get() ||student.get())){
                    Submit.setDisable(false);
                }
            }
        });

        //surname handler
        surnameTF.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty() || newText.length()>20) {
                surnameHandler.setText("Surnames must be between 1 and 20 characters");
                validSurname.set(false);
                Submit.setDisable(true);
            } else {
                surnameHandler.setText("");
                validSurname.set(true);
                if(validEmail.get() && validName.get() && validSurname.get() && validPassword.get() && validCheckedPassword.get() &&(validQual.get() ||student.get())){
                    Submit.setDisable(false);
                }
            }
        });

        //password handler
        passwordTF.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() <8 || newText.length()>20) {
                passwordHandler.setText("Passwords must be between 8 and 20 characters long");
                validPassword.set(false);
                Submit.setDisable(true);
            } else if (!validPassword(newText, passwordHandler)) {
                Submit.setDisable(true);
            }else{
                passwordHandler.setText("");
                validPassword.set(true);
                if(validEmail.get() && validName.get() && validSurname.get() && validPassword.get() && validCheckedPassword.get() &&(validQual.get() ||student.get())){
                    Submit.setDisable(false);
                }
            }
        });

        //confirm password handler
        confirmPasswordTF.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(passwordTF.getText())) {
                confirmPasswordHandler.setText("Passwords must match");
                validCheckedPassword.set(false);
                Submit.setDisable(true);
            } else {
                confirmPasswordHandler.setText("");
                validCheckedPassword.set(true);
                if(validEmail.get() && validName.get() && validSurname.get() && validPassword.get() && validCheckedPassword.get() &&(validQual.get() ||student.get())){
                    Submit.setDisable(false);
                }
            }
        });


        //qualification handler
        qualificationTF.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty() || newText.length()>20) {
                qualificationHandler.setText("Qualifications must be between 1 and 20 characters");
                validQual.set(false);
                Submit.setDisable(true);
            } else {
                qualificationHandler.setText("");
                validQual.set(true);
                if(validEmail.get() && validName.get() && validSurname.get() && validPassword.get() && validCheckedPassword.get() && validQual.get()){
                    Submit.setDisable(false);
                }
            }
        });


        userSelected.selectedToggleProperty().addListener((observableValue, currentToggle, newToggle) -> {
            if (userSelected.getSelectedToggle().getUserData() == "lecturer") {
                formContent.getChildren().add(qualificationField);
                student.set(false);

                Submit.setDisable(!validEmail.get() || !validName.get() || !validSurname.get() || !validPassword.get() || !validCheckedPassword.get() || !validQual.get());
            } else {
                student.set(true);
                formContent.getChildren().remove(qualificationField);

                Submit.setDisable(!validEmail.get() || !validName.get() || !validSurname.get() || !validPassword.get() || !validCheckedPassword.get());
            }
        });

        BorderPane root = new BorderPane(formContent);
        root.setBottom(formBtns);
        root.setTop(title);

        root.setPadding(new Insets(10));

        return new Scene(root);
    }


    public boolean validPassword(String password, Text output){
        String specialChars = "@!#$%&/()=?@£{}.-;<>_,";
        boolean upperCharacter = false;
        boolean lowerCharacter = false;
        boolean number = false;
        boolean specialCharacter = false;

        for (int i = 0; i < password.length(); i++){
            char curr = password.charAt(i);

            if(Character.isUpperCase(curr)){
                upperCharacter = true;
            }else if(Character.isLowerCase(curr)){
                lowerCharacter = true;
            }else if(Character.isDigit(curr)){
                number = true;
            }else if(specialChars.contains(Character.toString(curr))){
                specialCharacter = true;
            }else{
                output.setText("Contains character not allowed in passwords");
                return false;
            }
        }

        if(!upperCharacter){
            output.setText("Password must contain an uppercase letter");
            return false;
        }else if(!lowerCharacter){
            output.setText("Password must contain a lowercase letter");
            return false;
        }else if(!number){
            output.setText("Password must contain a number");
            return false;
        }else if(!specialCharacter){
            output.setText("Password must contain a special letter");
            return false;
        }
        return true;
    }





    /** Return the unactivated account scene
     * @param goToLogin - method to go to login page
     * @return unactivated account scene
     */
    public Scene unactivatedAccount(Runnable goToLogin){
        VBox title = setTitle("NOTICE");

        HBox unactivatedNotfi = setNotficationCard("Sorry your account has not been activated yet.\r\nTry again later.");
        unactivatedNotfi.setFillHeight(false);

        Button returnBtn = new Button("RETURN TO LOGIN");
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

    public Scene createdAccount (Runnable goToLogin){
        VBox title = setTitle("NOTICE");

        HBox unactivatedNotfi = setNotficationSuccessCard("Your account has successfully been created.\r\nContact the manager to get it activated.");
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

        Map<String, TextField> curr = new HashMap<>();
        curr.put("email", emailTF);
        curr.put("password", passwordTF);
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

    private HBox setNotficationCard(String msg) {
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

    private HBox setNotficationSuccessCard (String msg) {
        FontIcon notfiGraphic = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
        notfiGraphic.getStyleClass().add("success-graphic");

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


