package cs308.group7.usms;

import javax.sql.rowset.CachedRowSet;

public class App {

    public static DatabaseConnection databaseConnection;

    public static void main(String[] args) {

        // Create database pool
        try {
            databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
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