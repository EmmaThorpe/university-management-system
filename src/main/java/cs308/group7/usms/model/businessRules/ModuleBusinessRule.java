package cs308.group7.usms.model.businessRules;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.Student;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleBusinessRule extends BusinessRule {

    private final String moduleID;

    protected ModuleBusinessRule(int ruleID, boolean active, String moduleID, RuleType type, int value) {
        super(ruleID, active, type, value);
        this.moduleID = moduleID;
    }

    /* STATIC METHODS */

    /**
     * Creates a new business rule that applies to all future marks for a given module.
     * If a rule of the same type already exists for the module, it will be deactivated.
     * @param moduleID The ID of the module the rule applies to
     * @param type The type of the rule
     * @param value The value of the rule
     * @return The newly created business rule
     * @throws SQLException If the query fails
     */
    public static BusinessRule createRule(String moduleID, RuleType type, int value) throws SQLException {
        return createGroupRule(Set.of(moduleID), type, value).iterator().next();
    }

    /**
     * Creates a new business rule that applies to all future marks for a given module group.
     * If any rules of the same type already exist for the modules, they will be deactivated.
     * <br><br>
     * When any of the modules in this group has a new rule of the same type created for it
     * this rule will be completely deactivated, affecting every module in the group.
     * @param moduleIDs The IDs of the modules the rule applies to
     * @param type The type of the rule
     * @param value The value of the rule
     * @return The newly created business rules
     * @throws SQLException If the query fails
     */
    public static Set<BusinessRule> createGroupRule(Set<String> moduleIDs, RuleType type, int value) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();

        // Create the rule in the database
        final int ruleID = getNewRuleID();
        BusinessRule.createNewRuleEntry(ruleID, type, value);

        // Create the rule-module relationships in the database
        for (String moduleID : moduleIDs) {
            Map<String, String> rm_values = new HashMap<>();
            rm_values.put("ModuleID", db.sqlString(moduleID));
            rm_values.put("RuleID", Integer.toString(ruleID));
            db.insert("BusinessRuleModule", rm_values);
        }

        System.out.println("Created business rule #" + ruleID + " for module(s) [" + String.join(", ", moduleIDs) + "] of type " + type.name() + " with value " + value);

        // Deactivate any previously existing rule of the same type for these modules
        int rulesDeactivated = db.executeUpdate(
            "UPDATE BusinessRule\n" +
            "SET BusinessRule.Active = FALSE\n" +
            "WHERE BusinessRule.RuleID != " + ruleID + " AND BusinessRule.Type = " + db.sqlString(type.name()) + " AND BusinessRule.Active = TRUE AND EXISTS (\n" +
                "SELECT RuleID FROM BusinessRuleModule\n" +
                "WHERE BusinessRuleModule.RuleID = BusinessRule.RuleID\n" +
                "AND BusinessRuleModule.ModuleID IN ("
                    + String.join(", ", moduleIDs.stream()
                        .map(db::sqlString)
                        .collect(Collectors.toSet()))
                + ")\n" +
            ");"
        );

        if (rulesDeactivated > 0) System.out.println("Deactivated " + rulesDeactivated + " conflicting business rule(s)");

        return moduleIDs.stream()
                .map(moduleID -> new ModuleBusinessRule(ruleID, true, moduleID, type, value))
                .collect(Collectors.toSet());
    }

    /**
     * Gets all the business rules that are applied to a given module
     * @param moduleID The ID of the module to get the rules for
     * @throws SQLException If the query fails
     */
    public static List<BusinessRule> getModuleRules(String moduleID) throws SQLException { return getModuleRules(moduleID, false); }

    /**
     * Gets all the business rules that are applied to a given module
     * @param moduleID The ID of the module to get the rules for
     * @param ensureActive Whether to only return active rules
     * @throws SQLException If the query fails
     */
    public static List<BusinessRule> getModuleRules(String moduleID, boolean ensureActive) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        String[] tables = {"BusinessRuleModule t1", "BusinessRule t2"};
        String[] columns = {"t1.RuleID", "t2.Active", "t2.Type", "t2.Value"};
        String[] conditions = {"t1.ModuleID = '" + moduleID + "'", "t1.RuleID = t2.RuleID"};
        CachedRowSet res = db.select(tables, columns, conditions);

        List<BusinessRule> rules = new ArrayList<>();
        while (res.next()) {
            boolean active = res.getBoolean("Active");
            if (ensureActive && !active) continue;
            int ruleID = res.getInt("RuleID");
            RuleType type = RuleType.valueOf(res.getString("Type"));
            int value = res.getInt("Value");
            rules.add(new ModuleBusinessRule(ruleID, active, moduleID, type, value));
        }
        return rules;
    }

    /* INSTANCE METHODS */

    /**
     * Checks if the given student passes this business rule
     * @throws SQLException If the query fails
     */
    @Override
    public boolean passes(Student student) throws SQLException {
        switch(this.type) {

            case MAX_RESITS -> {
                int moduleAttempts = student.getNumberOfAttempts(this.moduleID);
                int moduleResits = (moduleAttempts > 0) ? moduleAttempts - 1 : 0;
                return moduleResits <= this.value;
            }

            default -> throw new IllegalStateException("Invalid module business rule type!: " + this.type.name());

        }
    }

    @Override
    public String toString() { return moduleID + " - " + super.toString(); }

}
