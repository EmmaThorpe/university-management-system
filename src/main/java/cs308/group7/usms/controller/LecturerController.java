package cs308.group7.usms.controller;

import cs308.group7.usms.model.Lecturer;
import cs308.group7.usms.ui.LecturerUI;
import cs308.group7.usms.ui.MainUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LecturerController{

    private final String lecturerID;
    private final LecturerUI lecUI;

    public LecturerController(String id) {
        this.lecturerID = id;
        lecUI = new LecturerUI();
        pageSetter("DASHBOARD", true);
    }


    public void pageSetter(String page, Boolean initial){
        Map<String, Button> buttons = null;
        switch (page){
            case "DASHBOARD":
                lecUI.dashboard();
                buttons =lecUI.getCurrentButtons();
                buttons.get("VIEW MODULE").setOnAction((event)->pageSetter("VIEW MODULE", false));
                Map<String, Node> currFields = lecUI.getCurrentFields();
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(currFields.get("OLD PASSWORD").getAccessibleText(), currFields.get("NEW PASSWORD").getAccessibleText()));

                break;
            case "VIEW MODULE":
                lecUI.module(getModuleInformation());
                buttons =lecUI.getCurrentButtons();
                break;
            case "UPDATE MODULE INFORMATION":



        }
        buttons.get("LOG OUT").setOnAction(event -> lecUI.hideStage());
        buttons.get("HOME").setOnAction(event -> pageSetter("DASHBOARD", false));

        if(initial){
            lecUI.displayFirstScene();
        }else{
            lecUI.displayScene();
        }
    }



    private Lecturer getCurrentLecturer() throws SQLException { return new Lecturer(lecturerID); }

    /**Changes the password for a user.
     * @param oldPass
     * @param newPass
     */
    public boolean changePassword(String oldPass, String newPass){
        return true;
    }


    /**Gets info of the module that the lecturer runs
     * @return A map containing module information
     */
    public Map<String,String> getModuleInformation(){
        Map<String,String> temp = new HashMap<String, String>();
        temp.put("Id","CS308");
        temp.put("Name", "Building Software Systems");
        temp.put("Description" ,"Development in a group setting of significant systems from scratch.");
        temp.put("Credit", "20");
        temp.put("Lecturers", "Bob Atkey, Jules, Alasdair"); //comma seperated list of all lecturers
        return temp;
    }


    /**Update materials for a module
     * @param Materials (Whatever the pdf data type is)
     */
    public void updateModuleMaterial(String Materials){
    }

    /**Gets an arraylist of student id's in the lecturer's module
     * @return An ArrayList of students' userIDs
     */
    public ArrayList<String> getEnrolledStudents(){
        return null;
    }



    /**Updates the student lab mark
     * @param studentID
     * @param attNo
     * @param Mark
     */
    public void updateStudentLabMark(String studentID, int attNo, Double Mark){

    }


    /**Updates the student exam mark
     * @param studentID
     * @param attNo
     * @param Mark
     */
    public void updateStudentExamMark(String studentID, int attNo, Double Mark){

    }
}
