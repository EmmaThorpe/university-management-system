package cs308.group7.usms.controller;

import cs308.group7.usms.model.User;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
                buttons =manUI.getCurrentButtons();
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



    /**Gets all the users
     * @return List of maps with user fields and their values (eg: forename, "john")
     */
    public List < HashMap <String, String> > getUsers() {
        List<User> accounts = new ArrayList<>();
        accounts.add(new User("abc1","mng1", "a","b", "a.com", new Date(2003,1,1), "man.", User.UserType.STUDENT,true));
        accounts.add(new User("abc2","mng1", "b","c", "b.com", new Date(2003,1,1), " Not man.", User.UserType.MANAGER,false));
        accounts.add(new User("abc3","mng1", "d","e", "c.com", new Date(2003,1,1), " Not man.", User.UserType.MANAGER,true));
        accounts.add(new User("abc4","mng1", "f","g", "d.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,true));
        accounts.add(new User("abc5","mng1", "h","i", "e.com", new Date(2003,1,1), "man.", User.UserType.STUDENT,false));
        accounts.add(new User("abc6","mng2", "x","x", "x.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,
                true));
        accounts.add(new User("abc7","mng1", "y","y", "y.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,
                true));
        accounts.add(new User("abc8","mng2", "z","z", "z.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,
                true));

        List<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
        for (User acc : accounts ) {
            //try {
                HashMap<String, String> userDetailsMap = new HashMap<String, String>();
                userDetailsMap.put("userID", acc.getUserID());
                userDetailsMap.put("managerID", "mng1");
                userDetailsMap.put("forename", acc.getForename());
                userDetailsMap.put("surname", acc.getSurname());
                userDetailsMap.put("email", acc.getEmail());
                userDetailsMap.put("DOB", acc.getDOB().toString());
                userDetailsMap.put("gender", acc.getGender());
                userDetailsMap.put("userType", acc.getType().toString());
                userDetailsMap.put("activated", acc.getActivated()? "ACTIVATED" : "DEACTIVATED");
                users.add(userDetailsMap);
            //} catch (SQLException e) {
            //    throw new RuntimeException(e);
            //}
        }

        return users;
    }

    /**Gets all the users that are still to be approved ( are inactive)
     * @return List of maps with user fields and their values (eg: forename, "john")
     */
    public List < HashMap <String, String> > getUnapprovedUsers(List<HashMap<String, String>> users) {
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
     *
     * @return List containing a map of course info
     */
    public List<Map<String, String>> getCourses(){
        List<Map<String, String>> courses = new ArrayList<>();
        HashMap<String, String> temp = new HashMap<>();
        temp.put("Id","G600");
        temp.put("Name","Software Engineering");
        temp.put("Description","Software Engineering will of high-quality software, focusing on large-scale software systems." );
        temp.put("Level", "Undergraduate");
        temp.put("Years", "4");

        courses.add(temp);
        return courses;
    }

    /**
     * Gets a list of all modules
     *
     * @return List of maps containg module info
     */
    public List<Map<String, String>> getModules(){
        List<Map<String, String>> modules = new ArrayList<>();
        HashMap temp = new HashMap<String, String>();
        temp.put("Id","CS308");
        temp.put("Name", "Building Software Systems");
        temp.put("Description" ,"Development in a group setting of significant systems from scratch.");
        temp.put("Credit", "20");
        temp.put("Lecturers", "Bob Atkey, Jules, Alasdair"); //comma seperated list of all lecturers

        modules.add(temp);
        return modules;
    }


    /**
     * Gets a list of all lecturers with no current class
     *
     * @return List of maps containing the name and the id of all lecturers
     */
    public List<Map<String, String>> getFreeLecturers(){
        List<Map<String, String>> lecturers = new ArrayList<>();
        Map<String, String> temp = new HashMap<>();
        temp.put("Id", "GOAT");
        temp.put("Name","Theresa May");
        lecturers.add(temp);
        return lecturers;
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
        //boolean success = activation code;

        boolean success = true; //dummy for testing
        if(success){
            pageSetter("MANAGE ACCOUNTS", false);
            manUI.makeNotificationModal("Account successfully activated", true);
        }else{
            manUI.makeNotificationModal("Error activating account", false);
        }

    }

    /**Takes in a userID and deactivates the user
     * @param userID
     */
    public void deactivateUser(String userID){
        System.out.println(userID);
    }

    /**Takes in a userID and password, and sets the selected user's password to be the set password
     * @param userID
     * @param password
     */
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
        System.out.println(lecturerID +" "+moduleID);
    }

    /** Assign a student to a course
     * @param studentID
     * @param courseID
     */
    public void assignStudentCourse(String studentID, String courseID){
        System.out.println(studentID +" "+ courseID);
    }

    /**Assign a module to a course
     * @param courseID
     * @param moduleID
     */
    public void assignModuleCourse(String courseID, String moduleID){
        System.out.println(courseID + " " +moduleID);
    }

    /**Issue a student with a decision
     * @param studentID
     * @param decision
     */
    public void issueStudentDecision(String studentID, String decision){
        System.out.println(studentID + " " +decision);
    }

    /**Add a new course
     * @param code
     * @param name
     * @param description
     */
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
    public void addModule(String code, String name, String description, String credit){
        System.out.println(code + " " +name +" "+description +" "+credit);
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


    public void AddBusinessRuleCourse(String type, int value, ArrayList<String> courses){

    }


    public void AddBusinessRuleModule(String type, int value, ArrayList<String> modules){

    }

}
