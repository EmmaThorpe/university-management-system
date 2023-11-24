package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.Lecturer;
import cs308.group7.usms.model.User;
import cs308.group7.usms.model.Student;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.ui.LecturerUI;
import cs308.group7.usms.ui.MainUI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerController{

    private final String lecturerID;
    private final LecturerUI lecUI;

    public LecturerController(String id) throws SQLException {
        this.lecturerID = id;
        lecUI = new LecturerUI();
        pageSetter("DASHBOARD", true);
    }


    public void pageSetter(String page, Boolean initial) {
        Map<String, Button> buttons = null;
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
                try{
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
                }catch(java.sql.SQLException e){
                    System.out.println("testing");
                };

                break;
            case "GIVE MARK":
                try{
                    lecUI.mark(getEnrolledStudents());
                    buttons =lecUI.getCurrentButtons();
                    buttons.get("ASSIGN LAB MARK").setOnAction(
                            (event)->
                                    updateStudentLabMark(
                                            lecUI.getValues().get("StudentID"),
                                            // NOTE: assuming that
                                            // attempt number should not be a lecturer's concern when setting
                                            // a student's
                                            // mark and the attempt number should be controlled on controller
                                            Integer.parseInt(lecUI.getValues().get("AttemptNo")),
                                            Double.parseDouble(((TextField)lecUI.getCurrentFields().get("LAB MARK")).getText())
                                    )
                    );
                    buttons.get("ASSIGN EXAM MARK").setOnAction(
                            (event)->
                                    updateStudentExamMark(
                                            lecUI.getValues().get("StudentID"),
                                            Integer.parseInt(lecUI.getValues().get("AttemptNo")),
                                            Double.parseDouble(((TextField)lecUI.getCurrentFields().get("EXAM MARK")).getText())
                                    )
                    );
                }catch(java.sql.SQLException e){

                }




                break;

            case "MATERIALS":
                try{
                    Map<String, String> moduleInfo = getModuleInformation();
                    lecUI.materials(moduleInfo.get("Id"), getAllLectureMaterials(lecUI.getValues().get("ID")), moduleInfo.get("Semesters"));
                    buttons = lecUI.getCurrentButtons();
                    buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                    buttons.get("VIEW LAB MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                    buttons.get("CHANGE LECTURE MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), Integer.parseInt(lecUI.getValues().get("SEMESTER")), "Lecture", lecUI.uploadFile()));
                    buttons.get("CHANGE LAB MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), Integer.parseInt(lecUI.getValues().get("SEMESTER")), "Lab", lecUI.uploadFile()));

                }
                catch(java.sql.SQLException e){

                }

                break;
            case "OPEN PDF":
                lecUI.displayPDF(null, "LECTURER NOTES");
                buttons = lecUI.getCurrentButtons();
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

    /**Changes the password for a user.
     * @param oldPass
     * @param newPass
     */
    public boolean changePassword(String oldPass, String newPass){
        System.out.println(oldPass + " " + newPass);
        return true;
    }


    /**Gets info of the module that the lecturer runs
     * @return A map containing module information
     */
    public Map<String,String> getModuleInformation() throws SQLException {
        String moduleID = getCurrentLecturer().getModule().getModuleID();
        Module mod = new Module(moduleID);
        Map<String,String> moduleInfo = new HashMap<String, String>();
        DatabaseConnection db = App.getDatabaseConnection();
        String Semesters = "";
        CachedRowSet result = db.select(new String[]{"Curriculum"}, new String[]{"Semester1, Semester2"}, new String[]{"ModuleID = " + db.sqlString(moduleID)});
        result.next();
        if (result.getBoolean("Semester1")) {
            Semesters += "1";
            if(result.getBoolean("Semester2")) {
                Semesters += "&2";
            }
        } else Semesters += "2";

        moduleInfo.put("Id", mod.getModuleID());
        moduleInfo.put("Name", mod.getName());
        moduleInfo.put("Description", mod.getDescription());
        moduleInfo.put("Credit", Integer.toString(mod.getCredit()));
        moduleInfo.put("Semesters", Semesters );
        moduleInfo.put("Lecturer", getCurrentLecturer().getForename());
        return moduleInfo;

    }


    /** Get if weekly lecture materials for a module exist or not
     * @param moduleID - the module the lecturer teaches
     * @return List of map containing if lab materials and lecture materials exist (goes from weeks 1-12) (1-24 if 2 semesters)
     */
    public List<Map<String, Boolean>> getAllLectureMaterials(String moduleID) throws SQLException {

        DatabaseConnection db = App.getDatabaseConnection();
        String Semesters = "";
        try{
            CachedRowSet result = db.select(new String[]{"Material"}, new String[]{"LectureNote, LabNote"}, new String[]{"Material.ModuleID = '" + moduleID + "'"});
            ArrayList<Map<String, Boolean>> material = new ArrayList<>();

            while (result.next()) {
                HashMap<String, Boolean> weekMaterial = new HashMap<>();
                weekMaterial.put("Lab", result.getBoolean("LabNote"));
                weekMaterial.put("Lecture", result.getBoolean("LabNote"));
                material.add(weekMaterial);
            }

            return material;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**Gets all the students in the lecturer's module alongisde their current scoring for the lecturer's module
     * @return List of maps with user fields and their values (eg: forename, "john"), including mark fields and values
     */
    public List<Map<String, String>> getEnrolledStudents() throws SQLException {
        String moduleID = getCurrentLecturer().getModule().getModuleID();
        List<Map<String, String>> enrolled = new ArrayList<>();
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Student, Curriculum"}, new String[]{"UserID"},new String[]{"Student.CourseID = Curriculum.CourseID AND Student.yearOfStudy = Curriculum.Year AND Curriculum.ModuleID = '" + moduleID + "'"});
            List<HashMap<String, String>> users = new ArrayList<>();

            while (result.next()) {
                Student stu = new Student(result.getString("UserID"));
                HashMap<String, String> studentDetailsMap = new HashMap<>();
                studentDetailsMap.put("userID", stu.getUserID());
                studentDetailsMap.put("managerID", stu.getManager().getUserID());
                studentDetailsMap.put("forename", stu.getForename());
                studentDetailsMap.put("surname", stu.getSurname());
                studentDetailsMap.put("email", stu.getEmail());
                studentDetailsMap.put("DOB", stu.getDOB().toString());
                studentDetailsMap.put("gender", stu.getGender());
                studentDetailsMap.put("userType", stu.getType().toString());
                studentDetailsMap.put("activated", stu.getActivated() ? "ACTIVATED" : "DEACTIVATED");
                studentDetailsMap.put("courseID", stu.getCourseID());
                studentDetailsMap.put("YearOfStudy", Integer.toString(stu.getYearOfStudy()));
                studentDetailsMap.put("decision", stu.getDecision().toString());
                studentDetailsMap.put("labMark", Double.toString(stu.getMark(moduleID).getLabMark()));
                studentDetailsMap.put("examMark", Double.toString(stu.getMark(moduleID).getExamMark()));
                studentDetailsMap.put("attemptNo", Integer.toString(stu.getMark(moduleID).getAttemptNo()));

                enrolled.add(studentDetailsMap);
            }

            return enrolled;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    /**Updates the module material for a class
     * @param week - the week the material is for
     * @param semester - the semester the material is for
     * @param type - the type of the material (lab or lecture)
     * @param file - file to be uploaded
     */
    public void updateModuleMaterial(int week, int semester, String type, File file) {
        Map<String, String> values = new HashMap<>();
        // TODO Dont know how to upload file in java to sql :(
        if (Objects.equals(type, "Lecture")){
            values.put("LectureNote", String.valueOf(file));
        }
        else {
            values.put("LabNote", String.valueOf(file));
        }
        if (semester > 1){
            week+= 12;
        }

        try {
            DatabaseConnection db = App.getDatabaseConnection();
            int res = db.update("Material", values, new String[]{"Week = '"+ week +"' AND ModuleID = '"+ getCurrentLecturer().getModule().getModuleID() +"'"});
            if (res > 0) return;
        } catch (SQLException e) {
            System.out.println("Failed to update module material for with file " + String.valueOf(file) + e.getMessage());
            return;
        }

    }

    /**Updates the student lab mark
     * @param studentID - the student ID for the student being updated
     * @param attNo - the attempt number wanting to be updated
     * @param mark - the new lab mark for that student's attempt
     */
    public void updateStudentLabMark(String studentID, int attNo, Double mark){
        Map<String, String> values = new HashMap<>();
        values.put("Lab", String.valueOf(mark));

        try {
            DatabaseConnection db = App.getDatabaseConnection();
            int res = db.update("Mark", values, new String[]{"AttNo = '"+ attNo +"' AND UserID = '"+ studentID +"'"});
        } catch (SQLException e) {
            System.out.println("Failed to update lab mark for student " + studentID + " (attempt #" + attNo + ")." + e.getMessage());
            return;
        }
    }


    /**Updates the student exam mark
     * @param studentID - the student ID for the student being updated
     *      * @param attNo - the attempt number wanting to be updated
     *      * @param mark - the new exam mark for that student's attempt
     */
    public void updateStudentExamMark(String studentID, int attNo, Double mark){
        Map<String, String> values = new HashMap<>();
        values.put("Exam", String.valueOf(mark));

        try {
            DatabaseConnection db = App.getDatabaseConnection();
            int res = db.update("Mark", values, new String[]{"AttNo = '"+ attNo +"' AND UserID = '"+ studentID +"'"});
        } catch (SQLException e) {
            System.out.println("Failed to update exam mark for student " + studentID + " (attempt #" + attNo + ")." + e.getMessage());
            return;
        }
    }

    /**Edits a module
     * @param code
     * @param name
     * @param credit
     */

    //note - same method is present in managerController - should this be moved to a shared controller
    //where both can use it?
    public void editModule(String oldCode, String code, String name, String description, String credit){
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("ModuleID", db.sqlString(code));
        values.put("Name", db.sqlString(name));
        values.put("Description", db.sqlString(description));
        values.put("Credit", String.valueOf(credit));

        try {
            db.update("Module", values, new String[]{"ModuleID = " + db.sqlString(oldCode)});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
