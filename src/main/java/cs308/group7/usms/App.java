package cs308.group7.usms;

import cs308.group7.usms.controller.*;
import cs308.group7.usms.database.DatabaseConnection;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.util.Map;

public class App extends Application{
    public static DatabaseConnection databaseConnection;

    public static void main(String[] args) {
        // Create database pool (no longer necessary, is created as needed)
        // DatabaseConnection db = getDatabaseConnection();

        Application.launch(args);

        // Application code (sample for demonstration)
        try (CachedRowSet res = getDatabaseConnection().select(new String[]{"Course"}, new String[]{"Name", "Description"}, null)) {
            while (res.next()) {
                System.out.println(res.getString("Name") + ": " + res.getString("Description"));
            }
        } catch (Exception e) {
            System.out.println("There was an error querying the database!: " + e);
        }

        // Close database pool
        closeDatabaseConnection();
    }

    public static DatabaseConnection getDatabaseConnection() {
        if (databaseConnection == null) {
            try {
                databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
            } catch (Exception e) {
                System.out.println("There was an error creating database pool!: " + e);
                System.exit(65);
            }
        }
        return databaseConnection;
    }

    public static void closeDatabaseConnection() {
        if (databaseConnection != null) {
            databaseConnection.close();
        }
        databaseConnection = null;
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