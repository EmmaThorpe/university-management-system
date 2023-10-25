package cs308.group7.usms;

import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.database.SystemOperations;
import cs308.group7.usms.ui.loginUI;
import javafx.application.Application;

import static javafx.application.Application.launch;

public class App{

    public static DatabaseConnection databaseConnection;
    public static SystemOperations systemOperations;

    public static void main(String[] args) {
        databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
        systemOperations = new SystemOperations(databaseConnection);
        Application.launch(loginUI.class, args);


    }
}