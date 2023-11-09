package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Lecturer extends User {

    private String moduleID;
    private final String qualification;

    /**
     * Creates a new Lecturer object from the database
     * @param userID The lecturer's user ID
     * @throws SQLException If the lecturer does not exist
     */
    public Lecturer(String userID) throws SQLException {
        super(userID);
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Lecturer"}, null, new String[]{"UserID = '" + userID + "'"});
        if (res.next()) {
            this.moduleID = res.getString("ModuleID");
            this.qualification = res.getString("Qualification");
        } else {
            throw new SQLException("Lecturer " + userID + " does not exist!");
        }
    }


    /**
     * Creates a new Lecturer object from the given parameters, getting generic user information from the database
     */
    public Lecturer(String userID, String moduleID, String qualification) throws SQLException{
        super(userID);
        this.moduleID = moduleID;
        this.qualification = qualification;
    }


    public String getQualification() { return qualification; }

    public Module getModule() throws SQLException {
        try {
            return new Module(moduleID);
        }
        catch (SQLException e) {
            System.out.println("Failed to find module " + moduleID + ".");
            throw new SQLException(e.getMessage() + " - " + this.getUserID() + "'s getModule failed");
        }
    }

    /**
     * Assigns the lecturer to a module
     * @throws SQLException If the module does not exist
     */
    public boolean assignModule(String moduleID) throws SQLException {
        Map<String, String> values = new HashMap<>();
        values.put("ModuleID", "'" + moduleID + "'");

        try {
            int res = App.getDatabaseConnection().update("Lecturer", values, new String[]{"UserID = '"+ this.getUserID() +"'"});
            if (res > 0) {
                this.moduleID = moduleID;
                return true;
            } else {
                throw new SQLException("Lecturer " + this.getUserID() + " does not exist!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to assign the module for the lecturer " + this.getUserID() + "!");
            return false;
        }
    }

}
