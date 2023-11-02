package cs308.group7.usms;

import cs308.group7.usms.controller.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;

public class AppGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<String, String> user;
        boolean exiting = false;
        while (!exiting) {
            PasswordManager pass = new PasswordManager();
            user = pass.response();
            switch (user.get("role")) {
                case "Lecturer":
                    UIController lec = new LecturerController(user.get("UserID"));
                    break;
                case "Student":
                    UIController stu = new StudentController(user.get("UserID"));
                    break;
                case "Manager":
                    UIController man = new ManagerController(user.get("UserID"));
                    break;
            }
        }
    }

}
