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
import java.util.Map;

public class PasswordManager extends UIController implements EventHandler{
    LoginUI loginUI;

    public PasswordManager(Stage currentStage) {
        super(currentStage);
        loginUI = new LoginUI(this);
        displayScene(loginUI.loginScene());
    }


    /*public EventHandler<ActionEvent> goToSignUp () {
        return (arg0 -> signUpScene());
    }

    public EventHandler<ActionEvent> goToLogin () {
        return (arg0 -> loginScene());
    }

    public EventHandler<ActionEvent> logIn (TextField email, PasswordField password, Text validHandler){
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                Map<String, String> result = login(email.getText(), password.getText());
                //System.out.println(result.toString());
                if (result == null) {
                    validHandler.setText("Incorrect Details");
                }else if(result.get("activated").equals("True")){
                    switch(result.get("role")){
                        case("Student"):
                            System.out.println("s");
                            StudentUI stuUI = new StudentUI(result.get("userID"), currentStage);
                            System.out.println("a");
                            stuUI.home();
                            goToLogin();
                            break;
                        case("Lecturer"):
                            LecturerUI lecUI = new LecturerUI(result.get("userID"), currentStage);
                            lecUI.home();
                            goToLogin();
                            break;
                        case("Manager"):
                            ManagerUI manUI = new ManagerUI(result.get("userID"), currentStage);
                            manUI.home();
                            goToLogin();
                            break;
                    }
                }else{
                    unactivatedAccount();
                }
            }
        };

    }*/



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


    @Override
    public void handle(Event event) {
        displayScene(loginUI.signUpScene());
    }
}
