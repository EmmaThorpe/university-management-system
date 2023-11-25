package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.ui.LoginUI;
import cs308.group7.usms.utils.Password;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.sql.rowset.CachedRowSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static cs308.group7.usms.utils.Password.*;

public class LoginController {
    LoginUI loginUI;
    Map<String, String> user;

    /**
     * Constructor
     */
    public LoginController() {
        loginUI = new LoginUI();
        pageSetter("LOGIN", true);
    }

    /** responds to main with the user that has successfully been logged in
     * @return Map containing user info
     */
    public Map<String, String> response(){
        return user;
    }


    /** Sets the page and assigns the events that will occur when you press the buttons
     * @param page - the page being moved to
     * @param initial - if this is the initial page or not
     */
    public void pageSetter(String page, Boolean initial){
        Map<String, Button> buttons;
        switch (page){
            case "LOGIN":
                loginUI.loginScene();
                buttons =loginUI.getCurrentButtons();
                buttons.get("SUBMIT").setOnAction((event)->attemptLogin());
                buttons.get("NEW USER").setOnAction((event)->pageSetter("SIGN UP", false));
                break;
            case "SIGN UP":
                loginUI.signUpScene();
                buttons =loginUI.getCurrentButtons();
                buttons.get("SUBMIT").setOnAction((event)->attemptSignUp());
                buttons.get("RETURN TO LOGIN").setOnAction((event)->pageSetter("LOGIN", false));
                break;
        }
        if(initial){
            loginUI.displayFirstScene();
        }else{
            loginUI.displayScene();
        }

    }


    /**
     * Attempts to log in to system and moves page accordingly based on outcome
     */
    public void attemptLogin (){
        Map<String, Node> textfields = loginUI.getCurrentFields();
        Map<String, Text> text = loginUI.getCurrentText();
        TextField email = (TextField) textfields.get("EMAIL");
        TextField password = (TextField) textfields.get("PASSWORD");
        Text validHandler = text.get("OUTPUT");

        Map<String, String> result = login(email.getText(), password.getText());
        if (result.containsKey("error")) {
            validHandler.setText(result.get("error"));
            password.setText(""); // Clear password field
        } else if(result.get("activated").equals("true")){
            loginUI.hideStage();
            user=result;
        } else {
            loginUI.notificationScene("Sorry your account has not been activated yet.\r\nTry again later.","RETURN TO LOGIN", false);
            Map<String, Button> buttons =loginUI.getCurrentButtons();
            buttons.get("RETURN TO LOGIN").setOnAction((event)->pageSetter("LOGIN", false));
            loginUI.displayScene();
        }
    }



    /**
     * Attempts to sign in to system and moves page accordingly based on outcome
     */
    public void attemptSignUp(){
        Map<String, Node> textFields = loginUI.getCurrentFields();
        Map<String, Text> text = loginUI.getCurrentText();

        final String forename = ((TextField) textFields.get("FORENAME")).getText();
        final String surname = ((TextField) textFields.get("SURNAME")).getText();
        final String email = ((TextField) textFields.get("EMAIL")).getText();
        final String dob = ((TextField) textFields.get("DATE OF BIRTH")).getText();
        final String gender = ((ComboBox) textFields.get("GENDER")).getValue().toString();
        final String password = ((TextField) textFields.get("PASSWORD")).getText();

        // Determine whether the user is a lecturer by checking if the qualification field is non-empty
        final boolean isLecturerSignUp = textFields.get("QUALIFICATION").getAccessibleText() != null;

        final boolean success;
        if (isLecturerSignUp) {
            final String qualification = ((TextField) textFields.get("QUALIFICATION")).getText();
            success = signup(forename, surname, email, password, dob, gender, qualification);
        } else {
            success = signup(forename, surname, email, password, dob, gender);
        }

        if(success) {
            loginUI.notificationScene("\"Your account has successfully been created.\r\nContact the manager to get it activated.","RETURN TO LOGIN", true);
            Map<String, Button> buttons =loginUI.getCurrentButtons();
            buttons.get("RETURN TO LOGIN").setOnAction((event)->pageSetter("LOGIN", false));
            loginUI.displayScene();

        } else {
            text.get("output").setText("A user with this email already exists");
        }
    }


