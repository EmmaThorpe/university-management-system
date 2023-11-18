package cs308.group7.usms.model.businessRules;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.Course;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.model.Student;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CourseBusinessRule extends BusinessRule {

    private final String courseID;

    protected CourseBusinessRule(int ruleID, boolean active, String courseID, RuleType type, int value) {
        super(ruleID, active, type, value);
        this.courseID = courseID;
    }

    /* STATIC METHODS */

    /**
     * Creates a new business rule that applies to all future marks for a given course.
     * If a rule of the same type already exists for the course, it will be deactivated.
     * @param courseID The ID of the course the rule applies to
     * @param type The type of the rule
     * @param value The value of the rule
     * @return The newly created business rule
     * @throws SQLException If the query fails
     */
    public static BusinessRule createRule(String courseID, RuleType type, int value) throws SQLException {
        return createGroupRule(Set.of(courseID), type, value).iterator().next();
    }

    /**
     * Creates a new business rule that applies to all future marks for a given course group.
     * If any rules of the same type already exist for the courses, they will be deactivated.
     * <br><br>
     * When any of the courses in this group has a new rule of the same type created for it
     * this rule will be completely deactivated, affecting every course in the group.
     * @param courseIDs The IDs of the courses the rule applies to
     * @param type The type of the rule
     * @param value The value of the rule
     * @return The newly created business rules
     * @throws SQLException If the query fails
     */
    public static Set<BusinessRule> createGroupRule(Set<String> courseIDs, RuleType type, int value) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();

        // Create the rule in the database
        final int ruleID = getNewRuleID();
        BusinessRule.createNewRuleEntry(ruleID, type, value);

        // Create the rule-course relationships in the database
        for (String courseID : courseIDs) {
            Map<String, String> rc_values = new HashMap<>();
            rc_values.put("CourseID", db.sqlString(courseID));
            rc_values.put("RuleID", Integer.toString(ruleID));
            db.insert("BusinessRuleCourse", rc_values);
        }

        System.out.println("Created business rule #" + ruleID + " for course(s) [" + String.join(", ", courseIDs) + "] of type " + type.name() + " with value " + value);

        // Deactivate any previously existing rule of the same type for these courses
        int rulesDeactivated = db.executeUpdate(
            "UPDATE BusinessRule\n" +
            "SET BusinessRule.Active = FALSE\n" +
            "WHERE BusinessRule.RuleID != " + ruleID + " AND BusinessRule.Type = " + db.sqlString(type.name()) + " AND BusinessRule.Active = TRUE AND EXISTS (\n" +
                "SELECT RuleID FROM BusinessRuleCourse\n" +
                "WHERE BusinessRuleCourse.RuleID = BusinessRule.RuleID\n" +
                "AND BusinessRuleCourse.CourseID IN ("
                    + String.join(", ", courseIDs.stream()
                        .map(db::sqlString)
                        .collect(Collectors.toSet()))
                + ")\n" +
            ");"
        );

        if (rulesDeactivated > 0) System.out.println("Deactivated " + rulesDeactivated + " conflicting business rule(s)");

        return courseIDs.stream()
                .map(courseID -> new CourseBusinessRule(ruleID, true, courseID, type, value))
                .collect(Collectors.toSet());
    }

    /**
     * Gets all the business rules that are applied to a given course
     * @param courseID The ID of the course to get the rules for
     * @throws SQLException If the query fails
     */
    public static List<BusinessRule> getCourseRules(String courseID) throws SQLException { return getCourseRules(courseID, false); }

    /**
     * Gets all the business rules that are applied to a given course
     * @param courseID The ID of the course to get the rules for
     * @param ensureActive Whether to only return active rules
     * @throws SQLException If the query fails
     */
    public static List<BusinessRule> getCourseRules(String courseID, boolean ensureActive) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        String[] tables = {"BusinessRuleCourse t1", "BusinessRule t2"};
        String[] columns = {"t1.RuleID", "t2.Active", "t2.Type", "t2.Value"};
        String[] conditions = {"t1.CourseID = " + db.sqlString(courseID), "t1.RuleID = t2.RuleID"};
        CachedRowSet res = db.select(tables, columns, conditions);

        List<BusinessRule> rules = new ArrayList<>();
        while (res.next()) {
            boolean active = res.getBoolean("Active");
            if (ensureActive && !active) continue;
            int ruleID = res.getInt("RuleID");
            RuleType type = RuleType.valueOf(res.getString("Type"));
            int value = res.getInt("Value");
            rules.add(new CourseBusinessRule(ruleID, active, courseID, type, value));
        }
        return rules;
    }

    @Override
    public boolean passes(Student student) throws SQLException {
        switch(this.type) {

            case MAX_RESITS -> {

                Course course = student.getCourse();
                List<Module> modules = course.getModules();
                int courseTotalResits = 0;
                for (Module module : modules) {
                    int moduleAttempts = student.getNumberOfAttempts(module.getModuleID());
                    int moduleResits = (moduleAttempts > 0) ? moduleAttempts - 1 : 0;
                    courseTotalResits += moduleResits;
                }
                return courseTotalResits <= this.value;

            }

            case MAX_COMPENSATED_MODULES -> {

                Course course = student.getCourse();
                List<Module> modules = course.getModules();
                int compensatedModules = 0;
                for (Module module : modules) {
                    if (student.getMark(module.getModuleID()).canBeCompensated()) compensatedModules++;
                }
                return compensatedModules <= this.value;

            }

            default -> throw new IllegalStateException("Invalid course business rule type!: " + this.type.name());

        }
    }

    @Override
    public String toString() { return courseID + " - " + super.toString(); }

}
