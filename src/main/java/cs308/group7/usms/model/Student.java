package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.businessRules.BusinessRule;
import cs308.group7.usms.model.businessRules.CourseBusinessRule;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

public class Student extends User {

    public enum StudentDecision {
        AWARD,
        RESIT,
        WITHDRAWAL,
        NO_DECISION
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

    @Nullable
    public String getCourseID() { return courseID; }

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

    /**
     * Gets the most recent mark for the given module
     * @throws SQLException If the query fails
     */
    public Mark getMark(String moduleID) throws SQLException { return new Mark(this.getUserID(), moduleID); }

    /**
     * Gets the mark matching the given attempt number for the given module
     * @throws SQLException If the query fails
     */
    public Mark getMark(String moduleID, int attemptNumber) throws SQLException { return new Mark(this.getUserID(), moduleID, attemptNumber); }

    /**
     * Gets the number of attempts the student has made for the given module
     * @throws SQLException If the query fails
     */
    public int getNumberOfAttempts(String moduleID) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Mark"}, new String[]{"MAX(AttNo) AS MaxAttNo"}, new String[]{"ModuleID = '" + moduleID + "' AND UserID = '" + this.getUserID() + "'"});
        res.next();
        return res.getInt("MaxAttNo");
    }

    /**
     * Gets the business rules that the student has failed
     * @return The set of failed business rules, empty if none
     * @throws SQLException If the query fails
     */
    public Set<BusinessRule> getFailedBusinessRules() throws SQLException {
        Set<BusinessRule> failedRules = new HashSet<>();
        Set<BusinessRule> checkedCourseRules = new HashSet<>();

        // For each module in the course, check pass/fail for every business rule applied to its most recently achieved mark
        Course c = this.getCourse();
        List<Module> modules = c.getModules();
        for (Module module : modules) {
            Mark mark = this.getMark(module.getModuleID()); // Get the most recent mark for the module
            for (BusinessRule rule : BusinessRule.getRules(c.getCourseID(), mark)) {

                if (checkedCourseRules.stream().anyMatch(o -> rule.toString().equals(o.toString()))) continue; // skip if already checked

                if (!rule.passes(this)) failedRules.add(rule);

                if (rule instanceof CourseBusinessRule) checkedCourseRules.add(rule); // note checked course rules

            }
        }

        return failedRules;
    }

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
            case NO_DECISION -> "No Decision";
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
            case "No Decision" -> StudentDecision.NO_DECISION;
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

    /**
     * Issues an unset decision to the student
     * @return Whether the unset decision was issued successfully
     */
    public boolean issueNoDecision() { return setDecision(StudentDecision.NO_DECISION); }

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
