package cs308.group7.usms.controller;

import cs308.group7.usms.ui.LoginUI;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordManager extends UIController{
    LoginUI loginUI;
    Map<String, String> user;

    /**
     * Constructor
     */
    public PasswordManager() {
        loginUI = new LoginUI();
        displayFirstScene(loginUI.loginScene(this::goToSignUp, this::attemptLogin));
    }

    /** responds to main with the user that has successfully been logged in
     * @return Map containing user info
     */
    public Map<String, String> response(){
        return user;
    }


    /**
     * Displays the signup page
     */
    public void goToSignUp(){
        displayScene(loginUI.signUpScene(this::attemptSignUp, this::goToLogin));
    }


    /**
     * Displays the login page
     */
    public void goToLogin() {
        displayScene(loginUI.loginScene(this::goToSignUp, this::attemptLogin));
    }

    /**
     * Displays the unactivated account page
     */
    public void goToUnactivated() {
        displayScene(loginUI.unactivatedAccount(this::goToLogin));
    }

    /**
     * Displays that an account has been created after successful sign up
     */
    public void goToCreated() {
        displayScene(loginUI.createdAccount(this::goToLogin));
    }


    /**
     * Attempts to log in to system and moves page accordingly based on outcome
     */
    public void attemptLogin (){
        Map<String, TextField> textfields = loginUI.getCurrentTextFields();
        Map<String, Text> text = loginUI.getCurrentText();
        TextField email = textfields.get("email");
        TextField password =textfields.get("password");
        Text validHandler = text.get("output");

        Map<String, String> result = login(email.getText(), password.getText());
        if (result == null) {
            validHandler.setText("Incorrect Details");
        }else if(result.get("activated").equals("True")){
            hideStage();
            user=result;

        }else{
            goToUnactivated();
        }
    }


    /**
     * Attempts to sign in to system and moves page accordingly based on outcome
     */
    public void attemptSignUp(){
        Map<String, TextField> textFields = loginUI.getCurrentTextFields();
        Map<String, Text> text = loginUI.getCurrentText();

        if(signup(textFields)){
            goToCreated();
        }else{
            text.get("output").setText("A user with this email already exists");
        }
    }


    /** Uses the database to check if a sign in is successful or not
     * @param email The email the user enters
     * @param password The password the user enters
     * @return A Map representing if the sign in was successful. Null is returned on details not being right and a map containing the userID, the role and if the user is activated or not is returned if the details are correct.
     */
    public Map<String,String> login(String email, String password){
        Map<String,String> potentialUser= new HashMap<>();
        potentialUser.put("UserID", "idk2?");
        if(email.equals("student") && password.equals("a")){
            potentialUser.put("role", "Student");
            potentialUser.put("activated", "True");

        }else if(email.equals("lecturer") && password.equals("a")){
            potentialUser.put("role", "Lecturer");
            potentialUser.put("activated", "True");

        }else if(email.equals("manager") && password.equals("a")){
            potentialUser.put("role", "Manager");
            potentialUser.put("activated", "True");

        }else if(email.equals("unactivated") && password.equals("a")){
            potentialUser.put("role", "Student");
            potentialUser.put("activated", "False");

        }else{
            return null;
        }

        return potentialUser;
    }

    /** Attempts to sign up user
     * @param Details Map containing the users' entered textfields along with key of what they represent
     * @return boolean indicating if signup is successful
     */
    public boolean signup(Map<String, TextField> Details){
        return true;
    }



}
