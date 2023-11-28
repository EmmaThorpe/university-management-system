package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.*;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.ui.LecturerUI;
import cs308.group7.usms.ui.StudentUI;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerController extends BaseController {

    private final String lecturerID;
    private final LecturerUI lecUI;

    public LecturerController(String id) {
        this.lecturerID = id;
        lecUI = new LecturerUI();
        pageSetter("DASHBOARD", true);
    }


    /** Sets the page and assigns the events that will occur when you press the buttons
     * @param page - the page being moved to
     * @param initial - if this is the initial page or not
     */
    public void pageSetter(String page, Boolean initial) {
        Map<String, Button> buttons;
        switch (page){
            case "DASHBOARD":
                lecUI.dashboard();
                buttons =lecUI.getCurrentButtons();
                buttons.get("VIEW MODULE").setOnAction((event)->pageSetter("VIEW MODULE", false));
                buttons.get("GIVE MARK").setOnAction((event)->pageSetter("GIVE MARK", false));
                buttons.get("CHECK MATERIAL").setOnAction((event)->pageSetter("MATERIALS", false));
                buttons.get("CHANGE PASSWORD").setOnAction(event -> changePassword(((TextField)lecUI.getCurrentFields().get("OLD PASSWORD")).getText(), ((TextField)lecUI.getCurrentFields().get("NEW PASSWORD")).getText()));
                break;
            case "VIEW MODULE":
                lecUI.module(getModuleInformation());
                buttons =lecUI.getCurrentButtons();
                buttons.get("EDIT").setOnAction((event)-> editModule(
                                lecUI.getValues().get("ID"),
                                ((TextField)lecUI.getCurrentFields().get("EDIT CODE")).getText(),
                                ((TextField)lecUI.getCurrentFields().get("EDIT NAME")).getText(),
                                ((TextArea)lecUI.getCurrentFields().get("EDIT DESCRIPTION")).getText(),
                                ((TextField)lecUI.getCurrentFields().get("EDIT CREDITS")).getText()
                        )
                );
                break;
            case "GIVE MARK":
                final List<Map<String, String>> enrolled = getEnrolledStudents();
                lecUI.mark(enrolled);
                buttons = lecUI.getCurrentButtons();
                if(!enrolled.isEmpty()) {
                    buttons.get("ASSIGN MARK").setOnAction(
                            (event) -> updateStudentMark(
                                    lecUI.getValues().get("StudentID"),
                                    Integer.parseInt(lecUI.getValues().get("AttemptNo")) + 1,
                                    Double.parseDouble(((TextField) lecUI.getCurrentFields().get("ASSIGN LAB MARK")).getText()),
                                    Double.parseDouble(((TextField) lecUI.getCurrentFields().get("ASSIGN EXAM MARK")).getText()),
                                    false
                            )
                    );
                    buttons.get("CHANGE MARK").setOnAction(
                            (event) -> updateStudentMark(
                                    lecUI.getValues().get("StudentID"),
                                    Integer.parseInt(lecUI.getValues().get("AttemptNo")),
                                    Double.parseDouble(((TextField) lecUI.getCurrentFields().get("CHANGE LAB MARK")).getText()),
                                    Double.parseDouble(((TextField) lecUI.getCurrentFields().get("CHANGE EXAM MARK")).getText()),
                                    true
                            )
                    );
                }

                break;

            case "MATERIALS":
                Map<String, String> moduleInfo = getModuleInformation();
                lecUI.materials(moduleInfo.get("Id"), getAllLectureMaterials(moduleInfo.get("Id")));
                buttons = lecUI.getCurrentButtons();
                buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> viewLectureMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), moduleInfo.get("Id")));
                buttons.get("VIEW LAB MATERIAL").setOnAction(event -> viewLabMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), moduleInfo.get("Id")));
                buttons.get("CHANGE LECTURE MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), Integer.parseInt(lecUI.getValues().get("SEMESTER")), "Lecture", lecUI.uploadFile()));
                buttons.get("CHANGE LAB MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), Integer.parseInt(lecUI.getValues().get("SEMESTER")), "Lab", lecUI.uploadFile()));

                break;
            case "OPEN PDF":
                lecUI.displayPDF(new File(App.FILE_DIR + File.separator + "Material.pdf"));
                break;


        }
        buttons = lecUI.getCurrentButtons();
        buttons.get("LOG OUT").setOnAction(event -> lecUI.hideStage());
        buttons.get("HOME").setOnAction(event -> pageSetter("DASHBOARD", false));

        if(initial){
            lecUI.displayFirstScene();
        }else{
            lecUI.displayScene();
        }
    }



    private Lecturer getCurrentLecturer() throws SQLException {
        return new Lecturer(lecturerID);
    }

    /**
     * Changes a lecturer's password.
     */
    public void changePassword(String oldPass, String newPass) { changePassword(lecUI, lecturerID, oldPass, newPass); }


    /**
     * Gets info of the module that the lecturer runs
     * @return A map containing module information
     */
    public Map<String,String> getModuleInformation() {
        try {
            Lecturer l = getCurrentLecturer();
            if(l.getModuleID()!=null) {
                String moduleID = l.getModuleID();
                Module mod = new Module(moduleID);
                Map<String, String> moduleInfo = new HashMap<>();
                moduleInfo.put("Id", mod.getModuleID());
                moduleInfo.put("Name", mod.getName());
                moduleInfo.put("Description", mod.getDescription());
                moduleInfo.put("Credit", Integer.toString(mod.getCredit()));
                moduleInfo.put("Lecturer", getCurrentLecturer().getForename());
                return moduleInfo;
            }
            else throw new SQLException("No module assigned to lecturer");
        } catch (SQLException e) {
            System.err.println("Failed to get module information for lecturer " + lecturerID + ": " + e.getMessage());
            return Collections.emptyMap();
        }

    }

    public void viewLabMaterial(int week, String moduleID) {
        try {
            downloadLabNote(moduleID, week);
            pageSetter("OPEN PDF", false);
        } catch (Exception e) {
            lecUI.makeNotificationModal(null, "Failed to view lab material for week " + week + ": " + e.getMessage(), false);
        }
    }

    public void viewLectureMaterial(int week, String moduleID) {
        try {
            downloadLectureNote(moduleID, week);
            pageSetter("OPEN PDF", false);
        } catch (Exception e) {
            lecUI.makeNotificationModal(null, "Failed to view lecture material for week " + week + ": " + e.getMessage(), false);
        }
    }


    /** Get if weekly lecture materials for a module exist or not
     * @param moduleID - the module the lecturer teaches
     * @return List of map containing if lab materials and lecture materials exist (goes from weeks 1-12) (1-24 if 2 semesters)
     */
    public List<Map<String, Boolean>> getAllLectureMaterials(String moduleID) {
        return getAllLectureMaterials(lecUI, null, moduleID);
    }



    /**
     * Gets all the students in the lecturer's module alongside their current scoring for the lecturer's module
     * @return List of maps with user fields and their values (eg: forename, "john"), including mark fields and values
     */
    public List<Map<String, String>> getEnrolledStudents() {
        try {
            Lecturer l = getCurrentLecturer();

            if (l.getModuleID() == null) throw new SQLException("No module assigned to lecturer");

            String moduleID = l.getModuleID();
            List<Map<String, String>> enrolled = new ArrayList<>();
            DatabaseConnection db = App.getDatabaseConnection();
            CachedRowSet result = db.select(new String[]{"Student, Curriculum"}, new String[]{"UserID"}, new String[]{"Student.CourseID = Curriculum.CourseID AND Student.yearOfStudy = Curriculum.Year AND Curriculum.ModuleID = " + db.sqlString(moduleID)});

            while (result.next()) {
                Student stu = new Student(result.getString("UserID"));
                HashMap<String, String> studentDetailsMap = new HashMap<>();
                studentDetailsMap.put("userID", stu.getUserID());
                final User MANAGER = stu.getManager();
                if (MANAGER != null) studentDetailsMap.put("managerID", MANAGER.getUserID());
                studentDetailsMap.put("forename", stu.getForename());
                studentDetailsMap.put("surname", stu.getSurname());

                final Double labMark = stu.getMark(moduleID).getLabMark();
                final Double examMark = stu.getMark(moduleID).getExamMark();
                final int attempt = stu.getNumberOfAttempts(moduleID);

                studentDetailsMap.put("labMark", (labMark == null) ? "N/A" : Double.toString(labMark));
                studentDetailsMap.put("examMark", (examMark == null) ? "N/A" : Double.toString(examMark));
                studentDetailsMap.put("attemptNo", Integer.toString(attempt));

                enrolled.add(studentDetailsMap);
            }

            return enrolled;
        } catch (SQLException e) {
            System.err.println("Failed to get enrolled students for lecturer " + lecturerID + ": " + e.getMessage());
            return Collections.emptyList();
        }

    }

    /**
     * Updates the module material for a class
     * @param week - the week the material is for
     * @param semester - the semester the material is for
     * @param type - the type of the material (lab or lecture)
     * @param file - file to be uploaded
     */
    public void updateModuleMaterial(int week, int semester, String type, File file) {
        final int WEEK_TARGET = week + ((semester == 2) ? StudentUI.WEEKS_PER_SEM : 0);
        try {
            String moduleID = getCurrentLecturer().getModuleID();
            if (moduleID == null) throw new SQLException("No module assigned to lecturer");
            Material m = new Material(moduleID, WEEK_TARGET);
            final boolean success;
            switch(type) {
                case "Lecture" -> success = m.setLectureNote(file);
                case "Lab" -> success = m.setLabNote(file);
                default -> throw new IllegalArgumentException("Invalid material type: " + type);
            }
            if (!success) throw new SQLException("Failed to update material");
            lecUI.makeNotificationModal(null, "Uploaded " + type + " material for week " + week + " of semester " + semester + " successfully!", true);
            pageSetter("MATERIALS", false);
        } catch (SQLException e) {
            lecUI.makeNotificationModal(null, "Failed to update " + type + " material for week " + week + " of semester " + semester + ": " + e.getMessage(), false);
        }
    }



    /**
     * add the student mark
     * @param studentID - the student ID for the student being added
     * @param attNo - the attempt number wanting to be added
     * @param labMark - the new lab mark for that student's attempt
     * @param examMark - the new exam mark for that student's attempt
     * @param existing - value representing if the student mark being updated exists or not
     */
    public void updateStudentMark(String studentID, int attNo, Double labMark, Double examMark, boolean existing){

        try {
            Lecturer lec = new Lecturer(lecturerID);

            if (lec.getModuleID() == null) throw new SQLException("No module assigned to lecturer");

            Mark m = new Mark(studentID, lec.getModuleID(), attNo);
            m.setLabMark(labMark);
            m.setExamMark(examMark);

            if (existing) {
                lecUI.makeNotificationModal("CHANGE MARK", "MARK CHANGED SUCCESSFULLY", true);
            } else {
                lecUI.makeNotificationModal("ASSIGN MARK", "MARK ADDED SUCCESSFULLY", true);

            }
            pageSetter("GIVE MARK", false);
        } catch (SQLException e) {
            lecUI.makeNotificationModal("ASSIGN MARK", "FAILED TO UPDATE MARK FOR STUDENT " + studentID + " (ATTEMPT #" + attNo + ")." + e.getMessage(), false);
        }
    }



    /**
     * Updates information about a module.
     * @param oldCode The old module code
     * @param code The new module code
     * @param name The new module name
     * @param credit The new module credit
     */
    public void editModule(String oldCode, String code, String name, String description, String credit){
        try {
            editModule(oldCode, code, name, description, Integer.parseInt(credit));
            lecUI.makeNotificationModal("EDIT", "Updated module successfully!", true);
            pageSetter("VIEW MODULE", false);
        } catch (SQLException e) {
            lecUI.makeNotificationModal("EDIT","Error updating module " + e.getMessage(), false);
        }
    }


}
