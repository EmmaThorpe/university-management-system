package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.model.businessRules.BusinessRule;
import cs308.group7.usms.model.businessRules.CourseBusinessRule;
import cs308.group7.usms.model.businessRules.ModuleBusinessRule;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;

import static cs308.group7.usms.model.businessRules.BusinessRule.RuleType.MAX_RESITS;
import static cs308.group7.usms.model.businessRules.BusinessRule.RuleType.MAX_COMPENSATED_MODULES;

public class ManagerController{

    private final String userID;
    private final ManagerUI manUI;

    public ManagerController(String id) {
        userID = id;
        manUI = new ManagerUI();
        pageSetter("DASHBOARD",true);
    }

    public void pageSetter(String page, Boolean initial){
        Map<String, Button> buttons = null;
        switch (page){
            case "DASHBOARD":
                manUI.dashboard();
                buttons = manUI.getCurrentButtons();
                buttons.get("MANAGE MODULES").setOnAction((event)->pageSetter("MANAGE MODULES", false));
                buttons.get("MANAGE COURSES").setOnAction((event)->pageSetter("MANAGE COURSES", false));
                buttons.get("MANAGE SIGN-UP WORKFLOW").setOnAction((event)->pageSetter("MANAGE SIGN-UP WORKFLOW", false));
                buttons.get("MANAGE ACCOUNTS").setOnAction((event)->pageSetter("MANAGE ACCOUNTS", false));
                buttons.get("MANAGE BUSINESS RULES").setOnAction((event)->pageSetter("MANAGE BUSINESS RULES", false));

                Map<String, Node> currFields = manUI.getCurrentFields();
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(currFields.get("OLD PASSWORD").getAccessibleText(), currFields.get("NEW PASSWORD").getAccessibleText()));

                break;
            case "MANAGE ACCOUNTS":
                manUI.accounts(getUsers(), getCourses(), getModules());
                buttons = manUI.getCurrentButtons();
                buttons.get("ACTIVATE").setOnAction((event)-> activateUser(manUI.getValues().get("UserID")));
                buttons.get("DEACTIVATE").setOnAction((event)-> deactivateUser(manUI.getValues().get("UserID")));
                buttons.get("RESET USER PASSWORD").setOnAction((event)-> resetPassword(manUI.getValues().get("UserID"), ((TextField)manUI.getCurrentFields().get("NEW PASSWORD")).getText()));
                buttons.get("ASSIGN LECTURER").setOnAction((event)-> assignLecturerModule(manUI.getValues().get("UserID"), ((ComboBox<?>)manUI.getCurrentFields().get("MODULE TO ASSIGN")).getValue().toString()));
                buttons.get("ENROL STUDENT").setOnAction((event)-> assignStudentCourse(manUI.getValues().get("UserID"), ((ComboBox)manUI.getCurrentFields().get("COURSE TO ENROL TO")).getValue().toString()));
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event -> pageSetter("STUDENT DECISION", false));
                break;

