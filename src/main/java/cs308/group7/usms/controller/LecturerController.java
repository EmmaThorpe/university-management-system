package cs308.group7.usms.controller;

import cs308.group7.usms.ui.LecturerUI;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

public class LecturerController extends UIController{
    String userID;
    LecturerUI lecUI;

    public LecturerController(String id){
        currentStage = new Stage();
        userID = id;
        lecUI = new LecturerUI();
        displayFirstScene(lecUI.home());
    }

    /**Changes the password for a user.
     * @param oldPass
     * @param newPass
     * @return Boolean value representing if the password change was successful or not.
     */
    public boolean changePassword(String oldPass, String newPass){
        return true;
    }


    /**Gets info of the curriculum the student is in
     * @return A map containing module information
     */
    public Map<String,String> getModuleInformation(){
        return null;
    }


    /**Update materials for a module
     * @param Materials
     */
    public void updateModuleMaterial(String Materials){
    }

    /**
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
