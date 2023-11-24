package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.ui.MainUI;
import cs308.group7.usms.ui.StudentUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.Nullable;
import org.jpedal.exception.PdfException;

import java.io.*;
import javax.sql.rowset.CachedRowSet;
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
                stuUI.materials(stuUI.getValues().get("ID"), getAllLectureMaterials(stuUI.getValues().get("ID")), getTwoSems(stuUI.getValues().get("ID")));
                buttons = stuUI.getCurrentButtons();
                buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                buttons.get("VIEW LAB MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));

                break;
            case "OPEN PDF":
                stuUI.displayPDF(getLectureNote(stuUI.getValues().get("ID"), 1, Integer.parseInt(stuUI.getValues().get("WEEK"))), "LECTURER NOTES");
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


    /**
     * Gets whether a given module spans both semesters for a given course.
     */
    private boolean getTwoSems(String moduleID) {
        try {
            Student s = getCurrentStudent();
            String courseID = s.getCourseID();
            DatabaseConnection db = App.getDatabaseConnection();
            CachedRowSet res = db.select(
                    new String[]{"Curriculum"},
                    new String[]{"ModuleID"},
                    new String[]{
                            "CourseID = " + db.sqlString(courseID),
                            "ModuleID = " + db.sqlString(moduleID),
                            "Semester1 = TRUE",
                            "Semester2 = TRUE"
                    }
            );
            return res.next();
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "Failed to get whether module " + moduleID + " spans both semesters: " + e.getMessage(), false);
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
            Course c = getCurrentStudent().getCourse();
            Map<String, String> courseMap = new HashMap<>();
            courseMap.put("Id", c.getCourseID());
            courseMap.put("Name", c.getName());
            courseMap.put("Description", c.getDescription());
            courseMap.put("Level", c.getLevel());
            courseMap.put("Years", String.valueOf(c.getLength()));
            courseMap.put("Department", String.valueOf(c.getDepartment()));
            return courseMap;
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "Failed to get course info for student " + studentID + "!: " + e.getMessage(), false);
            return Collections.emptyMap();
        }
    }

    /**
     * Get the lecture note for a module, from a given semester and week.
     * @return A file representing the lecture note, or null if it doesn't exist
     */
    @Nullable
    public File getLectureNote(String moduleID, int semester, int week) {
        try {
            Material m = new Module(moduleID).getMaterial(semester, week);
            Optional<byte[]> lectureNote = m.getLectureNote();
            if (lectureNote.isEmpty()) return null;

            File f = new File(App.FILE_DIR + File.separator + "Material.pdf");
            try (OutputStream out = new FileOutputStream(f)) { out.write(lectureNote.get()); }
            return f;
        } catch (Exception e) {
            stuUI.makeNotificationModal(null, "Failed to get the lecture note for module " + moduleID + " in week " + week + " of semester " + semester + "!: " + e.getMessage(), false);
            return null;
        }
    }

    /**
     * Get the lab note for a module, from a given semester and week.
     * @return A file representing the lab note, or null if it doesn't exist
     */
    @Nullable
    public File getLabNote(String moduleID, int semester, int week) {
        try {
            Material m = new Module(moduleID).getMaterial(semester, week);
            Optional<byte[]> labNote = m.getLabNote();
            if (labNote.isEmpty()) return null;

            File f = new File(App.FILE_DIR + File.separator + "Material.pdf");
            try (OutputStream out = new FileOutputStream(f)) { out.write(labNote.get()); }
            return f;
        } catch (Exception e) {
            stuUI.makeNotificationModal(null, "Failed to get the lab note for module " + moduleID + " in week " + week + " of semester " + semester + "!: " + e.getMessage(), false);
            return null;
        }
    }

    /**
     * A list of maps representing whether lecture/lab materials exist for each week of a module.
     */
    public List<Map<String, Boolean>> getAllLectureMaterials(String moduleID) {
        DatabaseConnection db = App.getDatabaseConnection();
        List<Map<String, Boolean>> materials = new ArrayList<>();

        try {
            CachedRowSet max_res = db.select(
                new String[]{"Material"},
                new String[]{"MAX(Week) AS MaxWeek"},
                new String[]{"ModuleID = " + db.sqlString(moduleID)}
            );
            max_res.next();
            int maxWeek = max_res.getInt("MaxWeek");

            CachedRowSet res = db.executeQuery("SELECT Week, (CASE WHEN LectureNote IS NOT NULL THEN TRUE ELSE FALSE END) AS LectureNote, (CASE WHEN LabNote IS NOT NULL THEN TRUE ELSE FALSE END) AS LabNote FROM Material WHERE ModuleID = " + db.sqlString(moduleID) + " ORDER BY Week ASC");
            res.next();
            for (int i = 1; i <= maxWeek; i++) {
                int week = res.getInt("Week");
                Map<String, Boolean> w = new HashMap<>();
                if (week == i) {
                    w.put("Lecture", res.getBoolean("LectureNote"));
                    w.put("Lab", res.getBoolean("LabNote"));
                    materials.add(w);
                    res.next();
                } else {
                    w.put("Lecture", false);
                    w.put("Lab", false);
                    materials.add(w);
                }
            }
            return materials;
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "Failed to get lecture materials for module " + moduleID + "!: " + e.getMessage(), false);
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
            stuUI.makeNotificationModal(null, "Failed to get decision for student " + studentID + "!: " + e.getMessage(), false);
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
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "Failed to get marks for student " + studentID + "!: " + e.getMessage(), false);
            return Collections.emptyList();
        }
    }

    /**
     * Gets formatted module information for this student. In the event of an error, returns an empty list.
     * @return A list of maps representing the modules, with the following keys:<br>
     *         {@code Id, Name, Description, Credit, Lecturers}
     */
    public List<Map<String, String>> getModules(){
        DatabaseConnection db = App.getDatabaseConnection();
        List<Map<String, String>> modules = new ArrayList<>();
        try {
            Student s = getCurrentStudent();
            final int currentYear = s.getYearOfStudy();
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
        } catch (SQLException e) {
            stuUI.makeNotificationModal(null, "Failed to get modules for student " + studentID + "!: " + e.getMessage(), false);
            return Collections.emptyList();
        }

    }

}
