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

        HBox userOptions = styleToggleOptions(studentOp, lecturerOp);

        Label userFieldLabel = new Label("USER TYPE");
        VBox userField = new VBox(userFieldLabel, userOptions);
        userField.setPadding(new Insets(5));

        VBox emailField = textAndField("EMAIL", emailCheck("EMAIL", "Email", "SIGNUP", null));
        VBox forenameField = textAndField("FORENAME", lengthCheck(1, 20, "FORENAME", "Forename",
                "SIGNUP", null));
        VBox surnameField = textAndField("SURNAME", lengthCheck(1, 20, "SURNAME", "Surname",
                "SIGNUP", null));

        List<String> genderOptions = new ArrayList<>();
        genderOptions.add("Male");
        genderOptions.add("Female");
        genderOptions.add("Non-binary");
        genderOptions.add("Other/Prefer not to say");
        VBox genderField = dropdownField("GENDER", genderOptions);

        VBox dobField = textAndField("DATE OF BIRTH", dateCheck("DATE OF BIRTH", "Date of birth", "SIGNUP", null));
        VBox passwordField = textAndField("PASSWORD", passwordCheck("PASSWORD", false, true));
        VBox confirmPasswordField = textAndField("CONFIRM PASSWORD", confirmPasswordCheck("CONFIRM PASSWORD",
                "PASSWORD", false, true));
        VBox qualificationField = textAndField("QUALIFICATION", lengthCheck(1, 20, "QUALIFICATION", "Qualification",
                "SIGNUP", null));

        HBox.setHgrow(forenameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);
        HBox nameField = new HBox(forenameField, surnameField);
        nameField.setSpacing(10.0);

        HBox.setHgrow(genderField, Priority.ALWAYS);
        HBox.setHgrow(dobField, Priority.ALWAYS);
        HBox birthField = new HBox(genderField, dobField);
        birthField.setSpacing(10.0);

        VBox formFields = new VBox(userField, emailField, nameField, birthField, passwordField, confirmPasswordField);
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

        Text validHandler = inputText("OUTPUT");

        createScene("SIGN UP", formContent, new VBox(formBtns, validHandler));
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
                    checkValidSignupFields("STUDENT", false);
                } else {
                    VBox fields = (VBox) formContent.getContent();
                    fields.getChildren().remove(qualificationField);
                    formContent.contentProperty().set(fields);
                    checkValidSignupFields("STUDENT", true);
                }
            }
        };
    }



}




