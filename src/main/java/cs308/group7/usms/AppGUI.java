package cs308.group7.usms;

import cs308.group7.usms.controller.*;
import javafx.application.Application;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Map;

public class AppGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Get the database connection once to "preload" it
        System.out.println("Connecting to database...");
        try { App.getDatabaseConnection(); }
        catch (RuntimeException e) {
            errorOnInitialise(e.getMessage());
            return;
        }

        try {

            Map<String, String> user;
            while (true) {
                LoginController login = new LoginController();
                user = login.response();
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

        } catch (RuntimeException e) { // On exit
            System.out.println("Closing database connection...");
            App.closeDatabaseConnection();
            System.out.println("Closing program...");
            System.exit(0);
        }

    }

    /**
     * Displays an error message in a modal dialog box
     * @param errorMessage The error message to display
     */
    private void errorOnInitialise(String errorMessage) {
        DialogPane modalDialog = new DialogPane();

        Text modalText = new Text(errorMessage);
        VBox content = new VBox(modalText);
        modalDialog.setContent(content);

        Dialog<Void> modal = new Dialog<>();
        modal.setDialogPane(modalDialog);

        Window modalWindow = modal.getDialogPane().getScene().getWindow();
        modalWindow.setOnCloseRequest(windowEvent -> modalWindow.hide());

        modal.showAndWait();
    }

}
