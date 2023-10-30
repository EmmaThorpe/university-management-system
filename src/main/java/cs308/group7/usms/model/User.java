package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;

public class User {

    private final int userID;
    private final int managedBy;
    private final String forename;
    private final String surname;
    private final String email;
    private final String gender;
    private boolean activated;

    /**
     * Creates a new User object from the database
     * @param userID The ID of the user to create
     * @throws SQLException If the user does not exist
     */
    public User(int userID) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Users"}, null, new String[]{"UserID = " + userID});
        this.userID = res.getInt("UserID");
        this.managedBy = res.getInt("ManagedBy");
        this.forename = res.getString("Forename");
        this.surname = res.getString("Surname");
        this.email = res.getString("Email");
        this.gender = res.getString("Gender");
        this.activated = res.getBoolean("Activated");
    }

    /**
     * Creates a new User object from the given parameters without checking the database
     */
    public User(int userID, int managerID, String forename, String surname, String email, String gender, boolean activated) {
        this.userID = userID;
        this.managedBy = managerID;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.activated = activated;
    }

    public int getUserID() { return userID; }

    /**
     * Checks if the user is a manager
     * @return Whether the user is a manager, or false in the event of an error
     */
    public boolean isManager() {
        try (CachedRowSet res = App.getDatabaseConnection().select(new String[]{"Users"}, new String[]{"COUNT(UserID) AS NumberOfUsers"}, new String[]{"ManagedBy = " + userID})) {
            return res.getInt("NumberOfUsers") > 0;
        } catch (SQLException e) {
            System.out.println("Failed to check if user " + userID + " is a manager!");
            return false;
        }
    }

    /**
     * Gets the manager of the user
     * @throws SQLException If the manager does not exist
     */
    public User getManager() throws SQLException { return new User(managedBy); }

    public String getForename() { return forename; }

    public String getSurname() { return surname; }

    public String getEmail() { return email; }

    public String getGender() { return gender; }

    public boolean getActivated() { return activated; }

    /**
     * Sets the user to activated
     * @return Whether the operation was successful
     */
    public boolean setActivated() {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("Activated", "true");
        try {
            db.update("Users", values, new String[]{"UserID = " + userID});
            return activated = true;
        } catch (SQLException e) {
            System.out.println("Failed to set user " + userID + " to activated!");
            return false;
        }
    }

    public void changePassword(String newPass) {
        // TODO: Implement this
    }

}
