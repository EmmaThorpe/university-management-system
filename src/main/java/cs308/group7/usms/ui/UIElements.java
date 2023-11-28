package cs308.group7.usms.ui;

import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.commons.validator.routines.EmailValidator;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *  UI Elements sets out component definitions
 *  for model specific UI items, form
 *  validations (covering listeners,
 *  form error displays, ...), and
 *  layouts for user contexts, like
 *  dashboards
 */
public class UIElements extends MainUI{

    private ImageView pdfImg;

    /*
    FORM VALIDATION
    */

    /**
     * Creates the form for setting a new password
     * @param manager Represents if the user is a manager changing
     *                an account's password, and applies that action's
     *                specific context to called upon methods
     * @return A VBox that contains the fields of setting and confirming a
     *         password, and can be used to construct the reset password
     *         form
     */
    protected VBox resetPass(Boolean manager) {
        validFields = new HashMap<>();
        VBox setPass = textAndField("NEW PASSWORD", passwordCheck("NEW PASSWORD", manager, false));
        VBox confirmPass = textAndField("CONFIRM NEW PASSWORD",
                confirmPasswordCheck("CONFIRM NEW PASSWORD", "NEW PASSWORD", manager, false));

        return new VBox(setPass, confirmPass);
    }

    /**
     * Creates the form for resetting a user's own password
     * @return A VBox that contains the fields of setting and confirming a
     *         password, and can be used to construct the reset password
     *         form
     */
    public VBox resetPassUser() {
        VBox oldPass = inputField("OLD PASSWORD", true);
        return new VBox(oldPass, resetPass(false));
    }

    /**
     * Validates that a new password follows conventions of a set length
     * and the validPassword rules set up by the method, <code>validPassword</code>
     * @param field    The password field
     * @param manager  Represents if the user is a manager changing
     *                 an account's password, and applies that action's
     *                 specific context to called upon methods
     * @param signup  Represents if the user is choosing a password
     *                 for signup, and applies that action's
     *                 specific context to called upon methods
     * @return  A listener that can be attached to a password field
     *          to ensure that the user input is validated against
     *          these rules
     */
    protected ChangeListener<String> passwordCheck(String field, Boolean manager, Boolean signup){
        return (obs, oldText, newText) -> {
            if (newText.length() <8 || newText.length()>20) {
                currentText.get(field).setText("Passwords must be between 8 and 20 characters long");
            } else if (!validPassword(newText, currentText.get(field))) {
                checkValidPasswordFields(field, false, manager, signup);
            }else{
                currentText.get(field).setText("");
                checkValidPasswordFields(field, true, manager, signup);
            }
        };

    }

    /**
     * Validates that when creating a new password, the user's input
     * is valid within the rule that to confirm their new password,
     * they repeat the input accurately
     * @param field             The password field
     * @param fieldToMatch    The password field that is to be matched
     * @param manager  Represents if the user is a manager changing
     *                 an account's password, and applies that action's
     *                 specific context to called upon methods
     * @param signup  Represents if the user is choosing a password
     *                 for signup, and applies that action's
     *                 specific context to called upon methods
     * @return        A listener that can be attached to a password field
     *                to ensure that the user input is validated against
     *                these rules
     */
    protected ChangeListener<String> confirmPasswordCheck(String field, String fieldToMatch, Boolean manager,
                                                          Boolean signup){
        return (obs, oldText, newText) -> {
            TextField newPassField = (TextField) currentFields.get(fieldToMatch);
            if (!newText.equals(newPassField.getText())) {
                currentText.get(field).setText("Passwords must match");
                checkValidPasswordFields(field, false, manager, signup);
            } else {
                currentText.get(field).setText("");
                checkValidPasswordFields(field, true, manager, signup);
            }
        };

    }

    /** Sets out the rules on what makes a valid password by checking that
     * a user's input has special character(s), number(s), and
     * a mix of uppercase and lowercase characters.
     * @param password The user inputted password
     * @param output   <code>true</code> when the password passes the
     *                 requirements, and <code>false</code> otherwise
     * @return
     */
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

