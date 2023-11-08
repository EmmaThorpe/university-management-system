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

public class LoginUI extends MainUI{

    private Map<String, Boolean> validFields;

    protected VBox textAndField(String text, ChangeListener<String> listener){
        Label label=new Label(text);
        TextField field=new TextField();
        Text inputText = new Text();

        currentTextFields.put(text, field);
        currentText.put(text, inputText);

        field.textProperty().addListener(listener);

        validFields.put(text, false);

        return new VBox(label, field, inputText);
    }


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

        VBox formContent = new VBox(userField, emailField, nameField, passwordField, confirmPasswordField);
        formContent.setPadding(new Insets(5));
        formContent.setSpacing(5);

        userSelected.selectedToggleProperty().addListener(toggleUser(userSelected, formContent, qualificationField));

        Button Submit = inputButton("SUBMIT");

        Submit.setDisable(true);
        validFields.put("STUDENT", true);

        Button returnBtn = inputButton("RETURN TO LOGIN");
        returnBtn.getStyleClass().add("outline-button");

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
                currentText.get("EMAIL").setText("Not Valid Email Format");
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
            if (!newText.equals(currentTextFields.get("PASSWORD").getText())) {
                currentText.get("CONFIRM PASSWORD").setText("Passwords must match");
                checkValidFields("CONFIRM PASSWORD", false);
            } else {
                currentText.get("CONFIRM PASSWORD").setText("");
                checkValidFields("CONFIRM PASSWORD", true);
            }
        };

    }


    public boolean validPassword(String password, Text output){
        String specialChars = "@!#$%&/()=?@£{}.-;<>_,*";
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


    protected ChangeListener<Toggle> toggleUser(ToggleGroup userSelected, VBox formContent, VBox qualificationField){
        return (observableValue, currentToggle, newToggle) -> {
            if (userSelected.getSelectedToggle().getUserData() == "lecturer") {
                formContent.getChildren().add(qualificationField);
                checkValidFields("STUDENT", false);
            } else {
                formContent.getChildren().remove(qualificationField);
                checkValidFields("STUDENT", true);

            }
        };

    }

    private ToggleButton setToggleOption(ToggleGroup group, String operatorName, FontIcon icon) {
        String operatorNameDisplay = operatorName.toUpperCase();
        icon.getStyleClass().add("card-graphic");
        ToggleButton op = new ToggleButton(operatorNameDisplay, icon);
        op.setToggleGroup(group);
        op.setUserData(operatorName.toLowerCase());
        op.setContentDisplay(ContentDisplay.TOP);
        op.getStyleClass().add("card-toggle");
        return op;
    }
}


