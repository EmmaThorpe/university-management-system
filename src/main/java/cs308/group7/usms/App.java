package cs308.group7.usms;

import javafx.application.Application;

import static javafx.application.Application.launch;

public class App{

    public static DatabaseConnection databaseConnection;
    public static SystemOperations systemOperations;

    public static void main(String[] args) {
        databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
        systemOperations = new SystemOperations(databaseConnection);
        //studentUI stu = new studentUI("tom");
        Application.launch(loginUI.class, args);
    }
}