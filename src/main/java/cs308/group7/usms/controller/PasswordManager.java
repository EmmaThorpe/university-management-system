package cs308.group7.usms.controller;

import cs308.group7.usms.ui.LoginUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class PasswordManager {
    LoginUI loginUI;
    Map<String, String> user;

    /**
     * Constructor
     */
    public PasswordManager() {
        loginUI = new LoginUI();
        pageSetter("LOGIN", true);
    }

    /** responds to main with the user that has successfully been logged in
     * @return Map containing user info
     */
    public Map<String, String> response(){
        return user;
    }


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
        if (result == null) {
            validHandler.setText("Incorrect Details");
        }else if(result.get("activated").equals("True")){
            loginUI.hideStage();
            user=result;

        }else{
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

        if(signup(textFields)){
            loginUI.notificationScene("\"Your account has successfully been created.\r\nContact the manager to get it activated.","RETURN TO LOGIN", true);
            Map<String, Button> buttons =loginUI.getCurrentButtons();
            buttons.get("RETURN TO LOGIN").setOnAction((event)->pageSetter("LOGIN", false));
            loginUI.displayScene();

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
        if(email.equals("student") && password.equals("a")){
            potentialUser.put("UserID", "stu1");
            potentialUser.put("role", "Student");
            potentialUser.put("activated", "True");

        }else if(email.equals("lecturer") && password.equals("a")){
            potentialUser.put("UserID", "lec1");
            potentialUser.put("role", "Lecturer");
            potentialUser.put("activated", "True");

        }else if(email.equals("manager") && password.equals("a")){
            potentialUser.put("UserID", "mng1");
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
    public boolean signup(Map<String, Node> Details){
        return true;
    }



}
