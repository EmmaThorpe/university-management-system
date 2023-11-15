package cs308.group7.usms.controller;

import cs308.group7.usms.model.Student;
import cs308.group7.usms.ui.MainUI;
import cs308.group7.usms.ui.StudentUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.jpedal.exception.PdfException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentController{

    private final String studentID;
    private final StudentUI stuUI;

    public StudentController(String id) {
        studentID = id;
        stuUI = new StudentUI();
        pageSetter("DASHBOARD", true);
    }

    public void pageSetter(String page, Boolean initial){
        Map<String, Button> buttons = null;
        switch (page){
            case "DASHBOARD":
                stuUI.dashboard();
                buttons =stuUI.getCurrentButtons();
                buttons.get("VIEW MODULES").setOnAction((event)->pageSetter("VIEW MODULES", false));
                buttons.get("OPEN FILE").setOnAction((event)->pageSetter("OPEN PDF", false));


                Map<String, Node> currFields = stuUI.getCurrentFields();
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(currFields.get("OLD PASSWORD").getAccessibleText(), currFields.get("NEW PASSWORD").getAccessibleText()));

                break;
            case "VIEW MODULES":
                stuUI.modules(getCurriculumInfo());
                buttons =stuUI.getCurrentButtons();
                buttons.get("VIEW MATERIALS").setOnAction(event -> pageSetter("MATERIALS", false));
                break;
            case "MATERIALS":
                stuUI.materials(getAllLectureMaterials(stuUI.getID()));
                break;
            case "OPEN PDF":
                stuUI.displayPDF(null);
                buttons = stuUI.getCurrentButtons();
                break;
        }
        buttons.get("LOG OUT").setOnAction(event -> stuUI.hideStage());
        buttons.get("HOME").setOnAction(event -> pageSetter("DASHBOARD", false));

        if(initial){
            stuUI.displayFirstScene();
        }else{
            stuUI.displayScene();
        }
    }




    private Student getCurrentStudent() throws SQLException { return new Student(studentID); }

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
    public List<Map<String,String>> getCurriculumInfo(){

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


    /**Gets info of the course the student is in
     * @return A map containing course information
     */
    public Map<String,String> getCourseInfo(){
        return null;
    }


    /** Get weekly lecture materials for a module
     * @param moduleID
     * @param week
     * @return ??? Whatever the pdf type is
     */
    public String getLectureMaterials(String moduleID, int week){
        return "";
    }



    /** Get ALL weekly lecture materials for a module
     * @param moduleID
     * @return ??? List<Map<String, ??? >>
     */
    public List<Map<String, String>> getAllLectureMaterials(String moduleID){
        return null;
    }


    /**Returns the student decision
     * @return The result the student has received.
     */
    public String getDecision(){
        return "Resit";
    }

}
