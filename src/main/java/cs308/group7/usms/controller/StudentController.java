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
                stuUI.decision(getCurriculumInfo());
                buttons = stuUI.getCurrentButtons();
                break;
            case "VIEW COURSE":
                stuUI.course(getCourseInfo());
                buttons = stuUI.getCurrentButtons();
                break;
            case "VIEW MODULES":
                stuUI.modules(getCurriculumInfo());
                buttons =stuUI.getCurrentButtons();
                buttons.get("VIEW MATERIALS").setOnAction(event -> pageSetter("MATERIALS", false));
                break;
            case "MATERIALS":
                stuUI.materials(getAllLectureMaterials(stuUI.getID()), getTwoSems("s"));
                buttons = stuUI.getCurrentButtons();
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
     * @return ??? List<Map<String, ??? >>
     * @deprecated When the new format is implemented, this should probably be removed.
     *             Returning all PDFs from the database is likely to be intensive and unnecessary.
     *             (this will work fine if you're just wanting the Material objects though, since it'll only load the PDFs when they're needed)
     */
    public List<Map<String, String>> getAllLectureMaterials(String moduleID){
        return null;
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

}
