package cs308.group7.usms.controller;

import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.ui.MainUI;
import cs308.group7.usms.ui.StudentUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.jpedal.exception.PdfException;

import java.sql.SQLException;
import java.util.*;

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
                buttons.get("VIEW DECISION").setOnAction((event)->pageSetter("VIEW DECISION", false));
                buttons.get("VIEW COURSE").setOnAction((event)->pageSetter("VIEW COURSE", false));
                buttons.get("VIEW MODULES").setOnAction((event)->pageSetter("VIEW MODULES", false));
                buttons.get("OPEN FILE").setOnAction((event)->pageSetter("OPEN PDF", false));

                Map<String, Node> currFields = stuUI.getCurrentFields();
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(currFields.get("OLD PASSWORD").getAccessibleText(), currFields.get("NEW PASSWORD").getAccessibleText()));

                break;
            case "VIEW DECISION":
                stuUI.decision(getModules(), getMarks(), "AWARD");
                buttons = stuUI.getCurrentButtons();
                break;
            case "VIEW COURSE":
                stuUI.course(getCourseInfo());
                buttons = stuUI.getCurrentButtons();
                break;
            case "VIEW MODULES":
                stuUI.modules(getModules());
                buttons =stuUI.getCurrentButtons();
                buttons.get("VIEW MATERIALS").setOnAction(event -> pageSetter("MATERIALS", false));
                break;
            case "MATERIALS":
                stuUI.materials(stuUI.getValues().get("ID"), getAllLectureMaterials(stuUI.getValues().get("ID")), getTwoSems(stuUI.getValues().get("ID")));
                buttons = stuUI.getCurrentButtons();
                buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                buttons.get("VIEW LAB MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));

                break;
            case "OPEN PDF":
                stuUI.displayPDF(null, "LECTURER NOTES");
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

    /**
     * Changes the password for a user.
     * @param oldPass
     * @param newPass
     */
    public boolean changePassword(String oldPass, String newPass){
        return true;
    }


    private boolean getTwoSems(String moduleID){
        return true;
    }

    /**
     * Gets formatted curriculum information for this student. In the event of an error, returns an empty list.
     * @return A List of Maps representing attended modules, each with the following keys:<br>
     *         {@code Id, Name, Description, Credit, Lecturers}
     */
    public List<Map<String,String>> getCurriculumInfo() {
        try {
            List<Map<String, String>> res = new ArrayList<>();
            for (Module m : getCurrentStudent().getCourse().getModules(true, false, 1)) { // TODO: This only gets modules for semester 1 of first year.
                Map<String, String> moduleMap = new HashMap<>();                                          // TODO: Should be able to access current semester/year from somewhere
                moduleMap.put("Id", m.getModuleID());                                                     // TODO: to determine the parameters for this method.
                moduleMap.put("Name", m.getName());
                moduleMap.put("Description", m.getDescription());
                moduleMap.put("Credit", String.valueOf(m.getCredit()));
                StringBuilder lecturers = new StringBuilder();
                for (Lecturer l : m.getLecturers()) {
                    if (!lecturers.isEmpty()) lecturers.append(", ");
                    lecturers.append(l.getForename()).append(l.getSurname());
                }
                moduleMap.put("Lecturers", lecturers.toString());
                res.add(moduleMap);
            }
            return res;
        } catch (SQLException e) {
            System.out.println("Failed to get curriculum info for student " + studentID + "!: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    /**
     * Gets formatted course information for this student. In the event of an error, returns an empty map.
     * @return A map representing the course, with the following keys:<br>
     *         {@code Id, Name, Description, Level, Years}
     */
    public Map<String,String> getCourseInfo() {
        try {
            Course c = getCurrentStudent().getCourse();
            Map<String, String> courseMap = new HashMap<>();
            courseMap.put("Id", c.getCourseID());
            courseMap.put("Name", c.getName());
            courseMap.put("Description", c.getDescription());
            courseMap.put("Level", c.getLevel());
            courseMap.put("Years", String.valueOf(c.getLength()));
            return courseMap;
        } catch (SQLException e) {
            System.out.println("Failed to get course info for student " + studentID + "!: " + e.getMessage());
            return Collections.emptyMap();
        }
    }


    /**
     * Get lecture material for a module, from a given semester and week.
     * @return A map representing the lecture material, with the following keys:<br>
     *         {@code LectureNote, LabNote}
     * @deprecated Due to be replaced with two methods (for lecture + lab) that return PDFs (in some format)
     */
    public Map<String,String> getLectureMaterials(String moduleID, int semester, int week) {
        try {
            Material m = new Module(moduleID).getMaterial(semester, week);
            Map<String, String> materialMap = new HashMap<>();
            materialMap.put("LectureNote", m.getLectureNote());
            materialMap.put("LabNote", m.getLabNote());
            return materialMap;
        } catch (SQLException e) {
            System.out.println("Failed to get lecture materials for module " + moduleID + " in week " + week + " of semester " + semester + "!: " + e.getMessage());
            return Collections.emptyMap();
        }
    }



    /**
     * Get all lecture material for a module.
     * @param moduleID
     * @return List of map containing if lab materials and lecture materials exist
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

        return tempList;
    }


    /**
     * Get the student's decision. In the event of an error, returns "Unable to load student decision.".
     */
    public String getDecision() {
        try {
            return Student.stringFromStudentDecision(getCurrentStudent().getDecision());
        } catch (SQLException e) {
            System.out.println("Failed to get decision for student " + studentID + "!: " + e.getMessage());
            return "Unable to load student decision.";
        }
    }

    /**
     * Dummy for getting a student's marks for all their modules
     */
    public List<Map<String, String>> getMarks() {
        List<Map<String, String>> marks = new ArrayList<>();
        Map<String, String> temp = new HashMap<>();
        temp.put("moduleID", "CS308");
        temp.put("lab", "96");
        temp.put("exam", "80");
        temp.put("attempt", "2");
        temp.put("grade", "PASS");
        marks.add(temp);

        Map<String, String> temp2 = new HashMap<>();
        temp2.put("moduleID", "CS308");
        temp2.put("lab", "30");
        temp2.put("exam", "20");
        temp2.put("attempt", "1");
        temp2.put("grade", "FAIL");
        marks.add(temp2);
        return marks;
    }

    /**
     * Dummy for getting a student's modules
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

}
