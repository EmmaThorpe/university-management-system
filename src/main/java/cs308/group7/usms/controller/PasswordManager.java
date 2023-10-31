package cs308.group7.usms.controller;

import cs308.group7.usms.ui.LoginUI;
import cs308.group7.usms.ui.ManagerUI;
import cs308.group7.usms.ui.StudentUI;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordManager extends UIController{
    LoginUI loginUI;

    public PasswordManager(Stage currentStage) {
        super(currentStage);
        loginUI = new LoginUI();
        goToLogin();
    }

    public HashMap<String, String> response(){
        return null;
    }


    public void goToSignUp(){
        displayScene(loginUI.signUpScene());
    }


    public void goToLogin() {
        displayScene(loginUI.loginScene(()->goToSignUp(), ()->attemptLogin()));
    }

    public void goToUnactivated() {
        displayScene(loginUI.unactivatedAccount(()->goToLogin()));
    }

    public Map<String, String> attemptLogin (){
        List<TextField> textfields = loginUI.getCurrentTextFields();
        List<Text> text = loginUI.getCurrentText();
        TextField email = textfields.get(0);
        TextField password =textfields.get(1);
        Text validHandler = text.get(0);

        Map<String, String> result = login(email.getText(), password.getText());
        if (result == null) {
            validHandler.setText("Incorrect Details");
        }else if(result.get("activated").equals("True")){
            return result;

        }else{
            goToUnactivated();
        }
        return null;
    }





    /** Uses the database to check if a sign in is successful or not
     * @param email The email the user enters
     * @param password The password the user enters
     * @return A Map representing if the sign in was successful. Null is returned on details not being right and a map containing the userID, the role and if the user is activated or not is returned if the details are correct.
     */
    public Map<String,String> login(String email, String password){
        Map<String,String> user= new HashMap<>();
        user.put("userID", "idk2?");
        if(email.equals("student") && password.equals("a")){
            user.put("role", "Student");
            user.put("activated", "True");

        }else if(email.equals("lecturer") && password.equals("a")){
            user.put("role", "Lecturer");
            user.put("activated", "True");

        }else if(email.equals("manager") && password.equals("a")){
            user.put("role", "Manager");
            user.put("activated", "True");

        }else if(email.equals("unactivated") && password.equals("a")){
            user.put("role", "Student");
            user.put("activated", "False");

        }else{
            return null;
        }
        System.out.println(user.toString());

        return user;
    }



}
