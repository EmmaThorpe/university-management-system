package cs308.group7.usms;

import cs308.group7.usms.controller.PasswordManager;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.ui.LoginUI;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;

import java.util.Stack;

import static javafx.application.Application.launch;

public class App extends Application {
    public static DatabaseConnection databaseConnection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage currentStage = primaryStage;
        String css = this.getClass().getResource("/css/style.css").toExternalForm();

        PasswordManager passwordM = new PasswordManager(currentStage);


    }


    public static void main(String[] args) {
        // Create database pool
        try {
            //databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
            launch(args);
        } catch (Exception e) {
            System.out.println("There was an error creating database pool!: " + e);
            System.exit(65);
        }

        // Application code (sample for demonstration)
        try (CachedRowSet res = databaseConnection.select(new String[]{"Course"}, new String[]{"Name", "Description"}, null)) {
            while (res.next()) {
                System.out.println(res.getString("Name") + ": " + res.getString("Description"));
            }
        } catch (Exception e) {
            System.out.println("There was an error querying the database!: " + e);
        }

        // Close database pool
        databaseConnection.close();
    }



}