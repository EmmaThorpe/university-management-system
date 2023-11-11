package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import org.jetbrains.annotations.Nullable;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Mark {
    private final String userID;
    private final String moduleID;
    private final int attemptNo;
    private Double labMark = null;
    private Double examMark = null;

    /**
     * Creates a new Mark object from the database
     * @param userID The student's user ID
     * @param moduleID The specific module the student wants to know marks for
     * @param attemptNo The attempt number for the specific marks for that module
     * @throws SQLException If the query fails
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
            this.labMark = res.getDouble("Lab");
            this.examMark = res.getDouble("Exam");
        }
    }

    /**
     * Creates a new Mark object from the given parameters without checking the database
     */
    public Mark(String userID, String moduleID, int attemptNo, Double labMark, Double examMark) {
        this.userID = userID;
        this.moduleID = moduleID;
        this.attemptNo = attemptNo;
        this.labMark = labMark;
        this.examMark = examMark;
    }

    @Nullable
    public Double getLabMark() { return labMark; }


    /**
     * Sets the lab mark of the student for this module and attempt
     * @return Whether the lab mark was set successfully
     */
    public boolean setLabMark(Double lab) {
        Map<String, String> values = new HashMap<>();
        values.put("Lab", String.valueOf(lab));

        try {
            DatabaseConnection db = App.getDatabaseConnection();
            int res = db.update("Mark", values, new String[]{"ModuleID = '"+ moduleID +"' AND UserID = '"+ userID +"' AND AttNo = '"+ attemptNo +"'"});
            if (res > 0) {
                this.labMark = lab;
                return true;
            } else {
                values.put("ModuleID", db.sqlString(moduleID));
                values.put("UserID", db.sqlString(userID));
                values.put("AttNo", String.valueOf(attemptNo));
                if (db.insert("Mark", values) > 0) {
                    this.labMark = lab;
                    return true;
                } else {
                    throw new SQLException("Couldn't insert mark entry!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to set lab mark for student " + userID + " (attempt #" + attemptNo + " in " + moduleID + ": " + e.getMessage());
            return false;
        }
    }


    @Nullable
    public Double getExamMark() { return examMark; }


    /**
     * Sets the exam mark of the student for this module and attempt
     * @return Whether the exam mark was set successfully
     */
    public boolean setExamMark(Double exam) {
        Map<String, String> values = new HashMap<>();
        values.put("Exam", String.valueOf(exam));

        try {
            DatabaseConnection db = App.getDatabaseConnection();
            int res = db.update("Mark", values, new String[]{"ModuleID = '"+ moduleID +"' AND UserID = '"+ userID +"' AND AttNo = '"+ attemptNo +"'"});
            if (res > 0) {
                this.examMark = exam;
                return true;
            } else {
                values.put("ModuleID", db.sqlString(moduleID));
                values.put("UserID", db.sqlString(userID));
                values.put("AttNo", String.valueOf(attemptNo));
                if (db.insert("Mark", values) > 0) {
                    this.examMark = exam;
                    return true;
                } else {
                    throw new SQLException("Couldn't insert mark entry!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to set exam mark for student " + userID + " (attempt #" + attemptNo + " in " + moduleID + ": " + e.getMessage());
            return false;
        }
    }


    public int getAttemptNo()  { return attemptNo; }
}
