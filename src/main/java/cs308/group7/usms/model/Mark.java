package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Mark {
    private final String userID;
    private final String moduleID;
    private final int attemptNo;
    private double labMark;
    private double examMark;

    /**
     * Creates a new Mark object from the database
     * @param userID The student's user ID
     * @param moduleID The specific module the student wants to know marks for
     * @param attemptNo The attempt number for the specific marks for that module
     * @throws SQLException If the student does not exist
     */
    public Mark(String userID, String moduleID, int attemptNo) throws SQLException {
        this.userID = userID;
        this.moduleID = moduleID;
        this.attemptNo = attemptNo;

        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Mark"},
                null,
                new String[]{"ModuleID = '"+ moduleID +"' AND UserID = '"+ userID +"' AND AttNo = '"+ attemptNo +"'"});
        if (res.first()) {
            this.labMark = res.getFloat("Lab");
            this.examMark = res.getFloat("Exam");
        } else {
            throw new SQLException("There is no mark for the student " + userID +
                    " for the attempt number " + attemptNo + " for the module " + moduleID);
        }
    }

    /**
     * Creates a new Mark object from the given parameters without checking the database
     */
    public Mark(String userID, String moduleID, int attemptNo, int labMark, int examMark) {
        this.userID = userID;
        this.moduleID = moduleID;
        this.attemptNo = attemptNo;
        this.labMark = labMark;
        this.examMark = examMark;
    }
    
    public String getModuleID() { return moduleID; }

    public String getUserID() { return userID; }

    public double getLabMark() { return labMark; }


    /**
     * Sets the lab mark of the student for this module and attempt
     * @return Whether the lab mark was set successfully
     */
    public boolean setLabMark(double lab) {
        Map<String, String> values = new HashMap<>();
        values.put("Lab", "'" + lab + "'");

        try {
            int res = App.getDatabaseConnection().update("Mark", values, new String[]{"ModuleID = '"+ moduleID +"' AND UserID = '"+ userID +"' AND AttNo = '"+ attemptNo +"'"});
            if (res > 0) {
                this.labMark = lab;
                return true;
            } else {
                throw new SQLException("There is no lab mark for the student " + userID +
                        " for the attempt number " + attemptNo + " for the module " + moduleID);
            }
        } catch (SQLException e) {
            System.out.println("Failed to set lab mark for student " + userID + "!");
            return false;
        }
    }


    public double getExamMark() { return examMark; }


    /**
     * Sets the exam mark of the student for this module and attempt
     * @return Whether the exam mark was set successfully
     */
    public boolean setExamMark(double exam) {
        Map<String, String> values = new HashMap<>();
        values.put("Exam", "'" + exam + "'");

        try {
            int res = App.getDatabaseConnection().update("Mark", values, new String[]{"ModuleID = '"+ moduleID +"' AND UserID = '"+ userID +"' AND AttNo = '"+ attemptNo +"'"});
            if (res > 0) {
                this.examMark = exam;
                return true;
            } else {
                throw new SQLException("There is no exam mark for the student " + userID +
                        " for the attempt number " + attemptNo + " for the module " + moduleID);
            }
        } catch (SQLException e) {
            System.out.println("Failed to set exam mark for student " + userID + "!");
            return false;
        }
    }


    public int getAttemptNo()  { return attemptNo; }
}
