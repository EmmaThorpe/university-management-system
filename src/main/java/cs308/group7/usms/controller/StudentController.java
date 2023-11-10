package cs308.group7.usms.controller;

import cs308.group7.usms.model.Student;
import cs308.group7.usms.ui.MainUI;
import cs308.group7.usms.ui.StudentUI;

import java.sql.SQLException;
import java.util.Map;

public class StudentController{
    Student currStu;
    StudentUI stuUI;

    public StudentController(String id) throws SQLException {
        currStu = new Student(id);
        stuUI = new StudentUI();
        stuUI.home();
        stuUI.displayScene();
    }

    /**Changes the password for a user.
     * @param oldPass
     * @param newPass
     */
    public boolean changePassword(String oldPass, String newPass){
        return true;
    }

    /**Gets info of the curriculum the student is in
     * @return A map containing curriculum information
     */
    public Map<String,String> getCurriculumInfo(){
        return null;
    }


    /** Get weekly lecture materials for a course
     * @param moduleID
     * @param week
     * @return ??? Whatever the pdf type is
     */
    public String getLectureMaterials(String moduleID, int week){
        return "";
    }


    /**Returns the student decision
     * @return The result the student has received.
     */
    public String getDecision(){
        return "Resit";
    }

}
