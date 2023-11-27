package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.businessRules.BusinessRule;
import cs308.group7.usms.model.businessRules.CourseBusinessRule;
import cs308.group7.usms.model.businessRules.ModuleBusinessRule;
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
     * Creates a new Mark object from the database for the latest attempt
     * @param userID The student's user ID
     * @param moduleID The specific module the student wants to know marks for
     * @throws SQLException If the query fails
     */
    public Mark(String userID, String moduleID) throws SQLException {
        this.userID = userID;
        this.moduleID = moduleID;

        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.executeQuery("SELECT AttNo, Lab, Exam FROM Mark WHERE UserID = '"+ userID +"' AND ModuleID = '"+ moduleID +"' ORDER BY AttNo DESC LIMIT 1");
        if (res.first()) {
            this.attemptNo = res.getInt("AttNo");
            this.labMark = res.getDouble("Lab");
            if(res.wasNull()){
                this.labMark = null;
            }

            this.examMark = res.getDouble("Exam");
            if(res.wasNull()){
                this.examMark = null;
            }
        } else {
            attemptNo = 1; // First attempt, no marks yet
        }
    }

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
            if(res.wasNull()){
                this.labMark = null;
            }

            this.examMark = res.getDouble("Exam");
            if(res.wasNull()){
                this.examMark = null;
            }
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

    public String getModuleID() { return moduleID; }

    public String getUserID() { return userID; }

    @Nullable
    public Double getLabMark() { return labMark; }

    /**
     * Sets the lab mark of the student for this module and attempt.<br>
     * If an attempt is <em>being overwritten</em>, the business rules applied to it will <strong>not</strong> be changed.<br>
     * If an attempt is <em>being created</em>, the currently active relevant business rules will be applied.
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
                if (insertMark(values)) {
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
     * Sets the exam mark of the student for this module and attempt.<br>
     * If an attempt is <em>being overwritten</em>, the business rules applied to it will <strong>not</strong> be changed.<br>
     * If an attempt is <em>being created</em>, the currently active relevant business rules will be applied.
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
                if (insertMark(values)) {
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

    /**
     * Inserts a new mark entry into the database & adds the relevant business rule connections
     * @return Whether the mark was inserted successfully
     * @throws SQLException If the query fails
     * @see #setExamMark(Double) setExamMark()
     * @see #setLabMark(Double) setLabMark()
     */
    private boolean insertMark(Map<String, String> valueMap) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        if (db.insert("Mark", valueMap) > 0) {

            // Add connection for each course rule
            String courseID = new Student(userID).getCourseID();
            for (BusinessRule rule : CourseBusinessRule.getCourseRules(courseID, true))
                insertRuleConnection(rule);

            // Add connection for each module rule
            for (BusinessRule rule : ModuleBusinessRule.getModuleRules(moduleID, true))
                insertRuleConnection(rule);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Inserts a new rule-application connection into the database
     * @throws SQLException If the query fails
     * @see #insertMark(Map) insertMark()
     */
    private void insertRuleConnection(BusinessRule rule) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        Map<String, String> applicationRuleValues = new HashMap<>();
        applicationRuleValues.put("ModuleID", db.sqlString(moduleID));
        applicationRuleValues.put("UserID", db.sqlString(userID));
        applicationRuleValues.put("AttNo", Integer.toString(attemptNo));
        applicationRuleValues.put("RuleID", Integer.toString(rule.getRuleID()));
        db.insert("BusinessRuleApplication", applicationRuleValues);
    }

    /**
     * Returns the averaged mark of the student for this module and attempt, representing the overall mark for the module.
     * @throws IllegalStateException If either the lab or exam mark is null
     */
    public Double getOverallMark() throws IllegalStateException {
        if (labMark == null || examMark == null) throw new IllegalStateException("Cannot get average mark if mark is incomplete!");
        return (labMark + examMark) / 2.0;
    }

    public int getAttemptNo()  { return attemptNo; }

    /**
     * Checks if the mark passes the module
     * @throws IllegalStateException If the mark is incomplete
     */
    public boolean passes() {
        try { return getOverallMark() >= 50; }
        catch (IllegalStateException e) { return false; }
    }

    /**
     * Whether this mark can be compensated despite a fail when considering a student's award.
     */
    public boolean canBeCompensated() {
        try {
            final Double overallMark = getOverallMark();
            return overallMark >= 40 && overallMark < 50;
        } catch (IllegalStateException e) { return false; }
    }

}
