package cs308.group7.usms.controller;

import cs308.group7.usms.model.Lecturer;
import cs308.group7.usms.ui.LecturerUI;
import cs308.group7.usms.ui.MainUI;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class LecturerController{

    private final String lecturerID;
    private final LecturerUI lecUI;

    public LecturerController(String id) {
        this.lecturerID = id;
        lecUI = new LecturerUI();
        lecUI.home();
        lecUI.displayFirstScene();
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
        return null;
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
