package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public User getManager() {
        try {
            return new User(managedBy);
        } catch (SQLException e) {
            System.out.println("Failed to get manager for user " + userID + "!");
            return null;
        }
    }

    public String getForename() { return forename; }

    public String getSurname() { return surname; }

    public String getEmail() { return email; }

    public String getGender() { return gender; }

    public boolean isActivated() { return activated; }

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