            case "STUDENT DECISION":
                manUI.studentDecision(getSelectedStudent(manUI.getValues().get("UserID")), getStudentMarks(manUI.getValues().get("UserID")), getDecisionRec(manUI.getValues().get("UserID")).get(0),
                        getDecisionRec(manUI.getValues().get("UserID")).get(1));
                buttons = manUI.getCurrentButtons();
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event-> issueStudentDecision(manUI.getValues().get("UserID"), ((ComboBox)manUI.getCurrentFields().get("DECISION TO ISSUE")).getValue().toString()));
                break;

            case "MANAGE COURSES":
                manUI.courses(getCourses(), getModules(), getDepartments());
                buttons = manUI.getCurrentButtons();
                buttons.get("ADD").setOnAction(event-> addCourse(((TextField)manUI.getCurrentFields().get("SET CODE")).getText(), ((TextField)manUI.getCurrentFields().get("SET NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("SET DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("SET LEVEL OF STUDY")).getText(), Integer.parseInt(((TextField)manUI.getCurrentFields().get("SET LENGTH OF COURSE")).getText()), ((ComboBox)manUI.getCurrentFields().get("SET DEPARTMENT")).getValue().toString() ));
                buttons.get("EDIT").setOnAction((event)-> editCourse(manUI.getValues().get("ID"), ((TextField)manUI.getCurrentFields().get("EDIT CODE")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("EDIT DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT LEVEL OF STUDY")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT LENGTH OF COURSE")).getText(), ((ComboBox)manUI.getCurrentFields().get("EDIT DEPARTMENT")).getValue().toString()));
                // TODO UI CREW: add sems + year to assignModuleCourse
                // TODO: I have done it in manager UI and updated the event call method as needed
                //buttons.get("ASSIGN").setOnAction((event)-> assignCourseModule(manUI.getValues().get("ID"),
                //        ((ComboBox)manUI.getCurrentFields().get("MODULE TO ASSIGN TO")).getValue().toString()),
                //        ((ComboBox)manUI.getCurrentFields().get("SET SEMESTER")).getValue().toString()),
                //        ((ComboBox)manUI.getCurrentFields().get("SET YEAR")).getValue().toString()));
                break;

            case "MANAGE MODULES":
                manUI.modules(getModules(), getFreeLecturers());
                buttons = manUI.getCurrentButtons();
                buttons.get("ADD").setOnAction(event-> addModule(((TextField)manUI.getCurrentFields().get("CODE")).getText(), ((TextField)manUI.getCurrentFields().get("NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("DESCRIPTION")).getText(), Integer.parseInt(((TextField)manUI.getCurrentFields().get("CREDITS")).getText())));
                buttons.get("EDIT").setOnAction((event)-> editModule(manUI.getValues().get("ID"), ((TextField)manUI.getCurrentFields().get("EDIT CODE")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("EDIT DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT CREDITS")).getText()));
                buttons.get("ASSIGN").setOnAction((event)-> assignLecturerModule(manUI.getValues().get("ID"), ((ComboBox)manUI.getCurrentFields().get("LECTURER TO ASSIGN TO")).getValue().toString()));
                break;

            case "MANAGE SIGN-UP WORKFLOW":
                manUI.signups(getUnapprovedUsers(getUsers()));
                buttons = manUI.getCurrentButtons();
                buttons.get("APPROVE SIGN UP").setOnAction((event)-> activateUser(manUI.getValues().get("ID")));

                break;

            case "MANAGE BUSINESS RULES":
                manUI.manageBusinessRules(getActivatedBusinessRules(), getAssociatedOfRules());
                buttons = manUI.getCurrentButtons();
                buttons.get("ADD BUSINESS RULE").setOnAction(event-> pageSetter("ADD BUSINESS RULE", false));
                break;

            case "ADD BUSINESS RULE":
                manUI.addBusinessRule(getCourseRulesMap(), getModuleRulesMap());
                buttons = manUI.getCurrentButtons();
                // TODO UI CREW: the input ui is swapped (course is not given a type but module is) - module can't have max compensated
                buttons.get("SET COURSE RULE").setOnAction(event -> addBusinessRuleCourse(((ComboBox)manUI.getCurrentFields().get("RULE TYPE")).getValue().toString(), Integer.parseInt(((TextField)manUI.getCurrentFields().get("COURSE VALUE")).getText()), manUI.getRulesAppliedTo("COURSE")));
                buttons.get("SET MODULE RULE").setOnAction(event -> addBusinessRuleModule(Integer.parseInt(((TextField)manUI.getCurrentFields().get("MODULE VALUE")).getText()), manUI.getRulesAppliedTo("MODULE")));
                break;

        }
        buttons.get("LOG OUT").setOnAction(event -> manUI.hideStage());
        buttons.get("HOME").setOnAction(event -> pageSetter("DASHBOARD", false));

        if(initial){
            manUI.displayFirstScene();
        }else{
            manUI.displayScene();
        }
    }




    //Connections with models



    /** Gets all the users
     * @return List of maps with user fields and their values (eg: forename, "john")
     */
    public List <HashMap <String, String>> getUsers() {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Users"}, new String[]{"UserID"}, null);
            List<HashMap<String, String>> users = new ArrayList<>();

            while(result.next()){
                User acc = new User(result.getString("UserID"));
                HashMap<String, String> userDetailsMap = new HashMap<String, String>();
                userDetailsMap.put("userID", acc.getUserID());
                userDetailsMap.put("managerID", acc.getManager().getUserID());
                userDetailsMap.put("forename", acc.getForename());
                userDetailsMap.put("surname", acc.getSurname());
                userDetailsMap.put("email", acc.getEmail());
                userDetailsMap.put("DOB", acc.getDOB().toString());
                userDetailsMap.put("gender", acc.getGender());
                userDetailsMap.put("userType", acc.getType().toString());
                userDetailsMap.put("activated", acc.getActivated() ? "ACTIVATED" : "DEACTIVATED");
                users.add(userDetailsMap);
            }

            return users;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**Gets all the users that are still to be approved (are inactive)
     * @return List of maps with user fields and their values (eg: forename, "john")
     */
    public List <HashMap <String, String> > getUnapprovedUsers(List<HashMap<String, String>> users) {
        List<HashMap<String, String>> unapprovedUsers = new ArrayList<HashMap<String, String>>();
        for (HashMap<String, String> user : users ) {
            if (user.get("activated").equals("DEACTIVATED")) {
                unapprovedUsers.add(user);
            }
        }
        return unapprovedUsers;
    }


    /**
     * Gets a list of all courses
     * @return List containing a map of course info
     */
    public List<Map<String, String>> getCourses(){
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Course"}, new String[]{"CourseID"}, null);
            List<Map<String, String>> modules = new ArrayList<>();

            while(result.next()){
                Course cour = new Course(result.getString("CourseID"));
                Map<String, String> courseDetailsMap = new HashMap<String, String>();
                courseDetailsMap.put("Id", cour.getCourseID());
                courseDetailsMap.put("Name", cour.getName());
                courseDetailsMap.put("Description", cour.getDescription());
                courseDetailsMap.put("Level", cour.getLevel());
                courseDetailsMap.put("Years", String.valueOf(cour.getLength()));
                courseDetailsMap.put("Department", "ABC1");   //todo actually make this work
                modules.add(courseDetailsMap);
            }

            return modules;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of all modules
     *
     * @return List of maps containg module info
     */
    public List<Map<String, String>> getModules(){
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Module"}, new String[]{"ModuleID"}, null);
            List<Map<String, String>> modules = new ArrayList<>();

            while(result.next()){
                Module mod = new Module(result.getString("ModuleID"));
                Map<String, String> moduleDetailsMap = new HashMap<String, String>();
                moduleDetailsMap.put("Id", mod.getModuleID());
                moduleDetailsMap.put("Name", mod.getName());
                moduleDetailsMap.put("Description", mod.getDescription());
                moduleDetailsMap.put("Credit", String.valueOf(mod.getCredit()));

                List<Lecturer> lecs = mod.getLecturers();
                List<String> lecsList = new ArrayList<>();
                for(Lecturer l : lecs){
                    lecsList.add(l.getForename() + " " + l.getSurname());
                }
                String lecsString = String.join(", ", lecsList);

                moduleDetailsMap.put("Lecturers", lecsString);
                modules.add(moduleDetailsMap);
            }

            return modules;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets a list of all lecturers
     *
     * @return List of maps containing the name and the id of all lecturers
     */
    public List<Map<String, String>> getFreeLecturers(){
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Lecturer"}, new String[]{"UserID"}, null);
            List<Map<String, String>> lecturers = new ArrayList<>();

            while(result.next()){
                Lecturer lec = new Lecturer(result.getString("UserID"));
                Map<String, String> lecturerDetailsMap = new HashMap<String, String>();
                lecturerDetailsMap.put("Id", lec.getLecturerID());
                lecturerDetailsMap.put("Qualification", lec.getQualification());

                lecturers.add(lecturerDetailsMap);
            }

            return lecturers;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the student selected from the accounts action
     *
     * @return Map of that student's information
     */

    public Map<String, String> getSelectedStudent(String userID) {
        try {
            User acc = new User(userID);
            HashMap<String, String> user = new HashMap<>();
            user.put("userID", acc.getUserID());
            user.put("managerID", acc.getManager().getUserID());
            user.put("forename", acc.getForename());
            user.put("surname", acc.getSurname());
            user.put("email", acc.getEmail());
            user.put("DOB", acc.getDOB().toString());
            user.put("gender", acc.getGender());
            user.put("userType", acc.getType().toString());
            user.put("activated", acc.getActivated() ? "ACTIVATED" : "DEACTIVATED");
            return user;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets a list of maps of department's name
     *
     * @return A lists containing the names of all departments
     */
    public List<String> getDepartments(){
        List<String> temp = new ArrayList<>();
        temp.add("ABC2");
        temp.add("ABC3");
        temp.add("ABC1");
        return temp;
    }

    public String[] checkPassFail(Mark m){
        if(m.getLabMark()!=null && m.getExamMark()!=null) {
            if (m.getLabMark() >= 50 && m.getExamMark() >= 50) {
                return new String[]{"grade", "PASS"};
            }
            else{
                return new String[]{"grade", "FAIL"};
            }
        }
        else{
            return new String[]{"grade", "N/A"};
        }
    }

    /**
     * Get marks for a selected student
     *
     * @return List of maps containing the mark fields for each mark
     */
    public List<Map<String, String>> getStudentMarks(String userID) {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Mark"}, new String[]{"ModuleID", "AttNo"}, new String[]{"UserID = " + db.sqlString(userID)});
            List<Map<String, String>> marks = new ArrayList<>();

            while(result.next()){
                Mark m = new Mark(userID, result.getString("ModuleID"), result.getInt("AttNo"));
                Map<String, String> markDetailsMap = new HashMap<>();
                markDetailsMap.put("moduleID", m.getModuleID());
                Double labMark = m.getLabMark();
                Double examMark = m.getExamMark();
                markDetailsMap.put("lab", String.valueOf(labMark));
                markDetailsMap.put("exam", String.valueOf(examMark));
                markDetailsMap.put("attempt", String.valueOf(m.getAttemptNo()));

                String[] passFail = checkPassFail(m);
                markDetailsMap.put(passFail[0], passFail[1]);

                marks.add(markDetailsMap);
            }
            return marks;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }


    /**
     * Recommends a student's decision based on the business rules activated and their marks, with a reason
     *
     * @return An arraylist of strings
     * where the first element (key) has the decision made and the second (value) has the reason for the decision
     * (this return value can be altered but the reason and the award type must be accessible as seperate strings to be passed into the issueStudentDecision method)
     */
    //this thing chugs really badly i'm so sorry
    public ArrayList<String> getDecisionRec(String userID) {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            Student s = new Student(userID);
            CachedRowSet result = db.select(new String[]{"Mark"}, new String[]{"ModuleID", "AttNo"}, new String[]{"UserID = " + db.sqlString(userID)});
            boolean suggestResit = false;
            boolean suggestWithdraw = false;
            boolean maxCompen = false;
            List<String> maxResits = new ArrayList<>();
            String reason = "";

            while(result.next()){
                Mark m = new Mark(userID, result.getString("ModuleID"), result.getInt("AttNo"));

                // check against rules
                List<BusinessRule> rules = BusinessRule.getRules(s.getCourseID(), m);
                for(BusinessRule r : rules){
                    // if student doesn't pass the rule
                    if(!r.passes(s)){
                        switch(r.getType()){
                            case MAX_COMPENSATED_MODULES:
                                // if at the max compensated modules, set flag for reason and suggest resit
                                maxCompen = true;
                                suggestResit = true;
                                break;

                            case MAX_RESITS:
                                // add module to list of maximum resit reached and suggest withdraw
                                if(!maxResits.contains(m.getModuleID() + "\n")) {
                                    maxResits.add(m.getModuleID() + "\n");
                                }
                                suggestWithdraw = true;
                                break;
                        }
                    }
                }

                // check for regular failure
                if(m.getLabMark()!=null & m.getExamMark()!=null) {
                    if (m.getLabMark() < 40 || m.getExamMark() < 40) {
                        reason = reason + "Failed " + m.getModuleID() + ".\n";
                        suggestResit = true;
                    }
                }
            }

            // add reason for max compensations
            if(maxCompen){
                reason = reason + "The maximum number of passes by compensation has been passed.\n";
            }

            if(!maxResits.isEmpty()){
                reason = reason + "The maximum number of resits have been reached for the following modules:\n";
                for(String modID : maxResits){
                    reason = reason + modID;
                }
            }

            ArrayList<String> suggestion = new ArrayList<>();
            if (suggestWithdraw){
                suggestion.add("WITHDRAW");
                suggestion.add(reason);
            }
            else if (suggestResit){
                suggestion.add("RESIT");
                suggestion.add(reason);
            }
            else{
                suggestion.add("AWARD");
                suggestion.add("No rules have been failed.");
            }

            return suggestion;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    /**Takes in a userID and activates the user
     * @param userID
     */
    public void activateUser(String userID) {
        System.out.println(userID);
        try {
            User u = new User(userID);
            boolean success = u.setActivated();

            if (success) {
                pageSetter("MANAGE ACCOUNTS", false);
                manUI.makeNotificationModal("Account successfully activated", true);
            } else {
                manUI.makeNotificationModal("Error activating account", false);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**Takes in a userID and deactivates the user
     * @param userID
     */
    public void deactivateUser(String userID){
        System.out.println(userID);
        try {
            User u = new User(userID);
            boolean success = u.setDeactivated();

            if (success) {
                pageSetter("MANAGE ACCOUNTS", false);
                manUI.makeNotificationModal("Account successfully deactivated", true);
            } else {
                manUI.makeNotificationModal("Error deactivating account", false);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        System.out.println(userID);
    }

    /**Takes in a userID and password, and sets the selected user's password to be the set password
     * @param userID
     * @param password
     */
    // TODO: erm password stuff
    public void resetPassword(String userID, String password){
        System.out.println(userID + " " +password);
    }


    /**Changes the password for a user. And updates the view accordingly
     * @param userID - the id of the user whose password is being changed
     * @param newPass - the new password
     */
    public void changePassword(String userID, String newPass){
        // password changing
        System.out.println(userID + " " +newPass);
        pageSetter("DASHBOARD", false);

    }


    /** Assign a lecturer to a module
     * @param lecturerID
     * @param moduleID
     */
    public void assignLecturerModule(String lecturerID, String moduleID){
        try {
            Lecturer l = new Lecturer(lecturerID);
            l.assignModule(moduleID);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        System.out.println(lecturerID +" "+moduleID);
    }

    /** Assign a student to a course
     * @param studentID
     * @param courseID
     */
    public void assignStudentCourse(String studentID, String courseID){
        try {
            Student s = new Student(studentID);
            s.setCourse(courseID);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        System.out.println(studentID +" "+ courseID);
    }

    /**Assign a module to a course
     * @param courseID
     * @param moduleID
     * @param sem1
     * @param sem2
     * @param year
     */
    public void assignModuleCourse(String courseID, String moduleID, Boolean sem1, Boolean sem2, int year){
        try {
            Course c = new Course(courseID);
            c.addModule(moduleID, sem1, sem2, year);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        System.out.println(courseID + " " +moduleID);
    }

    /**Issue a student with a decision
     * @param studentID
     * @param decision
     */
    public void issueStudentDecision(String studentID, String decision){
        try {
            Student s = new Student(studentID);
            switch (decision) {
                case "Award" -> s.issueAward();
                case "Resit" -> s.issueResit();
                case "Withdrawal" -> s.issueWithdrawal();
                default -> throw new IllegalArgumentException("Invalid decision string!");
            };
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        System.out.println(studentID + " " +decision);
    }

    /**Add a new course
     * @param code
     * @param name
     * @param description
     * @param levelOfStudy
     * @param length
     */
    public void addCourse(String code, String name, String description, String levelOfStudy, int length, String deptNo) {
        DatabaseConnection db = App.getDatabaseConnection();

        HashMap<String, String> values = new HashMap<>();
        values.put("CourseID", db.sqlString(code));
        values.put("Name", db.sqlString(name));
        values.put("Description", db.sqlString(description));
        values.put("LevelOfStudy", db.sqlString(levelOfStudy));
        values.put("AmountOfYears", db.sqlString(String.valueOf(length)));
        values.put("DeptNo", db.sqlString(deptNo));

        try {
            db.insert("Course", values);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**Edits a course
     * @param code
     * @param name
     * @param description
     */
    public void editCourse(String oldCode, String code, String name, String description, String level, String length, String department){
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("CourseID", db.sqlString(code));
        values.put("Name", db.sqlString(name));
        values.put("Description", db.sqlString(description));
        values.put("LevelOfStudy", db.sqlString(level));
        values.put("AmountOfYears", String.valueOf(length));

        try {
            db.update("Course", values, new String[]{"CourseID = " + db.sqlString(oldCode)});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**Add a module
     * @param code
     * @param name
     * @param credit
     */
    public void addModule(String code, String name, String description, int credit){
        DatabaseConnection db = App.getDatabaseConnection();

        HashMap<String, String> values = new HashMap<>();
        values.put("ModuleID", db.sqlString(code));
        values.put("Name", db.sqlString(name));
        values.put("Description", db.sqlString(description));
        values.put("Credit", String.valueOf(credit));

        try{
            db.insert("Module", values);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

        /**Edits a module
         * @param code
         * @param name
         * @param credit
         */
        // TODO: couldn't test this in the ui because the button wasn't buttoning (but no errors showed)
        public void editModule(String oldCode, String code, String name, String description, String credit){
            DatabaseConnection db = App.getDatabaseConnection();
            HashMap<String, String> values = new HashMap<>();
            values.put("ModuleID", db.sqlString(code));
            values.put("Name", db.sqlString(name));
            values.put("Description", db.sqlString(description));
            values.put("Credit", String.valueOf(credit));

            try {
                db.update("Module", values, new String[]{"ModuleID = " + db.sqlString(oldCode)});
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * @return A list of a map containing all the details of each activated business rules
         */
        // TODO model crew: i should ask if there's a better way to do these two using the business rule methods
        public List<Map<String, String>> getActivatedBusinessRules(){
            DatabaseConnection db = App.getDatabaseConnection();
            try {
                CachedRowSet result = db.select(new String[]{"BusinessRule"}, null, null);
                List<Map<String, String>> rules = new ArrayList<>();

                while(result.next()){
                    Map<String, String> ruleDetailsMap = new HashMap<String, String>();

                    boolean active = result.getBoolean("Active");
                    if(active){
                        ruleDetailsMap.put("Id", result.getString("RuleID"));
                        ruleDetailsMap.put("Type", result.getString("Type"));
                        ruleDetailsMap.put("Value", result.getString("Value"));
                        rules.add(ruleDetailsMap);
                    }
                }

                return rules;
            }
            catch(SQLException e){
                throw new RuntimeException(e);
            }
        }


        /**
         * @return Get a map containing an id of a business rule along with a list of ids of courses/modules it is associated with
         * Can combine with above function if that is easier
         */
        public Map<String, List<String>> getAssociatedOfRules(){
            Map<String, List<String>> rulesAssoc = new HashMap<>();
            List<Map<String, String>> activeRules = getActivatedBusinessRules();

            DatabaseConnection db = App.getDatabaseConnection();
            for(Map<String, String> rule : activeRules) {
                try {
                    String ruleID = rule.get("Id");
                    ArrayList<String> idList = new ArrayList<>();

                    // check for modules
                    CachedRowSet resultMod = db.select(new String[]{"BusinessRule, BusinessRuleModule"},
                            new String[]{"BusinessRuleModule.ModuleID"},
                            new String[]{"BusinessRule.RuleID = BusinessRuleModule.RuleID",
                                    "BusinessRuleModule.RuleID = " + ruleID});

                    while (resultMod.next()) {
                        idList.add(resultMod.getString("ModuleID"));
                    }

                    // check for courses
                    CachedRowSet resultCour = db.select(new String[]{"BusinessRule, BusinessRuleCourse"},
                            new String[]{"BusinessRuleCourse.CourseID"},
                            new String[]{"BusinessRule.RuleID = BusinessRuleCourse.RuleID",
                                    "BusinessRuleCourse.RuleID = " + ruleID});

                    while (resultCour.next()) {
                        idList.add(resultCour.getString("CourseID"));
                    }

                    rulesAssoc.put(ruleID, idList);
                }
                catch(SQLException e){
                    throw new RuntimeException(e);
                }
            }
            return rulesAssoc;
        }


        /**
         * @return Map of courses with a map of whether or not they have a rule set for the 2 different rule types
         */
        //TODO: i should check if this should look for active rules or all rules
        // - It should look for active rules - matthew
        public Map<String, Map <String,Boolean>> getCourseRulesMap(){
            Map<String, Map<String,Boolean>> courseRules = new HashMap<>();
            boolean resitFlag = false;
            boolean compFlag = false;

            DatabaseConnection db = App.getDatabaseConnection();
            try{
                CachedRowSet courses = db.select(new String[]{"Course"}, new String[]{"CourseID"}, null);
                while(courses.next()){
                    List<BusinessRule> ruleList = CourseBusinessRule.getCourseRules(courses.getString("CourseID"), true);
                    Map<String,Boolean> ruleMap = new HashMap<>();

                    for(BusinessRule r : ruleList){
                        if(r.getType() == MAX_RESITS){
                            resitFlag = true;
                        }
                        else if (r.getType() == MAX_COMPENSATED_MODULES){
                            compFlag = true;
                        }
                    }

                    ruleMap.put("Max Number Of Resits", resitFlag);
                    ruleMap.put("Number of Compensated Classes", compFlag);
                }

                return courseRules;
            }
            catch(SQLException e){
                throw new RuntimeException(e);
            }
        }


        /**
         * @return A map containing module id's and if they have a rule set to them or not
         */
        public Map<String, Boolean> getModuleRulesMap(){
            DatabaseConnection db = App.getDatabaseConnection();
            Map<String,Boolean> ruleMap = new HashMap<>();
            try{
                CachedRowSet modules = db.select(new String[]{"Module"}, new String[]{"ModuleID"}, null);
                while(modules.next()){
                    String moduleID = modules.getString("ModuleID");
                    List<BusinessRule> ruleList = ModuleBusinessRule.getModuleRules(moduleID, true);

                    ruleMap.put(moduleID, !ruleList.isEmpty());
                }

                return ruleMap;
            }
            catch(SQLException e){
                throw new RuntimeException(e);
            }
        }


        public void addBusinessRuleCourse(String typeStr, int value, List<String> courses){
            Set<String> courseSet = new HashSet<>(courses);
            BusinessRule.RuleType type = switch (typeStr) {
                case "Max Number Of Resits" -> MAX_RESITS;
                case "Number of Compensated Classes" -> MAX_COMPENSATED_MODULES;
                default -> throw new RuntimeException("Rule type not understood.");
            };

            try {
                CourseBusinessRule.createGroupRule(courseSet, type, value);
            }
            catch(SQLException e){
                throw new RuntimeException(e);
            }
        }


        public void addBusinessRuleModule(int value, List<String> modules){
            Set<String> moduleSet = new HashSet<>(modules);
            try {
                ModuleBusinessRule.createGroupRule(moduleSet, MAX_RESITS, value);
            }
            catch(SQLException e){
                throw new RuntimeException(e);
            }
        }

}
