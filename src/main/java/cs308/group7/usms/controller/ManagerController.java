package cs308.group7.usms.controller;

import cs308.group7.usms.model.User;
import cs308.group7.usms.ui.MainUI;
import cs308.group7.usms.ui.ManagerUI;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
                manUI.accounts(getUsers(), null, null);
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

    /**Gets a list of all modules
     * @return ArrayList containing the id of all modules
     */
    public ArrayList<String> getModules(){
        return null;
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
