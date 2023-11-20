package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.*;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                buttons =manUI.getCurrentButtons();
                buttons.get("ACTIVATE").setOnAction((event)-> activateUser(manUI.getValues().get("UserID")));
                buttons.get("DEACTIVATE").setOnAction((event)-> deactivateUser(manUI.getValues().get("UserID")));
                buttons.get("RESET USER PASSWORD").setOnAction((event)-> resetPassword(manUI.getValues().get("UserID"), ((TextField)manUI.getCurrentFields().get("NEW PASSWORD")).getText()));
                buttons.get("ASSIGN LECTURER").setOnAction((event)-> assignLecturerModule(manUI.getValues().get("UserID"), ((ComboBox<?>)manUI.getCurrentFields().get("MODULE TO ASSIGN")).getValue().toString()));
                buttons.get("ENROL STUDENT").setOnAction((event)-> assignStudentCourse(manUI.getValues().get("UserID"), ((ComboBox)manUI.getCurrentFields().get("COURSE TO ENROL TO")).getValue().toString()));
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event -> pageSetter("STUDENT DECISION", false));
                break;

            case "STUDENT DECISION":
                manUI.studentDecision(getSelectedStudent(), getStudentMarks(), getDecisionRec().get(0),
                        getDecisionRec().get(1));
                buttons =manUI.getCurrentButtons();
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event-> issueStudentDecision(manUI.getValues().get("UserID"), ((ComboBox)manUI.getCurrentFields().get("DECISION TO ISSUE")).getValue().toString()));
                break;

            case "MANAGE COURSES":
                manUI.courses(getCourses(), getModules());
                buttons =manUI.getCurrentButtons();
                buttons.get("ADD").setOnAction(event-> addCourse(((TextField)manUI.getCurrentFields().get("CODE")).getText(), ((TextField)manUI.getCurrentFields().get("NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("LEVEL OF STUDY")).getText(), ((TextField)manUI.getCurrentFields().get("LENGTH OF COURSE")).getText()));
                buttons.get("EDIT").setOnAction((event)-> editCourse(manUI.getValues().get("ID"), ((TextField)manUI.getCurrentFields().get("EDIT CODE")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("EDIT DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT LEVEL OF STUDY")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT LENGTH OF COURSE")).getText()));
                buttons.get("ASSIGN").setOnAction((event)-> assignModuleCourse(manUI.getValues().get("ID"), ((ComboBox)manUI.getCurrentFields().get("MODULE TO ASSIGN TO")).getValue().toString()));
                break;

            case "MANAGE MODULES":
                manUI.modules(getModules(), getFreeLecturers());
                buttons = manUI.getCurrentButtons();
                buttons =manUI.getCurrentButtons();
                buttons.get("ADD").setOnAction(event-> addModule(((TextField)manUI.getCurrentFields().get("CODE")).getText(), ((TextField)manUI.getCurrentFields().get("NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("CREDITS")).getText()));
                buttons.get("EDIT").setOnAction((event)-> editModule(manUI.getValues().get("ID"), ((TextField)manUI.getCurrentFields().get("EDIT CODE")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT NAME")).getText(), ((TextArea)manUI.getCurrentFields().get("EDIT DESCRIPTION")).getText(), ((TextField)manUI.getCurrentFields().get("EDIT CREDITS")).getText()));
                buttons.get("ASSIGN").setOnAction((event)-> assignLecturerModule(manUI.getValues().get("ID"), ((ComboBox)manUI.getCurrentFields().get("LECTURER TO ASSIGN TO")).getValue().toString()));
                break;

            case "MANAGE SIGN-UP WORKFLOW":
                manUI.signups(getUnapprovedUsers(getUsers()));
                buttons =manUI.getCurrentButtons();
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

    /**Gets all the users that are still to be approved ( are inactive)
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

    //dummy values!
    public Map<String, String> getSelectedStudent() {
        Map<String, String> temp = new HashMap<>();
        temp.put("userID", "stu1");
        temp.put("managerID", "mng1");
        temp.put("forename", "john");
        temp.put("surname", "smith");
        temp.put("email", "johnsmith@mail.com");
        temp.put("DOB", "14-04-2000");
        temp.put("gender", "man");
        temp.put("userType", "STUDENT");
        temp.put("activated", "ACTIVATED");
        temp.put("courseID", "G600");
        temp.put("YearOfStudy", "1");
        return temp;
    }

    /**
     * Get marks for a selected student
     *
     * @return List of maps containing the mark fields for each mark
     */

    //dummy
    public List<Map<String, String>> getStudentMarks() {
        List<Map<String, String>> marks = new ArrayList<>();
        Map<String, String> temp = new HashMap<>();
        temp.put("moduleID", "CS308");
        temp.put("lab", "96");
        temp.put("exam", "80");
        temp.put("attempt", "2");
        temp.put("grade", "PASS");
        marks.add(temp);

        Map<String, String> temp2 = new HashMap<>();
        temp2.put("moduleID", "CS312");
        temp2.put("lab", "30");
        temp2.put("exam", "20");
        temp2.put("attempt", "1");
        temp2.put("grade", "FAIL");
        marks.add(temp2);

        Map<String, String> temp3 = new HashMap<>();
        temp3.put("moduleID", "CS316");
        temp3.put("lab", "50");
        temp3.put("exam", "49");
        temp3.put("attempt", "1");
        temp3.put("grade", "PASS");
        marks.add(temp3);
        return marks;
    }

    /**
     * Recommends a student's decision based on the business rules activated and their marks, with a reason
     *
     * @return An arraylist of strings where the first element (key) has the decision made and the second (value) has
     * the
     * reason for
     * the
     * decision (this return value can be altered but the reason and the award type must be accessible as
     * seperate strings to be passed into the issueStudentDecision method)
     */
    //dummy
    public ArrayList<String> getDecisionRec() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("RESIT");
        temp.add("Failed CS312");
        return temp;
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
    public void addCourse(String code, String name, String description, String levelOfStudy, int length, String deptNo){
        DatabaseConnection db = App.getDatabaseConnection();

        HashMap<String, String> values = new HashMap<>();
        values.put("CourseID", db.sqlString(code));
        values.put("Name", db.sqlString(name));
        values.put("Description", db.sqlString(description));
        values.put("LevelOfStudy", db.sqlString(levelOfStudy));
        values.put("AmountOfYears", db.sqlString(String.valueOf(length)));
        values.put("DeptNo", db.sqlString(deptNo));

        try{
            db.insert("Course", values);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    public void addCourse(String code, String name, String description, String level, String length){
        System.out.println(code + " " +name +" "+description +" "+level +" "+length);
    }


    /**Edits a course
     * @param code
     * @param name
     * @param description
     */
    public void editCourse(String oldCode, String code, String name, String description, String level, String length){
        System.out.println(oldCode+" "+code + " " +name +" "+description +" "+level +" "+length);
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
        public void editModule(String oldCode, String code, String name, String description, String credit){
            System.out.println(oldCode+" "+code + " " +name +" "+description +" "+credit);
        }

        /**
         * @return A list of a map containing all the details of each activated business rules
         */
        public List<Map<String, String>> getActivatedBusinessRules(){
            Map<String, String> temp = new HashMap<>();
            ArrayList<Map<String, String>> tempList = new ArrayList<>();

            temp.put("Id", "abc");
            temp.put("Type", "NUMBER OF RESITS");
            temp.put("Value", "2");
            tempList.add(temp);

            return tempList;
        }


        /**
         * @return Get a map containing an id of a business rule along with a list of ids of courses/modules it is associated with
         * Can combine with above function if that is easier
         */
        public Map<String, List<String>> getAssociatedOfRules(){
            Map<String, List<String>> temp = new HashMap<>();
            ArrayList<String> tempList = new ArrayList<>();

            tempList.add("CS103");
            tempList.add("CS104");

            temp.put("abc", tempList);

            return temp;
        }


        /**
         * @return Map of courses with a map of whether or not they have a rule set for the 2 different rule types
         */
        public Map<String, Map <String,Boolean>> getCourseRulesMap(){
            Map<String, Map<String,Boolean>> temp = new HashMap<>();
            Map<String,Boolean> tempRules = new HashMap<>();

            tempRules.put("Max Number Of Resits", false);
            tempRules.put("Number of Compensated Classes", false);

            temp.put("SE1", tempRules);


            tempRules.put("Max Number Of Resits", true);
            tempRules.put("Number of Compensated Classes", true);

            temp.put("SE2", tempRules);

            return temp;
        }


        /**
         * @return A map containing module id's and if they have a rule set to them or not
         */
        public Map<String, Boolean> getModuleRulesMap(){
            Map<String,Boolean> temp = new HashMap<>();

            temp.put("CS103", false);

            return temp;
        }


        public void addBusinessRuleCourse(String type, int value, List<String> courses){
            System.out.println(type +" "+ value + " " +courses.toString());
        }


        public void addBusinessRuleModule(int value, List<String> modules){
            System.out.println(value + " " +modules.toString());
        }

        // TODO: check diagram for all the other methods i probably forgot about

    }

}
