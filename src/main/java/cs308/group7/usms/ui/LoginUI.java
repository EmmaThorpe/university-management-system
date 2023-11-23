package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.*;
import javafx.scene.layout.HBox;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.*;

public class LoginUI extends UIElements{


    /** Returns the login scene so it can then be displayed
     */
    public void loginScene() {
        resetCurrentValues();

        VBox formContent = new VBox(inputField("EMAIL",false), inputField("PASSWORD", true));
        formContent.setPadding(new Insets(10));
        formContent.setSpacing(8);

        Button Submit = inputButton("SUBMIT");
        Button New = inputButton("NEW USER");
        New.getStyleClass().add("outline-button");

        HBox formBtns = bottomButtons(new HBox(Submit, New));

        Text validHandler = inputText("OUTPUT");

        createScene("LOGIN", formContent, new VBox(formBtns, validHandler));
    }

    public void signUpScene() {
        validFields = new HashMap<>();
        resetCurrentValues();
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

        VBox emailField = textAndField("EMAIL", emailCheck());
        VBox forenameField = textAndField("FORENAME", lengthCheck("FORENAME"));
        VBox surnameField = textAndField("SURNAME", lengthCheck("SURNAME"));
        VBox passwordField = textAndField("PASSWORD", passwordCheck());
        VBox confirmPasswordField = textAndField("CONFIRM PASSWORD", confirmPasswordCheck());
        VBox qualificationField = textAndField("QUALIFICATION", lengthCheck("QUALIFICATION"));

        HBox.setHgrow(forenameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);
        HBox nameField = new HBox(forenameField, surnameField);
        nameField.setSpacing(10.0);

        VBox formFields = new VBox(userField, emailField, nameField, passwordField, confirmPasswordField);
        formFields.setPadding(new Insets(5));
        formFields.setSpacing(5);
        ScrollPane fieldScroll = new ScrollPane(formFields);
        fieldScroll.setFitToWidth(true);

        userSelected.selectedToggleProperty().addListener(toggleUser(fieldScroll, qualificationField));

        Button Submit = inputButton("SUBMIT");

        Submit.setDisable(true);
        validFields.put("STUDENT", true);

        Button returnBtn = inputButton("RETURN TO LOGIN");
        returnBtn.getStyleClass().add("outline-button");

        VBox formContent = new VBox(fieldScroll);
        formContent.setAlignment(Pos.CENTER);
        HBox formBtns = bottomButtons(new HBox(Submit, returnBtn));

        createScene("SIGN UP", formContent, formBtns);
    }

    private void checkValidFields(String type, Boolean value){
        validFields.put(type, value);
        if(value && (validFields.get("QUALIFICATION") || validFields.get("STUDENT"))){
            boolean overallValid = true;
            for (String key : validFields.keySet()) {
                if(!key.equals("QUALIFICATION") && !key.equals("STUDENT")){
                    overallValid = overallValid && validFields.get(key);
                }
            }
            if(overallValid){
                currentButtons.get("SUBMIT").setDisable(false);
            }
        }else{
            currentButtons.get("SUBMIT").setDisable(true);
        }

    }

    protected ChangeListener<String> lengthCheck(String fieldType){
        return (observableVal, oldVal, newVal) -> {
            if (newVal.isEmpty() || newVal.length() > 20) {
                currentText.get(fieldType).setText(fieldType + " must be between 1 and 20 characters");
                checkValidFields(fieldType, false);
            } else {
                currentText.get(fieldType).setText("");
                checkValidFields(fieldType, true);
            }
        };

    }

    protected ChangeListener<String> emailCheck(){
        return (obs, oldText, newText) -> {
            if (!EmailValidator.getInstance().isValid(newText)) {
                currentText.get("EMAIL").setText("Not a valid Email format");
                checkValidFields("EMAIL", false);
            } else if (newText.length() > 20) {
                currentText.get("EMAIL").setText("Emails must be less than or equal to 20 character");
                checkValidFields("EMAIL", false);
            } else {
                currentText.get("EMAIL").setText("");
                checkValidFields("EMAIL", true);
            }
        };

    }

    protected ChangeListener<String> passwordCheck(){
        return (obs, oldText, newText) -> {
            if (newText.length() <8 || newText.length()>20) {
                currentText.get("PASSWORD").setText("Passwords must be between 8 and 20 characters long");
                checkValidFields("PASSWORD", false);
            } else if (!validPassword(newText, currentText.get("PASSWORD"))) {
                checkValidFields("PASSWORD", false);
            }else{
                currentText.get("PASSWORD").setText("");
                checkValidFields("PASSWORD", true);
            }
        };

    }

    protected ChangeListener<String> confirmPasswordCheck(){
        return (obs, oldText, newText) -> {
            String passwordText = ((TextField) currentFields.get("PASSWORD")).getText();
            if (!newText.equals(passwordText)){
                currentText.get("CONFIRM PASSWORD").setText("Passwords must match");
                checkValidFields("CONFIRM PASSWORD", false);
            } else {
                currentText.get("CONFIRM PASSWORD").setText("");
                checkValidFields("CONFIRM PASSWORD", true);
            }
        };

    }

    protected ChangeListener<Toggle> toggleUser(ScrollPane formContent, VBox qualificationField) {
        return (observableValue, previousToggle, newToggle) -> {
            if (newToggle == null) {
                previousToggle.setSelected(true);
            } else if (previousToggle != null) {
                if (newToggle.getUserData() == "lecturer") {
                    VBox fields = (VBox) formContent.getContent();
                    fields.getChildren().add(qualificationField);
                    formContent.contentProperty().set(fields);
                    checkValidFields("STUDENT", false);
                } else {
                    VBox fields = (VBox) formContent.getContent();
                    fields.getChildren().remove(qualificationField);
                    formContent.contentProperty().set(fields);
                    checkValidFields("STUDENT", true);
                }
            }
        };
    }



}




