package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

public class User {

    public enum UserType {
        STUDENT,
        LECTURER,
        MANAGER
    }

    private final String userID;
    private final String managedBy;
    private final String forename;
    private final String surname;
    private final String email;
    private final Date dob;
    private final String gender;
    private final UserType type;
    private boolean activated;

    /**
     * Creates a new User object from the database
     * @param userID The ID of the user to create
     * @throws SQLException If the user does not exist or is otherwise invalid
     */
    public User(String userID) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Users"}, null, new String[]{"UserID = '" + userID + "'"});
        if (res.next()) {
            this.userID = res.getString("UserID");
            this.managedBy = res.getString("ManagedBy");
            this.forename = res.getString("Forename");
            this.surname = res.getString("Surname");
            this.email = res.getString("Email");
            this.dob = res.getDate("DOB");
            this.gender = res.getString("Gender");
            this.type = switch (res.getString("Type")) {
                case "Student" -> UserType.STUDENT;
                case "Lecturer" -> UserType.LECTURER;
                case "Manager" -> UserType.MANAGER;
                default -> throw new SQLException("Unexpected user type: " + res.getString("Type"));
            };
            this.activated = res.getBoolean("Activated");
        } else {
            throw new SQLException("User " + userID + " does not exist!");
        }
    }

    /**
     * Creates a new User object from the given parameters without checking the database
     */
    public User(String userID, String managerID, String forename, String surname, String email, Date dob, String gender, UserType type, boolean activated) {
        this.userID = userID;
        this.managedBy = managerID;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.type = type;
        this.activated = activated;
    }

    public String getUserID() { return userID; }

    /**
     * Checks if the user is a manager
     * @return Whether the user is a manager, or false in the event of an error
     */
    public boolean isManager() {
        try (CachedRowSet res = App.getDatabaseConnection().select(new String[]{"Users"}, new String[]{"COUNT(UserID) AS NumberOfUsers"}, new String[]{"ManagedBy = '" + userID + "'"})) {
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

    public Date getDOB() { return dob; }

    public String getGender() { return gender; }

    public UserType getType() { return type; }

    public boolean getActivated() { return activated; }

    /**
     * Sets the user to activated
     * @return Whether the operation was successful
     */
    public boolean setActivated() {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("Activated", "TRUE");
        try {
            return activated = db.update("Users", values, new String[]{"UserID = '" + userID + "'"}) > 0;
        } catch (SQLException e) {
            System.out.println("Failed to set user " + userID + " to activated!");
            return false;
        }
    }

    public void changePassword(String newPass) {
        // TODO: Implement this
    }

}