    /**
     * Checks if the inputs for password fields are valid for submission and
     * enables user input once that is done
     * @param type      The field that is being validated
     * @param value     The user's value inputted for that field
     * @param manager  Represents if the user is a manager changing
     *                 an account's password, and applies that action's
     *                 specific context to called upon methods
     * @param signup  Represents if the user is choosing a password
     *                 for signup, and applies that action's
     *                 specific context to called upon methods
     */
    private void checkValidPasswordFields(String type, Boolean value, Boolean manager, Boolean signup){
        boolean disabled;
        validFields.put(type, value);

        if(manager){
            disabled = !validFields.get("NEW PASSWORD") || !validFields.get("CONFIRM NEW PASSWORD");
            currentButtons.get("RESET USER PASSWORD").setDisable(disabled);
        } else if(signup) {
            disabled = !validFields.get("PASSWORD") || !validFields.get("CONFIRM PASSWORD");
            currentButtons.get("SUBMIT").setDisable(disabled);
        }
        else{
            disabled = !validFields.get("NEW PASSWORD") || !validFields.get("CONFIRM NEW PASSWORD");
            currentButtons.get("CHANGE PASSWORD").setDisable(disabled);
        }

    }

    /** Checks the length of a field in a form
     * @param minLength - the minimum allowed amount of characters
     * @param maxLength - the maximum allowed amount of characters
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     * @return        A listener that can be attached to a field
     *                to ensure that the user input is validated against
     *                these rules
     */
    protected ChangeListener<String> lengthCheck(int minLength, int maxLength, String field,
                                                 String fieldName, String model,
                                                 String manipulation){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty() || newText.length() < minLength || newText.length() >maxLength) {
                currentText.get(field).setText(fieldName + " must be between " + minLength
                        + " and " + maxLength + " characters long");
                checkFields(model, field, false, manipulation);
                }
            else {
                currentText.get(field).setText("");
                checkFields(model, field, true, manipulation);
            }
        };
    }


    /** Checks that a field in a form is within a number range
     * @param minValue - the minimum number value for the field
     * @param maxValue - the maximum number value for the field
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     * @return        A listener that can be attached to a field
     *                to ensure that the user input is validated against
     *                these rules
     */
    protected ChangeListener<String> rangeCheck(int minValue, int maxValue, String field,
                                                 String fieldName, String model,
                                                 String manipulation){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty()
                    || !(newText.matches("\\d+"))
                    || Integer.parseInt(newText) < minValue
                    || Integer.parseInt(newText) >maxValue) {
                currentText.get(field).setText(fieldName + " must be between " + minValue
                        + " and " + maxValue);
                checkFields(model, field, false, manipulation);
            }
            else {
                currentText.get(field).setText("");
                checkFields(model, field, true, manipulation);
            }
        };
    }

    /** Checks that a field in a form is within the mark format
     * @param minValue - the minimum mark value for that field
     * @param maxValue - the maximum mark value for that field
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param markType - the type of mark being set (eg: "EXAM", "LAB")
     * @return        A listener that can be attached to a mark field
     *                to ensure that the user input is validated against
     *                these rules
     */
    protected ChangeListener<String> markCheck(double minValue, double maxValue, String field,
                                                String fieldName, String model,
                                                String markType){
        return (obs, oldText, newText) -> {
            if (newText.isEmpty()
                    || !(newText.matches("^\\d*\\.?\\d+|\\d+\\.\\d*$"))
                    || Double.parseDouble(newText) < minValue
                    || Double.parseDouble(newText) >maxValue) {
                currentText.get(field).setText(fieldName + " must be between " + minValue
                        + " and " + maxValue);
                if (model.equals("MARK")) {
                    checkValidMarkFields(field, false, markType);
                }
            }
            else {
                currentText.get(field).setText("");
                if (model.equals("MARK")) {
                    checkValidMarkFields(field, true, markType);
                }
            }
        };
    }

    /** Checks the date of a field in a form
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     * @return        A listener that can be attached to a field
     *                to ensure that the user input is validated against
     *                these rules
     */
    protected ChangeListener<String> dateCheck(String field, String fieldName, String model, String manipulation){
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return (observableVal, oldVal, newVal) -> {
            if (!newVal.matches(datePattern)) {
                currentText.get(field).setText(fieldName + " must be a date formatted as YYYY-MM-DD");
                checkFields("SIGNUP", field, false, manipulation);
            } else {
                currentText.get(field).setText("");
                checkFields("SIGNUP", field, true, manipulation);
            }
        };
    }
    /** Checks the date of a field in a form
     * @param field - the field the validation is occuring on
     * @param fieldName - the name of the field (eg: "Password", "Name", etc)
     * @param model - the model the form is within (eg: "COURSE", "MODULE")
     * @param manipulation - the way the form data is being applied to with the model (eg: EDIT, ADD)
     * @return        A listener that can be attached to a field
     *                to ensure that the user input is validated against
     *                these rules
     */
    protected ChangeListener<String> emailCheck(String field, String fieldName, String model, String manipulation){
        return (obs, oldText, newText) -> {
            if (!EmailValidator.getInstance().isValid(newText)) {
                currentText.get(field).setText(fieldName + " is not in a valid Email format");
                checkFields(model, field, false, manipulation);
            } else if (newText.length() > 254) {
                currentText.get(field).setText(fieldName + " must be less than or equal to 254 characters");
                checkFields(model, field, false, manipulation);
            } else {
                currentText.get(field).setText("");
                checkFields(model, field, true, manipulation);
            }
        };

    }

    /**
     * Checks if the inputs for fields are valid for submission and
     * enables user input once that is done
     * @param model     A string representation of
     *                  the model that these user inputs will be
     *                  applied to (COURSE, MODULE, ...)
     * @param field      The field that is being validated
     * @param value     The user's value inputted for that field
     * @param manipulation  A string representing how the user input
     *                      is to be applied to the model (EDIT, ADD, ...).
     *                      Can be null/empty if no manipulation is occuring
     *                      on the model, such as the case with signup
     */
    private void checkFields(String model, String field, Boolean value, String manipulation){
        switch (model) {
            case "COURSE":
                checkValidCourseFields(field, value, manipulation);
                break;
            case "MODULE":
                checkValidModuleFields(field, value, manipulation);
                break;
            case "SIGNUP":
                checkValidSignupFields(field, value);
                break;
        }
    }

    /**
     * Checks if the inputs for signup fields are valid for submission and
     * enables user input once that is done
     * @param type      The field that is being validated
     * @param value     If the user input is valid for that field
     */
    protected void checkValidSignupFields(String type, Boolean value){
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

    /**
     * Checks if the inputs for module fields are valid for submission and
     * enables user input once that is done
     * @param type      The field that is being validated
     * @param value     If the user input is valid for that field
     * @param manipulation  A string representing how the user input
     *                      is to be applied to the model (EDIT, ADD, ...)
     */
    private void checkValidModuleFields(String type, Boolean value, String manipulation){
        boolean disabled;
        validFields.put(type, value);
        disabled = !validFields.get(manipulation + " CODE")
                || !validFields.get(manipulation + " NAME")
                || !validFields.get(manipulation + " DESCRIPTION")
                || !validFields.get(manipulation + " CREDITS")
        ;
        currentButtons.get(manipulation).setDisable(disabled);
    }

    /**
     * Checks if the inputs for course fields are valid for submission and
     * enables user input once that is done
     * @param type      The field that is being validated
     * @param value     If the user input is valid for that field
     * @param manipulation  A string representing how the user input
     *                      is to be applied to the model (EDIT, ADD, ...)
     */
    private void checkValidCourseFields(String type, Boolean value, String manipulation){
        boolean disabled;
       validFields.put(type, value);
        disabled = !validFields.get(manipulation + " CODE")
                || !validFields.get(manipulation + " NAME")
                || !validFields.get(manipulation + " DESCRIPTION")
                || !validFields.get(manipulation + " LEVEL OF STUDY")
                || !validFields.get(manipulation + " LENGTH OF COURSE")
        ;
        currentButtons.get(manipulation).setDisable(disabled);
    }

    /**
     * Checks if the inputs for mark fields are valid for submission and
     * enables user input once that is done
     * @param type      The field that is being validated
     * @param value     If the user input is valid for that field
     * @param formType  The form type i.e. ASSIGN or CHANGE
     */
    private void checkValidMarkFields(String type, Boolean value, String formType){
        boolean disabled;
        validFields.put(type, value);
        disabled = !validFields.get(formType+ " EXAM MARK") || !validFields.get(formType+ " LAB MARK");
        currentButtons.get(formType+" MARK").setDisable(disabled);
    }

    /*
    LAYOUT
    */

    /**
     * Creates a toolbar view
     * @param title  The string representation of the page the user is on
     * @return      A HBox that will be used at the top of a user's view
     *              as a toolbar that contains their current page and the actions
     *              to log out and return to their main dashboard
     */
    protected HBox makeToolbar(String title) {
        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.GRADUATION_CAP);
        StackPane iconStack = makeCircleIcon(25, "toolbar-back" ,appGraphic, "toolbar-graphic");

        Text toolbarTitle = new Text(title.toUpperCase());
        toolbarTitle.setTranslateY(5.0);
        toolbarTitle.getStyleClass().add("toolbar-title");

        HBox titleContainer = new HBox(iconStack, toolbarTitle);
        titleContainer.setPadding(new Insets(10));
        titleContainer.setSpacing(10);


        Button homeBtn = inputButton("HOME");
        homeBtn.getStyleClass().add("toolbar-btn");
        Button logoutBtn = inputButton("LOG OUT");
        logoutBtn.getStyleClass().add("toolbar-btn");
        HBox.setMargin(logoutBtn, new Insets(10));

        HBox logoutContainer = new HBox(homeBtn, logoutBtn);
        logoutContainer.setAlignment(Pos.CENTER);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox container = new HBox(titleContainer, region, logoutContainer);
        container.setPadding(new Insets(15));
        container.setSpacing(50);
        container.getStyleClass().add("toolbar-bar");

        return container;
    }

    /**
     * Creates a container for lists of buttons
     * @param btnsList  A group of buttons to be listed together
     * @return          A VBox that will be used to show a list
     *                  of button actions to a user
     */
    protected VBox createButtonsVBox(ArrayList<Button> btnsList){
        Button[] btns = btnsList.toArray(new Button[0]);
        btns = stylePanelActions(btns);

        VBox btnView = new VBox(btns);

        btnView.setAlignment(Pos.CENTER);
        btnView.setSpacing(20.0);
        btnView.setPadding(new Insets(10));

        return btnView;
    }

    /**
     * Creates a dashboard view
     * @param actionBtns    A group of buttons that represents the
     *                      actions a user can take
     * @param toolbar       A toolbar that appears at the top of the
     *                      view so the user can log out, return to
     *                      their dashboard, and view their user type
     */
    protected void createDashboard(Button[] actionBtns, HBox toolbar){
        actionBtns = stylePanelActions(actionBtns);

        VBox mainActionPanel = makePanel(new VBox(actionBtns));
        mainActionPanel.setAlignment(Pos.CENTER);

        HBox actionPanel = new HBox(mainActionPanel);
        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        panelLayout(actionPanel, toolbar);
    }

    /**
     * Constructs a single panel layout in which the panel displays
     * when the model has no values
     * @param model     The input model that is empty
     * @return          A VBox that will be used to show the
     *                  notice that the model is empty to the user
     */
    protected VBox emptyModelContent(String model) {
        Text emptyNotice = new Text("No " + model);
        emptyNotice.getStyleClass().add("empty-notice-text");
        VBox emptyContainer = new VBox(emptyNotice);
        emptyContainer.setAlignment(Pos.CENTER);

        VBox emptyDisplay = infoContainer(emptyContainer);
        emptyDisplay.setSpacing(20.0);
        emptyDisplay.setPadding(new Insets(10));
        return emptyDisplay;
    }

    /* Panel layouts */

    /**
     * Constructs a panel layout
     * @param actionPanel   The panel(s) that make up the view.
     * @param toolbar       A toolbar that appears at the top of the
     *                      view so the user can log out, return to
     *                      their dashboard, and view their user type
     */
    private void panelLayout(Pane actionPanel, HBox toolbar){
        BorderPane root = new BorderPane(actionPanel);
        root.setTop(toolbar);
        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(actionPanel, new Insets(15));

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }

    /**
     * Constructs a single panel layout
     * @param main      A main panel that is centered in the view.
     *                  This panel can act as an action list,
     *                  a list viewer, report information, ...
     * @param title     The title of the page that replaces the
     *                  user type display in the toolbar and tells
     *                  the user where about they are in the app
     */
    protected void singlePanelLayout(VBox main, String title){
        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(main);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        panelLayout(actionPanel, toolbar);
    }

    /**
     * Constructs a two panel layout
     * @param left      The left panel.
     *                  This panel can act as an action list,
     *                  a list viewer, report information, ...
     * @param right     The right panel. This panel can be
     *                  spawned from left panel actions
     *                  and used to display additional information,
     *                  but also serve the same purpose as a left
     *                  panel
     * @param title     The title of the page that replaces the
     *                  user type display in the toolbar and tells
     *                  the user where about they are in the app
     */
    protected void twoPanelLayout(VBox left, VBox right, String title){

        HBox toolbar = makeToolbar(title);

        HBox actionPanel = new HBox(left, right);

        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setSpacing(20.0);
        HBox.setHgrow(actionPanel, Priority.ALWAYS);

        panelLayout(actionPanel, toolbar);
    }

    /**
     * Constructs a three panel layout
     * @param left      The left panel.
     *                  This panel can act as an action list,
     *                  a list viewer, report information, ...
     * @param right     The right panel. This panel can be
     *                  spawned from left panel actions
     *                  and used to display additional information,
     *                  but also serve the same purpose as a left
     *                  panel
     * @param top     The top panel. This panel is smaller in width
     *                compared to the right and left panel and
     *                therefore its action is better suited to
     *                reporting information over listing
     * @param title     The title of the page that replaces the
     *                  user type display in the toolbar and tells
     *                  the user where about they are in the app
     */
    protected void threePanelLayout(VBox left, VBox right, VBox top, String title){

        HBox toolbar = makeToolbar(title);

        HBox topActionPanel = new HBox(top);
        HBox bottomActionPanel = new HBox(left, right);

        topActionPanel.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(topActionPanel, Priority.ALWAYS);

        bottomActionPanel.setAlignment(Pos.CENTER);
        bottomActionPanel.setSpacing(20.0);
        HBox.setHgrow(bottomActionPanel, Priority.ALWAYS);

        VBox actionPanel = new VBox(topActionPanel, bottomActionPanel);

        actionPanel.setSpacing(10);

        panelLayout(actionPanel, toolbar);
    }

    /*
    MODEL ELEMENTS
    */

    /* Module Elements */

    /**
     * Creates a list button view for a module
     * @param id    A string representation of the module ID
     * @param name  A string representation of the module name
     * @param credit    A string representation of how many credits the module is worth
     * @return          A HBox that represents a list button - a view used to show
     *                  the details of a certain modules so that it can be picked out
     *                  in a list of other list buttons of the same model
     */
    protected HBox makeModuleListButton(String id, String name, String credit) {
        HBox yearsDisplay = listDetail("CREDITS" , credit);
        Text nameDisplay = new Text(name);

        VBox courseDetails = new VBox(nameDisplay, yearsDisplay);
        courseDetails.setSpacing(5.0);
        return makeListButton(id, new FontIcon(FontAwesomeSolid.CHALKBOARD), courseDetails);
    }

    /** Creates an information display for a module
     * @param tempModule    A map of the module with its fields and values
     * @return              A VBox that reports all the module's fields and
     *                      their values and shows it to the user
     */
    protected VBox moduleDetailDisplay(Map<String, String> tempModule) {
        Text idTitle = new Text(tempModule.get("Name"));
        idTitle.getStyleClass().add("info-box-title");

        String creditsValue = tempModule.get("Credit");
        HBox col1 = new HBox (
                listDetail("NAME", tempModule.get("Name")),
                listDetail("CREDITS", creditsValue)
        );
        VBox col2 = infoDetailLong("DESCRIPTION", tempModule.get("Description"));

        VBox col3 = infoDetailLong("LECTURER(S)", tempModule.get("Lecturers"));

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2, col3);
    }

    /**
     * Content for the editModule action's popup
     * @param currentModule     The module that triggered this popup
     * @return                  A VBox that contains the form for
     *                          editing the current module's fields
     *                          that will be contained within
     *                          the "EDIT MODULE" popup modal
     */
    protected VBox editModule(Map<String, String> currentModule) {
        VBox setCode = setTextAndField("EDIT CODE", currentModule.get("Id"),
                lengthCheck(1,5,"EDIT CODE", "Code", "MODULE", "EDIT"),true);
        VBox setName = setTextAndField("EDIT NAME", currentModule.get("Name"),
                lengthCheck(1,50,"EDIT NAME", "Name", "MODULE", "EDIT"),true);
        VBox setDesc = setLongTextAndField("EDIT DESCRIPTION", currentModule.get("Description"),
                lengthCheck(1,100,"EDIT DESCRIPTION", "Description", "MODULE", "EDIT"));
        VBox setCredit = setTextAndField("EDIT CREDITS", currentModule.get("Credit"),
                rangeCheck(10, 60,"EDIT CREDITS", "Credits", "MODULE", "EDIT"),true);

        return new VBox(setCode, setName, setDesc, setCredit);
    }

    /* Course Elements */

    /** Creates an information display for a course
     * @param tempCourse    A map of the course with its fields and values
     * @return              A VBox that reports all the course's fields and
     *                      their values and shows it to the user
     */
    protected VBox courseDetailDisplay(Map<String, String> tempCourse) {
        Text idTitle = new Text(tempCourse.get("Name"));
        idTitle.getStyleClass().add("info-box-title");

        VBox col1 = infoDetailLong("DESCRIPTION", tempCourse.get("Description"));


        HBox col2 = new HBox(
                listDetail("LEVEL", tempCourse.get("Level")),
                listDetail("COURSE LENGTH", tempCourse.get("Years"))
        );

        HBox col3 = new HBox(listDetail("Department", tempCourse.get("Department")));

        col1.setSpacing(5);
        col2.setSpacing(5);
        return new VBox(idTitle, col1, col2, col3);
    }

    /* User/Account Elements */

    /** Creates a list button view for a user
     * @param userID    String representing a user's ID
     * @param fname     String representing a user's first name
     * @param lname     String representing a user's last name
     * @param userType  String representing a user's type
     * @param activated String representing if a user is activated or not
     * @return          A HBox that represents a list button - a view used to show
     *                  the details of a certain user so that it can be picked out
     *                  in a list of other list buttons of the same model
     */
    protected HBox makeUserListButton(String userID, String fname, String lname, String userType,
                                    String activated) {
        Text nameDisplay = new Text(fname + " " + lname);

        HBox activatedDisplay = new HBox();
        if (activated.equals("ACTIVATED")) {
            activatedDisplay.getChildren().add(activeDetail(activated, true));
        } else {
            activatedDisplay.getChildren().add(activeDetail(activated, false));
        }

        FontIcon appGraphic;
        if (userType.equals("STUDENT")) {
            appGraphic =  new FontIcon(FontAwesomeSolid.USER);
        } else if (userType.equals("LECTURER")) {
            appGraphic =  new FontIcon(FontAwesomeSolid.CHALKBOARD_TEACHER);
        } else {
            appGraphic = new FontIcon(FontAwesomeSolid.USER_TIE);
        }

        VBox userDetails = new VBox(nameDisplay, activatedDisplay);
        userDetails.setSpacing(5.0);

        return makeListButton(userID, appGraphic, userDetails);
    }

    /** Creates an information display for a user
     * @param userID    The value of the user's ID
     * @param managerID     The value of the user's manager's ID
     * @param forename     The value of the user's first name
     * @param surname       The value of the user's last name
     * @param email         The value of the user's email
     * @param dob           The value of the user's date of birth
     * @param gender        The value of the user's gender
     * @param userType      The value of the user's type
     * @param activated     The value of the user's activation status
     * @return              A VBox that reports all the user's fields and
     *                      their values and shows it to the user
     */
    protected VBox userDetailDisplay(String userID, String managerID, String forename, String surname, String email,
                             String dob, String gender, String userType, String activated) {
        Text idTitle = new Text(userID);
        idTitle.getStyleClass().add("info-box-title");

        VBox row1 = new VBox(
                listDetail("FULL NAME", (forename + " " + surname)),
                listDetail("EMAIL", email),
                listDetail("DOB", dob),
                listDetail("GENDER", gender)

        );

        VBox row2 = new VBox(
                listDetail("USER TYPE", userType),
                listDetail("STATUS", activated),
                listDetail("MANAGED BY", managerID)
        );

        row1.setSpacing(5);
        row2.setSpacing(5);
        HBox rows = new HBox(row1, row2);
        return new VBox(idTitle, rows);
    }


    /* Student mark Elements */

    /** Creates a list button view for a student's mark
     * @param userID    String representing a student's ID
     * @param fname     String representing a student's first name
     * @param lname     String representing a student's last name
     * @param labMark   String representing a student's lab mark
     * @param examMark  String representing a student's exam mark
     * @return          A HBox that represents a list button - a view used to show
     *                  the details of a certain student mark so that
     *                  it can be picked out
     *                  in a list of other list buttons of the same model
     */
    protected HBox makeStudentMarkListButton(String userID, String fname, String lname, String labMark,
                                      String examMark) {
        Text nameDisplay = new Text(fname + " " + lname);

        HBox examDisplay = listDetail("EXAM" , examMark);
        HBox labDisplay = listDetail("LAB" , labMark);

        HBox markDetails = new HBox(labDisplay, examDisplay);
        markDetails.setSpacing(5.0);

        VBox userDetails = new VBox(nameDisplay, markDetails);
        userDetails.setSpacing(5.0);

        return makeListButton(userID, new FontIcon(FontAwesomeSolid.USER), userDetails);
    }

    /**  Creates an information display for a student's mark
     * @param userID    Value of a student's ID
     * @param fname     Value of a student's first name
     * @param lname     Value of a student's last name
     * @param labMark   Value of a student's lab mark
     * @param examMark  Value of a student's exam mark
     * @return              A VBox that reports all the student mark's details
     *                      and shows it to the user
     *
     */
    protected VBox studentMarkDisplay(String userID, String fname, String lname, String labMark,
                                      String examMark) {
        Text idTitle = new Text(userID);
        idTitle.getStyleClass().add("info-box-title");

        VBox row = new VBox(
                listDetail("FULL NAME", (fname + " " + lname)),
                listDetail("LAB MARK", labMark),
                listDetail("EXAM MARK", examMark)
        );

        row.setSpacing(5);
        return new VBox(idTitle, row);
    }

    /** Creates a list button view for a mark
     * @param moduleID  String representing the mark's module's ID
     * @param lab       String representing the mark's lab mark value
     * @param exam      String representing the mark's exam mark value
     * @param attempt   String representing the mark's attempt number
     * @param grade     String representing if the mark is a pass, a fail or
     *                  indeterminate based on the module and the business rules
     * @return          A HBox that represents a list button - a view used to show
     *                  the details of a certain mark so that it can be picked out
     *                  in a list of other list buttons of the same model
     */
    /* Mark and Decision Elements */
    protected HBox makeMarkList(String moduleID, String lab, String exam, String attempt,
                                String grade) {
        HBox examDisplay = listDetail("EXAM" , exam);
        HBox labDisplay = listDetail("LAB" , lab);

        HBox markDetails = new HBox(labDisplay, examDisplay);
        markDetails.setSpacing(5.0);

        HBox attemptDisplay = listDetail("ATTEMPT", attempt);
        HBox gradeDisplay = new HBox();
        if (grade.equals("PASS")) {
            gradeDisplay.getChildren().add(activeDetail(grade, true));
        } else {
            gradeDisplay.getChildren().add(activeDetail(grade, false));
        }

        HBox decisionDetails = new HBox(attemptDisplay, gradeDisplay);
        decisionDetails.setSpacing(5.0);

        FontIcon appGraphic =  new FontIcon(FontAwesomeSolid.AWARD);

        VBox studentMarkDetails = new VBox(markDetails, decisionDetails);
        studentMarkDetails.setSpacing(5.0);

        return makeListButton(moduleID, appGraphic, studentMarkDetails);
    }

    /** Displays the decision made
     * @param decision  String representing the decision that has been
     *                  made: AWARD, RESIT, WITHDRAWAL or N/A (for
     *                  unset)
     * @return          A VBox that displays what decision has
     *                  been issued
     */
    protected VBox decisionDisplay(String decision) {
        Text title = new Text("DECISION: ");
        Text decisionMade = new Text(decision);
        decisionMade.getStyleClass().add("decision-text");
        title.getStyleClass().add("decision-text");

        switch (decision.toUpperCase()) {
            case "AWARD" -> decisionMade.getStyleClass().add("decision-award");
            case "WITHDRAWAL" -> decisionMade.getStyleClass().add("decision-withdraw");
            case "RESIT" -> decisionMade.getStyleClass().add("decision-resit");
            default -> decisionMade.getStyleClass().add("decision-na");
        }

        HBox decisionDisplay = new HBox(title, decisionMade);
        decisionDisplay.setSpacing(5);
        return new VBox(decisionDisplay);

    }

    /* Material Elements */

    /** Creates a list button view for a material's week
     * @param weekNo    Number to represent a week's number
     * @return          A HBox that represents a list button - a view used to show
     *                  the details of a certain week so that it can be picked out
     *                  in a list of other list buttons of the same model
     */
    protected HBox makeWeekButton(int weekNo) {
        Text nameDisplay = new Text("Week " + weekNo);

        VBox weekDetails = new VBox(nameDisplay);
        weekDetails.setSpacing(5.0);
        return makeListButton(null, new FontIcon(FontAwesomeSolid.CHALKBOARD), weekDetails);
    }


    protected VBox weekButtons2Sem(List<Map<String, Boolean>> materialList, VBox rightPanel, VBox materialDetails,
                                   VBox panelSem1, VBox panelSem2){
        ToggleGroup semSelected = new ToggleGroup();
        ToggleButton sem1 = setToggleOption(semSelected, "Semester 1");
        ToggleButton sem2 = setToggleOption(semSelected, "Semester 2");
        sem1.setSelected(true);

        HBox semOptions = styleToggleOptions(sem1, sem2);

        panelSem1.setSpacing(20.0);
        panelSem1.setPadding(new Insets(10, 2, 10, 2));

        panelSem2.setSpacing(20.0);
        panelSem2.setPadding(new Insets(10, 2, 10, 2));

        ScrollPane weekListPanel = new ScrollPane();

        semSelected.selectedToggleProperty().addListener(toggleSem(semSelected, weekListPanel, panelSem1, panelSem2, rightPanel));

        return new VBox(semOptions, makeScrollablePanel(weekListPanel));
    }

    /** Creates a view for showing a PDF for a module's week's material
     * @param file  The file (PDF) that has the materials to display
     */
    public void displayPDF(File file){
        resetCurrentValues();

        Document pdf = showPage(file);

        int amount = pdf.getNumberOfPages()-1;

        Text pageNo = paginationText("PAGE NO");

        currentText.get("PAGE NO").setText("1");
        pdfImg = new ImageView(pdfToImg(pdf, 0));

        Text pageDisplay = new Text("PAGE");
        pageDisplay.getStyleClass().add("pagination-text");
        HBox paginationDisplay = new HBox(pageDisplay, pageNo);
        paginationDisplay.setSpacing(5);

        Button backBtn = inputButton("<-");
        backBtn.getStyleClass().add("panel-button-1");

        Button forwardBtn = inputButton("->");
        forwardBtn.getStyleClass().add("panel-button-2");

        backBtn.setOnAction(event -> backPage(pdf));
        forwardBtn.setOnAction(event -> forwardPage(pdf, amount));

        VBox pdfDisplay = new VBox(new ScrollPane(pdfImg));
        BorderPane root = new BorderPane(pdfDisplay);
        HBox toolbar = makeToolbar("Notes");

        root.setTop(toolbar);
        root.setBottom(paginationDisplay);

        root.setLeft(backBtn);
        root.setRight(forwardBtn);

        BorderPane.setMargin(toolbar, new Insets(15));
        BorderPane.setMargin(pdfDisplay, new Insets(15));

        root.setPadding(new Insets(10));

        currScene = new Scene(root);
    }


    /** Pagination for a module's week's material when it is being viewed,
     *  functioning as a back button
     * @param pdf   The document that is being displayed
     */
    protected void backPage(Document pdf){
        int page = Integer.parseInt(currentText.get("PAGE NO").getText());
        if(page>1){
            pdfImg.setImage(pdfToImg(pdf, page-2));
            currentText.get("PAGE NO").setText(String.valueOf(page-1));
        }

    }

    /** Pagination for a module's week's material when it is being viewed,
     *  functioning as a forward button
     * @param pdf   The document that is being displayed
     * @param amount    The document's total number of pages
     */
    protected void forwardPage(Document pdf, int amount){
        int page = Integer.parseInt(currentText.get("PAGE NO").getText());

        if(page <= amount){
            pdfImg.setImage(pdfToImg(pdf, page));
            currentText.get("PAGE NO").setText(String.valueOf(page+1));
        }
    }

    /** Display text for the pagination for a module's week's material
     * @param text  The text to be styled for pagination display
     * @return      Text that is styled for a pagination display
     */
    protected Text paginationText(String text){
        Text inputText = new Text();
        inputText.getStyleClass().add("pagination-text");
        currentText.put(text, inputText);
        return inputText;
    }


    /**
     * Gets the document that is to be displayed for a module's week's content
     * @return A document that can be used in the PDF viewer to show
     *          material
     */
    protected Document showPage(File file) {
        Document currentDocument = new Document();
        try {
            currentDocument.setFile(file.getAbsolutePath());
            return currentDocument;


        } catch (PDFException | PDFSecurityException | IOException e) {
            return null;
        }



    }

    /** Converts a document to an image view for displaying materials
     * @param currentDocument   The document to render into image view
     * @param page              The page to render into image view
     * @return                  The image view that consists of the document contents
     *
     */
    protected WritableImage pdfToImg(Document currentDocument, int page){
        float scale = 1f;

        try {
            BufferedImage image = (BufferedImage) currentDocument.getPageImage(page,
                    GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0f, scale);

            return SwingFXUtils.toFXImage(image, null);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    /**Toggles the semesters button and flips the week buttons
     * @param semSelected - the current sem selected
     * @param weekContent - current week content
     * @param sem1Content - the content of semester 1
     * @param sem2Content - the content of semester 2
     * @param rightPanel - the right hand side panel
     * @return - Change listener that handles the toggling of semesters
     */
    protected ChangeListener<Toggle> toggleSem(ToggleGroup semSelected, ScrollPane weekContent, VBox sem1Content, VBox sem2Content, VBox rightPanel){
        return (observableValue, currentToggle, newToggle) -> {
            if (semSelected.getSelectedToggle().getUserData() == "Semester 1"){
                weekContent.contentProperty().set(sem1Content);
            } else {
                weekContent.contentProperty().set(sem2Content);

            }

            rightPanel.setVisible(false);
        };

    }

}
