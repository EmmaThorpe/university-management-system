package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import java.util.Map;
import java.util.HashMap;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

public class Student extends User {

    public enum StudentDecision {
        AWARD,
        RESIT,
        WITHDRAWAL
    }

    private String courseID;
    private final int YearOfStudy;
    private StudentDecision decision;

    /**
     * Creates a new Student object from the database
     * @param userID The student's user ID
     * @throws SQLException If the student does not exist
     */
    public Student(String userID) throws SQLException {
        super(userID);
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Student"}, null, new String[]{"UserID = '" + userID + "'"});
        if (res.next()) {
            this.courseID = res.getString("CourseID");
            this.YearOfStudy = res.getInt("YearOfStudy");
            this.decision = studentDecisionFromString(res.getString("Decision"));
        } else {
            throw new SQLException("Student " + userID + " does not exist!");
        }
    }

    /**
     * Creates a new Student object from the given parameters, getting generic user information from the database
     */
    public Student(String userID, String courseID, int YearOfStudy, StudentDecision decision) throws SQLException {
        super(userID);
        this.courseID = courseID;
        this.YearOfStudy = YearOfStudy;
        this.decision = decision;
    }

    /**
     * Gets the course of the student
     * @throws SQLException If the course does not exist
     */
    public Course getCourse() throws SQLException { return new Course(courseID); }

    /**
     * Sets the course of the student
     * @return Whether the course was set successfully
     */
    public boolean setCourse(String courseID) {
        Map<String, String> values = new HashMap<>();
        values.put("CourseID", "'" + courseID + "'");

        try {
            int res = App.getDatabaseConnection().update("Student", values, new String[]{"UserID = '" + this.getUserID() + "'"});
            if (res > 0) {
                this.courseID = courseID;
                return true;
            } else {
                throw new SQLException("Student " + this.getUserID() + " does not exist!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to set course for student " + this.getUserID() + "!");
            return false;
        }
    }

    public Mark getMark(String moduleID, int attemptNumber) throws SQLException { return new Mark(this.getUserID(), moduleID, attemptNumber); }

    public StudentDecision getDecision() {
        return decision;
    }

    /**
     * Converts a StudentDecision to a string
     */
    public static String stringFromStudentDecision(StudentDecision decision) {
        return switch (decision) {
            case AWARD -> "Award";
            case RESIT -> "Resit";
            case WITHDRAWAL -> "Withdrawal";
        };
    }

    /**
     * Converts a string to a StudentDecision
     */
    public static StudentDecision studentDecisionFromString(String decision) {
        return switch (decision) {
            case "Award" -> StudentDecision.AWARD;
            case "Resit" -> StudentDecision.RESIT;
            case "Withdrawal" -> StudentDecision.WITHDRAWAL;
            default -> throw new IllegalArgumentException("Invalid decision string!");
        };
    }

    /**
     * Issues an award to the student
     * @return Whether the award was issued successfully
     */
    public boolean issueAward() { return setDecision(StudentDecision.AWARD); }

    /**
     * Issues a resit to the student
     * @return Whether the resit was issued successfully
     */
    public boolean issueResit() { return setDecision(StudentDecision.RESIT); }

    /**
     * Issues a withdrawal to the student
     * @return Whether the withdrawal was issued successfully
     */
    public boolean issueWithdrawal() { return setDecision(StudentDecision.WITHDRAWAL); }

    private boolean setDecision(StudentDecision decision) {
        Map<String, String> values = new HashMap<>();
        values.put("Decision", "'" + stringFromStudentDecision(decision) + "'");

        try {
            int res = App.getDatabaseConnection().update("Student", values, new String[]{"UserID = '" + this.getUserID() + "'"});
            if (res > 0) {
                this.decision = decision;
                return true;
            } else {
                throw new SQLException("Student " + this.getUserID() + " does not exist!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to set decision for student " + this.getUserID() + "!");
            return false;
        }
    }

    public int getYearOfStudy() { return YearOfStudy; }

}
