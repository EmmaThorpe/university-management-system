package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.ui.StudentUI;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class StudentController extends BaseController {

    private final String studentID;
    private final StudentUI stuUI;

    public StudentController(String id) {
        studentID = id;
        stuUI = new StudentUI();
        pageSetter("DASHBOARD", true);
    }


    /** Sets the page and assigns the events that will occur when you press the buttons
     * @param page - the page being moved to
     * @param initial - if this is the initial page or not
     */
    public void pageSetter(String page, Boolean initial){
        Map<String, Button> buttons = null;
        switch (page){
            case "DASHBOARD":
                stuUI.dashboard();
                buttons =stuUI.getCurrentButtons();
                buttons.get("VIEW DECISION").setOnAction((event)->pageSetter("VIEW DECISION", false));
                buttons.get("VIEW COURSE").setOnAction((event)->pageSetter("VIEW COURSE", false));
                buttons.get("VIEW MODULES").setOnAction((event)->pageSetter("VIEW MODULES", false));
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(((TextField)stuUI.getCurrentFields().get("OLD PASSWORD")).getText(), ((TextField)stuUI.getCurrentFields().get("NEW PASSWORD")).getText()));
                break;
            case "VIEW DECISION":
                stuUI.decision(getModules(), getMarks(), getDecision());
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
                stuUI.materials(stuUI.getValues().get("ID"), getAllLectureMaterials(stuUI.getValues().get("ID")), spansTwoSems(stuUI.getValues().get("ID")));
                buttons = stuUI.getCurrentButtons();
                buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> viewLectureNote(stuUI.getValues().get("ID"), Integer.parseInt(stuUI.getValues().get("WEEK"))));
                buttons.get("VIEW LAB MATERIAL").setOnAction(event -> viewLabNote(stuUI.getValues().get("ID"), Integer.parseInt(stuUI.getValues().get("WEEK"))));

                break;
            case "OPEN PDF":
                stuUI.displayPDF(new File(App.FILE_DIR + File.separator + "Material.pdf"));
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
     * Changes a student's password.
     */
    public void changePassword(String oldPass, String newPass){ changePassword(stuUI, studentID, oldPass, newPass); }


    /**
     * Gets whether a given module spans both semesters for a given course.
     */
    private boolean spansTwoSems(String moduleID) {
        try {
            return spansTwoSems(stuUI.getValues().get("ID"), moduleID);
        } catch (SQLException e) {
            System.err.println("Failed to get whether module " + moduleID + " spans two semesters: " + e.getMessage());
            return false;
        }

    }

    /**
     * Gets formatted course information for this student. In the event of an error, returns an empty map.
     * @return A map representing the course, with the following keys:<br>
     *         {@code Id, Name, Description, Level, Years}
     */
    public Map<String,String> getCourseInfo() {
        try {
            Student s = getCurrentStudent();
            if(s.getCourseID()!=null) {
                Course c = s.getCourse();
                Map<String, String> courseMap = new HashMap<>();
                courseMap.put("Id", c.getCourseID());
                courseMap.put("Name", c.getName());
                courseMap.put("Description", c.getDescription());
                courseMap.put("Level", c.getLevel());
                courseMap.put("Years", String.valueOf(c.getLength()));
                courseMap.put("Department", String.valueOf(c.getDepartment()));
                return courseMap;
            }
            else{
                return Collections.emptyMap();
            }
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "FAILED TO GET COURSE INFO FOR STUDENT " + studentID + "!: " + e.getMessage(), false);
            return Collections.emptyMap();
        }
    }

    /**
     * Display the lecture note for a module, from a given week.
     */
    public void viewLectureNote(String moduleID, int week) {
        try {
            downloadLectureNote(moduleID, week);
            pageSetter("OPEN PDF", false);
        } catch (Exception e) {
            stuUI.makeNotificationModal(null, "Failed to get the lecture notes for module " + moduleID + " in week " + week + "!: " + e.getMessage(), false);
        }
    }

    /**
     * Display the lab note for a module, from a given week.
     */
    public void viewLabNote(String moduleID, int week) {
        try {
            downloadLabNote(moduleID, week);
            pageSetter("OPEN PDF", false);
        } catch (Exception e) {
            stuUI.makeNotificationModal(null, "Failed to get the lab notes for module " + moduleID + " in week " + week + "!: " + e.getMessage(), false);
        }
    }

    /**
     * A list of maps representing whether lecture/lab materials exist for each week of a module.
     */
    public List<Map<String, Boolean>> getAllLectureMaterials(String moduleID) {
        try {
            final String courseID = getCurrentStudent().getCourseID();
            if (courseID == null) return Collections.emptyList();
            return getAllLectureMaterials(stuUI, courseID, moduleID);
        } catch (SQLException e) {
            System.err.println("FAILED TO GET THE LECTURE MATERIALS FOR MODULE " + moduleID + "!: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    /**
     * Get the student's decision. In the event of an error, returns "Unable to load student decision.".
     */
    public String getDecision() {
        try {
            return Student.stringFromStudentDecision(getCurrentStudent().getDecision());
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "FAILED TO GET DECISION FOR STUDENT " + studentID + "!: " + e.getMessage(), false);
            return "Unable to load student decision.";
        }
    }

    private Map<String, String> mapMark(Mark m) {
        Map<String, String> markMap = new HashMap<>();
        markMap.put("moduleID", m.getModuleID());
        markMap.put("lab", String.valueOf(m.getLabMark()));
        markMap.put("exam", String.valueOf(m.getExamMark()));
        markMap.put("attempt", String.valueOf(m.getAttemptNo()));
        try { markMap.put("grade", (m.passes()) ? "PASS" : "FAIL"); }
        catch (IllegalStateException e) { markMap.put("grade", "N/A"); }
        return markMap;
    }

    /**
     * Gets formatted marks for this student. In the event of an error, returns an empty list.
     */
    public List<Map<String, String>> getMarks() {
        try {
            Student s = getCurrentStudent();
            final int currentYear = s.getYearOfStudy();
            List<Map<String, String>> marks = new ArrayList<>();
            if(s.getCourseID()!=null) {
                for (Module m : s.getCourse().getModules(currentYear)) {
                    // Add most recent mark
                    Mark mostRecentMark = s.getMark(m.getModuleID());
                    marks.add(mapMark(mostRecentMark));
                    // Add previous marks
                    for (int i = mostRecentMark.getAttemptNo() - 1; i > 0; i--) {
                        Mark mark = s.getMark(m.getModuleID(), i);
                        marks.add(mapMark(mark));
                    }
                }
                return marks;
            }
            else{
                return Collections.emptyList();
            }
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "FAILED TO GET MARKS FOR STUDENT " + studentID + "!: " + e.getMessage(), false);
            return Collections.emptyList();
        }
    }

    /**
     * Gets formatted module information for this student. In the event of an error, returns an empty list.
     * @return A list of maps representing the modules, with the following keys:<br>
     *         {@code Id, Name, Description, Credit, Lecturers}
     */
    public List<Map<String, String>> getModules(){
        List<Map<String, String>> modules = new ArrayList<>();
        try {
            Student s = getCurrentStudent();
            final int currentYear = s.getYearOfStudy();
            if(s.getCourseID()!=null) {
                for (Module m : s.getCourse().getModules(currentYear)) {
                    Map<String, String> moduleMap = new HashMap<>();
                    moduleMap.put("Id", m.getModuleID());
                    moduleMap.put("Name", m.getName());
                    moduleMap.put("Description", m.getDescription());
                    moduleMap.put("Credit", String.valueOf(m.getCredit()));
                    StringBuilder lecturers = new StringBuilder();
                    for (Lecturer l : m.getLecturers()) {
                        if (!lecturers.isEmpty()) lecturers.append(", ");
                        lecturers.append(l.getForename()).append(" ").append(l.getSurname());
                    }
                    moduleMap.put("Lecturers", lecturers.toString());
                    modules.add(moduleMap);
                }
                return modules;
            }
            else{
                return Collections.emptyList();
            }
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "FAILED TO GET MODULES FOR STUDENT " + studentID + "!: " + e.getMessage(), false);
            return Collections.emptyList();
        }

    }

}
