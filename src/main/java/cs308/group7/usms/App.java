package cs308.group7.usms;

import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;

public class App {

    /** The directory that material files will be downloaded to and accessed from. */
    public static final String FILE_DIR = System.getProperty("user.dir");

    private static DatabaseConnection databaseConnection;

    public static void main(String[] args) {
        // Create database pool (no longer necessary, is created as needed)
        // DatabaseConnection db = getDatabaseConnection();

        AppGUI.main(args);

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

}