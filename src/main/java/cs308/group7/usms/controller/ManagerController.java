package cs308.group7.usms.controller;

import cs308.group7.usms.model.User;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerController{
    String userID;

    ManagerUI manUI;

    public ManagerController(String id){
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
                buttons.get("ACTIVATE").setOnAction((event)-> activateUser(manUI.getSelectedVal()));
                buttons.get("DEACTIVATE").setOnAction((event)-> deactivateUser(manUI.getSelectedVal()));
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event -> pageSetter("STUDENT DECISION", false));
                break;
            case "STUDENT DECISION":
               // manUI.studentDecision(getMarks(manUI.getSelectedVal()));
                buttons =manUI.getCurrentButtons();
                break;
            case "MANAGE COURSES":
                manUI.courses(getCourses(), getModules());
                buttons =manUI.getCurrentButtons();
                break;
            case "MANAGE MODULES":
                manUI.modules(getModules(), getFreeLecturers());
                buttons =manUI.getCurrentButtons();
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


    /**Changes the password for a user. And updates the view accordingly
     * @param oldPass
     * @param newPass
     */
    public void changePassword(String oldPass, String newPass){
        // password changing
        pageSetter("DASHBOARD", false);

    }

    /**Gets all the users
     * @return An ArrayList of users
     */
    public List<User> getUsers(){
        List<User> accounts = new ArrayList<>();
        accounts.add(new User("abc1","def1", "a","b", "a.com", new Date(2003,1,1), "man.", User.UserType.STUDENT,true));
        accounts.add(new User("abc2","def2", "b","c", "b.com", new Date(2003,1,1), " Not man.", User.UserType.MANAGER,false));
        accounts.add(new User("abc3","def3", "d","e", "c.com", new Date(2003,1,1), " Not man.", User.UserType.MANAGER,true));
        accounts.add(new User("abc4","def4", "f","g", "d.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,true));
        accounts.add(new User("abc5","def5", "h","i", "e.com", new Date(2003,1,1), "man.", User.UserType.STUDENT,false));
        accounts.add(new User("abc6","def6", "x","x", "x.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,
                true));
        accounts.add(new User("abc7","def7", "y","y", "y.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,
                true));
        accounts.add(new User("abc8","def8", "z","z", "z.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,
                true));
        return accounts;
    }


    /**Takes in a userID and activates the user
     * @param userID
     */
    public void activateUser(String userID){
        //boolean success = activation code;

        boolean success = true; //dummy for testing
        if(success){
            pageSetter("MANAGE ACCOUNTS", false);
            manUI.makeNotificationModal("Account succesfully activated", true);
        }else{
            pageSetter("MANAGE ACCOUNTS", false);
            manUI.makeNotificationModal("Error activating account", false);
        }

    }

    /**Takes in a userID and deactivates the user
     * @param userID
     */
    public void deactivateUser(String userID){

    }

    /**Takes in a userID and password, and sets the selected user's password to be the set password
     * @param userID
     * @param password
     */
    public void resetPassword(String userID, String password){

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


    /** Assign a lecturer to a module
     * @param lecturerID
     * @param moduleID
     */
    public void assignLecturerModule(String lecturerID, String moduleID){

    }

    /** Assign a student to a course
     * @param studentID
     * @param courseID
     */
    public void assignStudentCourse(String studentID, String courseID){

    }

    /**Assign a module to a course
     * @param courseID
     * @param moduleID
     */
    public void assignModuleCourse(String courseID, String moduleID){

    }

    /**Issue a student with a decision
     * @param studentID
     * @param decision
     */
    public void issueStudentDecision(String studentID, String decision){

    }

    /**Add a new course
     * @param code
     * @param name
     * @param description
     */
    public void addNewCourse(String code, String name, String description){

    }


    /**Add a module
     * @param code
     * @param name
     * @param credit
     */
    public void addModule(String code, String name, int credit){

    }

}
