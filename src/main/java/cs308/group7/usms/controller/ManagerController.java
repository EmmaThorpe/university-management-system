package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.Node;
import javafx.scene.control.Button;

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
                buttons = manUI.getCurrentButtons();
                buttons.get("ACTIVATE").setOnAction((event)-> activateUser(manUI.getSelectedVal()));
                buttons.get("DEACTIVATE").setOnAction((event)-> deactivateUser(manUI.getSelectedVal()));
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event -> pageSetter("STUDENT DECISION", false));
                break;
            case "STUDENT DECISION":
               // manUI.studentDecision(getMarks(manUI.getSelectedVal()));
                buttons = manUI.getCurrentButtons();
                break;
            case "MANAGE COURSES":
                manUI.courses(getCourses(), getModules());
                buttons = manUI.getCurrentButtons();
                break;
            case "MANAGE MODULES":
                manUI.modules(getModules(), getFreeLecturers());
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

    /**
     * Gets a list of all courses
     *
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
     * Gets a list of all lecturers with no current class
     *
     * @return List of maps containing the name and the id of all lecturers
     */
    // TODO: ask about this. can lecturers have no class rn?
    public List<Map<String, String>> getFreeLecturers(){
        List<Map<String, String>> lecturers = new ArrayList<>();
        Map<String, String> temp = new HashMap<>();
        temp.put("Id", "GOAT");
        temp.put("Name","Theresa May");
        lecturers.add(temp);
        return lecturers;
    }





    /**Takes in a userID and activates the user
     * @param userID
     */
    // TODO: ask about this. userID is always null. why?
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
    // TODO: ask about this. can users be deactivated rn?
    public void deactivateUser(String userID){

    }

    /**Takes in a userID and password, and sets the selected user's password to be the set password
     * @param userID
     * @param password
     */
    // TODO: erm password stuff is unclear rn
    public void resetPassword(String userID, String password){

    }


    /**Changes the password for a user. And updates the view accordingly
     * @param userID - the id of the user whose password is being changed
     * @param newPass - the new password
     */
    public void changePassword(String userID, String newPass){
        // password changing
        pageSetter("DASHBOARD", false);

    }


    /** Assign a lecturer to a module
     * @param lecturerID
     * @param moduleID
     */
    // TODO: ask about this and others below. no usages so don't know how to test? how do u make the button do button things
    public void assignLecturerModule(String lecturerID, String moduleID){
        try {
            Lecturer l = new Lecturer(lecturerID);
            l.assignModule(moduleID);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
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
    }

    /**Assign a module to a course
     * @param courseID
     * @param moduleID
     */
    // TODO: ask about this. if there's no input how is it known which semester/year the module goes in?
    public void assignModuleCourse(String courseID, String moduleID){
        try {
            Course c = new Course(courseID);
            //c.addModule(moduleID, );
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
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
    }

    /**Add a new course
     * @param code
     * @param name
     * @param description
     */
    // TODO: ask about these two. the inputted information isn't enough to fully populate the database; where does the other info come from? are these just to be added in?
    public void addCourse(String code, String name, String description){

    }


    /**Add a module
     * @param code
     * @param name
     * @param credit
     */
    public void addModule(String code, String name, int credit){

    }

    // TODO: check diagram for all the other methods i probably forgot about

}
