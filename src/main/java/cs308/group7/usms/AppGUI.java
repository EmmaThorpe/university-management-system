package cs308.group7.usms;

import cs308.group7.usms.controller.*;
import cs308.group7.usms.ui.MainUI;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Map;

public class AppGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException {
        Map<String, String> user;
        boolean exiting = false;
        while (!exiting) {
            PasswordManager pass = new PasswordManager();
            user = pass.response();
            switch (user.get("role")) {
                case "Lecturer":
                    LecturerController lec = new LecturerController(user.get("UserID"));
                    break;
                case "Student":
                    StudentController stu = new StudentController(user.get("UserID"));
                    break;
                case "Manager":
                    ManagerController man = new ManagerController(user.get("UserID"));
                    break;
            }
        }
    }

}
