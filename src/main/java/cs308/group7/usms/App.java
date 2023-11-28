package cs308.group7.usms;

import cs308.group7.usms.database.DatabaseConnection;

public class App {

    /** The directory that material files will be downloaded to and accessed from. */
    public static final String FILE_DIR = System.getProperty("user.dir");

    private static DatabaseConnection databaseConnection;

    public static void main(String[] args) {
        AppGUI.main(args);
    }

    public static DatabaseConnection getDatabaseConnection() {
        if (databaseConnection == null) {
            try {
                databaseConnection = new DatabaseConnection("dbConnect.txt");
            } catch (Exception e) {
                throw new RuntimeException("""
                    Could not connect to the database! Have you created a database connection file?
                    Please create a file called 'dbConnect.txt' in the same directory as this executable.
                    The program will need to be restarted to apply this change.
                    
                    The file should have the following format:
                    <url>
                    <database-name>
                    <username>
                    <password>
                    """);
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

}