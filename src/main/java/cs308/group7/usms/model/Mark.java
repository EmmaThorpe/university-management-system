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
    private int labMark;
    private int examMark;

    // Where part of update and select queries is the same so added variable to store it
    private final String queryWhere;

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
        queryWhere = "UserID = '" + userID + "' AND ModuleID = '" + moduleID + "' AND AttNo = '" + attemptNo +"'";
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Mark"},
                null,
                new String[]{queryWhere});
        if (res.next()) {
            this.labMark = res.getInt("Lab");
            this.examMark = res.getInt("Exam");
        } else {
            throw new SQLException("There is no mark for the student " + userID +
                    " for the attempt number " + attemptNo + " for the module " + moduleID);
        }
    }

    /**
     * Gets the lab mark of the student for that module and attempt number
     * @throws SQLException If the mark does not exist
     */
    public int getLabMark() throws SQLException { return labMark; }

    /**
     * Sets the lab mark of the student for this module and attempt
     * @return Whether the lab mark was set successfully
     */
    public boolean setLabMark(int lab) {
        Map<String, String> values = new HashMap<>();
        values.put("Lab", "'" + lab + "'");

        try {
            int res = App.getDatabaseConnection().update("Mark", values, new String[]{queryWhere});
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

    /**
     * Gets the exam mark of the student for that module and attempt number
     * @throws SQLException If the mark does not exist
     */
    public int getExamMark() throws SQLException { return examMark; }

    /**
     * Sets the exam mark of the student for this module and attempt
     * @return Whether the exam mark was set successfully
     */
    public boolean setExamMark(int exam) {
        Map<String, String> values = new HashMap<>();
        values.put("Exam", "'" + exam + "'");

        try {
            int res = App.getDatabaseConnection().update("Mark", values, new String[]{queryWhere});
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

    /**
     * Gets the attempt number of the student for this module
     * @throws SQLException If the attempt number does not exist
     */
    public int getAttemptNo() throws SQLException { return attemptNo; }
}