    /** Validates user login details and returns a map response
     * @return A map representing user login details.<br>
     *         If successful, will have the keys {@code UserID, role, activated}.<br>
     *         If unsuccessful, will have the key {@code error} containing an error message.
     */
    public Map<String,String> login(String email, String password){
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet res = db.select(
                    new String[]{"Users"},
                    new String[]{"UserID", "Type", "Activated", "Password"},
                    new String[]{"Email = " + db.sqlString(email)});
            if (!res.next()) {
                System.out.println("No user with email " + email + " found!");
                return Map.of("error", "Incorrect details provided.");
            }

            String encryptedPassword = res.getString("Password");
            if (!matches(password, encryptedPassword)) {
                System.out.println("User provided incorrect password!");
                return Map.of("error", "Incorrect password provided.");
            }

            Map<String,String> potentialUser= new HashMap<>();
            potentialUser.put("UserID", res.getString("UserID"));
            potentialUser.put("role", res.getString("Type"));
            potentialUser.put("activated", String.valueOf(res.getBoolean("Activated")));
            return potentialUser;
        } catch (SQLException e) {
            System.out.println("Failed to log in!: " + e.getMessage());
            return Map.of("error", "Could not connect to login server.");
        }
    }

    /**
     * Attempts to perform a signup for a student
     * @return Whether the signup was successful
     */
    public static boolean signup(String forename, String surname, String email, String unencryptedPassword,
                                 String dobVal, String gender) {
        final String type = "Student";
        final String userID = "S" + forename.charAt(0) + surname.substring(0, 3);
        final Date dob = Date.valueOf(dobVal);

        DatabaseConnection db = App.getDatabaseConnection();
        try {
            if (!signup_user(userID, email, forename, surname, unencryptedPassword, dob, gender, type)) return false;
            Map<String, String> values = new HashMap<>();
            values.put("UserID", db.sqlString(userID));
            values.put("CourseID", "NULL");
            values.put("Decision", db.sqlString("No Decision"));
            values.put("yearOfStudy", "1");
            int res = db.insert("Student", values);
            return res > 0;
        } catch (SQLException e) {
            System.out.println("Failed to insert student " + userID + " into database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Attempts to perform a signup for a lecturer
     * @return Whether the signup was successful
     */
    public static boolean signup(String forename, String surname, String email, String unencryptedPassword,
                                 String dobVal, String gender, String qualification) {
        final String type = "Lecturer";
        final String userID = "L" + forename.charAt(0) + surname.substring(0, 3);
        final Date dob = Date.valueOf(dobVal);

        DatabaseConnection db = App.getDatabaseConnection();
        try {
            if (!signup_user(userID, email, forename, surname, unencryptedPassword, dob, gender, type)) return false;
            Map<String, String> values = new HashMap<>();
            values.put("UserID", db.sqlString(userID));
            values.put("ModuleID", "NULL");
            values.put("Qualification", db.sqlString(qualification));
            int res = db.insert("Lecturer", values);
            return res > 0;
        } catch (SQLException e) {
            System.out.println("Failed to insert lecturer " + userID + " into database: " + e.getMessage());
            return false;
        }
    }

    private static boolean signup_user(String userID, String email, String forename, String surname, String unencryptedPassword, Date dob, String gender, String type) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        Map<String, String> values = new HashMap<>();
        values.put("UserID", db.sqlString(userID));
        values.put("Forename", db.sqlString(forename));
        values.put("Surname", db.sqlString(surname));
        values.put("Email", db.sqlString(email));
        values.put("Password", db.sqlString(Password.encrypt(unencryptedPassword)));
        values.put("DoB", db.sqlString(String.valueOf(dob)));
        values.put("Gender", db.sqlString(gender));
        values.put("Type", db.sqlString(type));
        values.put("Activated", db.sqlString(String.valueOf(0)));
        int res = db.insert("Users", values);
        return res > 0;
    }

}
