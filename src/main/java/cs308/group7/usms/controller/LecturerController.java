package cs308.group7.usms.controller;

import cs308.group7.usms.model.Lecturer;
import cs308.group7.usms.model.User;
import cs308.group7.usms.ui.LecturerUI;
import cs308.group7.usms.ui.MainUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                buttons.get("GIVE MARK").setOnAction((event)->pageSetter("GIVE MARK", false));
                buttons.get("CHECK MATERIAL").setOnAction((event)->pageSetter("MATERIALS", false));
                Map<String, Node> currFields = lecUI.getCurrentFields();
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(currFields.get("OLD PASSWORD").getAccessibleText(), currFields.get("NEW PASSWORD").getAccessibleText()));

                break;
            case "VIEW MODULE":
                lecUI.module(getModuleInformation());
                buttons =lecUI.getCurrentButtons();
                break;
            case "GIVE MARK":
                lecUI.mark(getEnrolledStudents());
                buttons =lecUI.getCurrentButtons();
                break;

            case "MATERIALS":
                lecUI.materials(getModuleInformation().get("Id"), getAllLectureMaterials(lecUI.getValues().get("ID")), getModuleInformation().get("Semesters"));
                buttons = lecUI.getCurrentButtons();
                Map<String, String> currValues = lecUI.getValues();
                buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                buttons.get("VIEW LAB MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                buttons.get("CHANGE LECTURE MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(currValues.get("WEEK")), Integer.parseInt(currValues.get("SEMESTER")), "Lecture", lecUI.uploadFile()));
                buttons.get("CHANGE LAB MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(currValues.get("WEEK")), Integer.parseInt(currValues.get("SEMESTER")), "Lab", lecUI.uploadFile()));

                break;
            case "OPEN PDF":
                lecUI.displayPDF(null, "LECTURER NOTES");
                buttons = lecUI.getCurrentButtons();
                break;

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
        temp.put("Semesters", "1&2");
        temp.put("Lecturers", "Bob Atkey, Jules, Alasdair"); //comma seperated list of all lecturers
        return temp;
    }


    /**Updates the module material for a class
     * @param week - the week the material is for
     * @param semester - the semester the material is for
     * @param type - the type of the material (lab or lecture)
     * @param file - file to be uploaded
     */
    public void updateModuleMaterial(int week, int semester, String type, File file){

    }


    /** Get if weekly lecture materials for a module exist or not
     * @param moduleID
     * @return List of map containing if lab materials and lecture materials exist (goes from weeks 1-12) (1-24 if 2 semesters)
     */
    public List<Map<String, Boolean>> getAllLectureMaterials(String moduleID){

        Map<String, Boolean> temp = new HashMap<>();
        ArrayList<Map<String, Boolean>> tempList = new ArrayList<>();

        temp.put("Lab", true);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);


        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", true);
        temp.put("Lecture", false);
        tempList.add(temp);

        temp = new HashMap<>();
        temp.put("Lab", false);
        temp.put("Lecture", true);
        tempList.add(temp);



        return tempList;
    }



    /**Gets all the students in the lecturer's module
     * @return List of maps with user fields and their values (eg: forename, "john")
     */
    public List<Map<String, String>> getEnrolledStudents(){
        List<Map<String, String>> enrolled = new ArrayList<>();
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
        temp.put("decision", "Award");
        temp.put("labMark", "69");
        temp.put("examMark", "72");
        enrolled.add(temp);
        return enrolled;
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
