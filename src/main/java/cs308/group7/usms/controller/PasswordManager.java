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
        displayScene(loginUI.signUpScene());
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
     * Attempts to log in to system and moves page accordingly based on outcome
     */
    public void attemptLogin (){
        List<TextField> textfields = loginUI.getCurrentTextFields();
        List<Text> text = loginUI.getCurrentText();
        TextField email = textfields.get(0);
        TextField password =textfields.get(1);
        Text validHandler = text.get(0);

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



}
