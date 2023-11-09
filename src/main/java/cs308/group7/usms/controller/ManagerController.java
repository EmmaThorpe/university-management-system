package cs308.group7.usms.controller;

import cs308.group7.usms.model.Course;
import cs308.group7.usms.model.User;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.sql.Date;
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
                break;
            case "MANAGE ACCOUNTS":
                manUI.accounts(getUsers(), getCourses(), getModules());
                buttons =manUI.getCurrentButtons();
                buttons.get("ACTIVATE").setOnAction((event)-> {
                    pageSetter("MANAGE ACCOUNTS", false);
                    if(activateUser(manUI.getSelectedVal())){

                    }else{

                    }});
                buttons.get("DEACTIVATE").setOnAction((event)-> {
                    pageSetter("MANAGE ACCOUNTS", false);
                    if(activateUser(manUI.getSelectedVal())){

                    }else{

                    }});
                buttons.get("ISSUE STUDENT DECISION").setOnAction(event -> pageSetter("STUDENT DECISION", false));
                break;
            case "STUDENT DECISION":
               // manUI.studentDecision(getMarks(manUI.getSelectedVal()));
                buttons =manUI.getCurrentButtons();
                break;
            case "MANAGE COURSES":
                manUI.courses(getCourses(), getModules());
                buttons =manUI.getCurrentButtons();
        }
        buttons.get("LOG OUT").setOnAction(event -> manUI.hideStage());
        buttons.get("HOME").setOnAction(event -> pageSetter("DASHBOARD", false));

        if(initial){
            manUI.displayFirstScene();
        }else{
            manUI.displayScene();
        }

    }



    /**Changes the password for a user.
     * @param oldPass
     * @param newPass
     * @return Boolean value representing if the password change was successful or not.
     */
    public boolean changePassword(String oldPass, String newPass){
        return true;
    }

    /**Gets all the users
     * @return An ArrayList of userIDs
     */
    public List<User> getUsers(){
        List<User> accounts = new ArrayList<>();
        accounts.add(new User("abc1","def1", "a","b", "a.com", new Date(2003,1,1), "man.", User.UserType.STUDENT,true));
        accounts.add(new User("abc2","def2", "b","c", "b.com", new Date(2003,1,1), " Not man.", User.UserType.MANAGER,false));
        accounts.add(new User("abc3","def3", "d","e", "c.com", new Date(2003,1,1), " Not man.", User.UserType.MANAGER,true));
        accounts.add(new User("abc4","def4", "f","g", "d.com", new Date(2003,1,1), "man.", User.UserType.LECTURER,true));
        accounts.add(new User("abc5","def5", "h","i", "e.com", new Date(2003,1,1), "man.", User.UserType.STUDENT,false));

        return accounts;
    }

    /**Gets the userIDs of all students
     * @return An ArrayList of students' userIDs
     */
    public ArrayList<String> getStudents(){
        return null;
    }

    /**Gets the userIDs of all lecturers
     * @return An ArrayList of lecturers' userIDs
     */
    public ArrayList<String> getLecturers(){
        return null;
    }

    /**Takes in a userID and activates the user
     * @param userID
     */
    public boolean activateUser(String userID){
        return true;
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

    /**Gets a list of all courses
     * @return List containing the id of all modules
     */
    public List<Course> getCourses(){
        //test values for view
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("G600",
                "Software Engineering",
                "Software Engineering will provide you with the essential skills to become a professional developer " +
                        "of high-quality software, focusing on large-scale software systems.",
                "Undergraduate",
                4));
        courses.add(new Course("GHK6",
                "Computer & Electronic Systems",
                "Computer and Electronic Systems is one of the few UK degrees with triple accreditation from the Institution of Engineering and Technology, British Computer Society and the Science Council.",
                "Undergraduate",
                4));
        courses.add(new Course("G400",
                "Computer Science",
                "Computer Science demands and develops a challenging mix of skills and abilities. Our graduates not only understand new technologies but can influence their development.",
                "Undergraduate",
                4));
        return courses;
    }

    /**
     * Gets a list of all modules
     *
     * @return List containing the id of all modules
     */
    public List<String> getModules(){
        //test values for view
        List<String> modules = new ArrayList<>();
        modules.add("CS308: Building Software Systems");
        modules.add("CS312: Web Applications Development");
        modules.add("CS316: Functional Programming");
        return modules;
    }

    /**Gets info of the curriculum the student is in
     * @param moduleID
     * @return A map containing module information
     */
    public Map<String,String> getModuleInformation(String moduleID){
        return null;
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
