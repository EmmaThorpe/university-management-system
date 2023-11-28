package cs308.group7.usms.model.businessRules;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.Mark;
import cs308.group7.usms.model.Student;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BusinessRule {

    public enum RuleType {
        MAX_RESITS,
        MAX_COMPENSATED_MODULES
    }

    protected final int ruleID;
    protected final boolean active;
    protected final RuleType type;
    protected int value;

    protected BusinessRule(int ruleID, boolean active, RuleType type, int value) {
        this.ruleID = ruleID;
        this.active = active;
        this.type = type;
        this.value = value;
    }

    /* STATIC METHODS */

    /**
     * Gets the next available RuleID
     * @throws SQLException If the query fails
     */
    protected static int getNewRuleID() throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"BusinessRule"}, new String[]{"MAX(RuleID) AS MaxRuleID"}, null);
        res.next();
        return res.getInt("MaxRuleID") + 1;
    }

    /**
     * Creates a new business rule in the database without linking it to any module or course or updating previous rules.
     * @throws SQLException If the query fails
     * @see CourseBusinessRule#createRule(String, RuleType, int) CourseBusinessRule.createRule()
     * @see ModuleBusinessRule#createRule(String, RuleType, int) ModuleBusinessRule.createRule()
     */
    protected static void createNewRuleEntry(int ruleID, RuleType type, int value) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        Map<String, String> values = new HashMap<>();
        values.put("RuleID", Integer.toString(ruleID));
        values.put("Active", "TRUE");
        values.put("Type", db.sqlString(type.name()));
        values.put("Value", Integer.toString(value));
        int rowsInserted = db.insert("BusinessRule", values);

        if (rowsInserted == 0) throw new SQLException("Failed to create business rule!");
    }

    /**
     * Gets all business rules that were applied to a given mark at the time of its achievement.
     * In the event of no rules of a given type being applied, strict default rules will be added.
     * @param courseID The ID of the course the module belongs to
     * @param mark The mark to get the rules for
     * @return A list of both course & module business rules that were applied to the mark
     * @throws SQLException If the query fails
     */
    public static List<BusinessRule> getRules(String courseID, Mark mark) throws SQLException {
        List<BusinessRule> rules = new ArrayList<>();

        CachedRowSet res = getRulesQuery("BusinessRuleModule", "ModuleID", mark.getModuleID(), mark);
        while (res.next()) {
            int ruleID = res.getInt("RuleID");
            boolean active = res.getBoolean("Active");
            RuleType type = RuleType.valueOf(res.getString("Type"));
            int value = res.getInt("Value");
            rules.add(new ModuleBusinessRule(ruleID, active, mark.getModuleID(), type, value));
        }

        res = getRulesQuery("BusinessRuleCourse", "CourseID", courseID, mark);
        while (res.next()) {
            int ruleID = res.getInt("RuleID");
            boolean active = res.getBoolean("Active");
            RuleType type = RuleType.valueOf(res.getString("Type"));
            int value = res.getInt("Value");
            rules.add(new CourseBusinessRule(ruleID, active, courseID, type, value));
        }

        // If there are no MAX_RESITS rules applied at all, add a default one to the module with value 0
        final boolean HAS_MAX_RESITS_MODULE_RULE = rules.stream().anyMatch(rule -> rule.getType() == RuleType.MAX_RESITS);
        if (!HAS_MAX_RESITS_MODULE_RULE) rules.add(new ModuleBusinessRule(-1, true, mark.getModuleID(), RuleType.MAX_RESITS, 0));

        // If there is no MAX_COMPENSATED_MODULES rule, add a default one to the course with value 0
        final boolean HAS_MAX_COMPENSATED_MODULES_COURSE_RULE = rules.stream().anyMatch(rule -> rule.getType() == RuleType.MAX_COMPENSATED_MODULES);
        if (!HAS_MAX_COMPENSATED_MODULES_COURSE_RULE) rules.add(new CourseBusinessRule(-1, true, courseID, RuleType.MAX_COMPENSATED_MODULES, 0));

        return rules;
    }

    /**
     * Gets the rule records from the given table that apply to the given mark
     * @throws SQLException If the query fails
     * @see #getRules(String, Mark) getRules()
     */
    private static CachedRowSet getRulesQuery(String specificTable, String specificColumn, String specificValue, Mark mark) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        return db.executeQuery(
                "SELECT BusinessRule.RuleID, BusinessRule.Type, BusinessRule.Active, BusinessRule.Value " +
                "FROM BusinessRuleApplication " +
                "INNER JOIN BusinessRule ON BusinessRuleApplication.RuleID = BusinessRule.RuleID " +
                "WHERE BusinessRuleApplication.UserID = " + db.sqlString(mark.getUserID()) + " AND BusinessRuleApplication.ModuleID = " + db.sqlString(mark.getModuleID()) + " AND BusinessRuleApplication.AttNo = " + mark.getAttemptNo() + " " +
                "AND EXISTS (SELECT RuleID FROM " + specificTable + " WHERE " + specificTable + "." + specificColumn + " = " + db.sqlString(specificValue) + " AND " + specificTable + ".RuleID = BusinessRule.RuleID)"
        );
    }

    /* INSTANCE METHODS */

    public int getRuleID() { return ruleID; }

    public boolean isActive() { return active; }

    public RuleType getType() { return type; }

    public int getValue() { return value; }

    /**
     * Sets the value of this business rule. This will retroactively change the rule for <b>every mark</b> that this rule is applied to.
     * To change the value for every <em>future</em> mark (until overwritten again), use
     * <br>{@link CourseBusinessRule#createRule(String, RuleType, int) CourseBusinessRule.createRule()} or
     * <br>{@link ModuleBusinessRule#createRule(String, RuleType, int) ModuleBusinessRule.createRule()} instead.
     * @param value The new value to set
     * @return Whether the value was set successfully
     */
    public boolean setValue(int value) {
        DatabaseConnection db = App.getDatabaseConnection();
        Map<String, String> values = new HashMap<>();
        values.put("Value", String.valueOf(value));
        try {
            if (db.update("BusinessRule", values, new String[]{"RuleID = " + ruleID}) == 1) {
                this.value = value;
                return true;
            } else {
                throw new SQLException("Business rule does not exist!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to change value of business rule #" + this.ruleID + "!: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the given student passes this business rule
     */
    abstract public boolean passes(Student student) throws SQLException;

    public String toString() { return type.name() + ": " + value + " (Rule #" + ruleID + ")" + (active ? "" : " [INACTIVE]"); }

}
