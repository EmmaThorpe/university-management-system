package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.*;
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
                    lecUI.makeNotificationModal(null, "ERROR GETTING MODULE INFORMATION", false);
                    pageSetter("DASHBOARD", false);
                };

                break;
            case "GIVE MARK":
                try{
                    lecUI.mark(getEnrolledStudents());
                    buttons =lecUI.getCurrentButtons();
                    buttons.get("ASSIGN MARK").setOnAction(
                            (event)->updateStudentMark(
                                        lecUI.getValues().get("StudentID"),
                                        Integer.parseInt(lecUI.getValues().get("AttemptNo"))+1,
                                        Double.parseDouble(((TextField)lecUI.getCurrentFields().get("ASSIGN LAB MARK")).getText()),
                                        Double.parseDouble(((TextField)lecUI.getCurrentFields().get("ASSIGN EXAM MARK")).getText()),
                                        false
                            )
                    );
                    buttons.get("CHANGE MARK").setOnAction(
                            (event)->updateStudentMark(
                                    lecUI.getValues().get("StudentID"),
                                    Integer.parseInt(lecUI.getValues().get("AttemptNo")),
                                    Double.parseDouble(((TextField)lecUI.getCurrentFields().get("CHANGE LAB MARK")).getText()),
                                    Double.parseDouble(((TextField)lecUI.getCurrentFields().get("CHANGE EXAM MARK")).getText()),
                                    true
                            )
                    );


                }catch(java.sql.SQLException e){
                    lecUI.makeNotificationModal(null, "ERROR GETTING ENROLLED STUDENTS", false);
                    pageSetter("DASHBOARD", false);
                }

                break;

            case "MATERIALS":
                try{
                    Map<String, String> moduleInfo = getModuleInformation();
                    lecUI.materials(moduleInfo.get("Id"), getAllLectureMaterials(lecUI.getValues().get("ID")));
                    buttons = lecUI.getCurrentButtons();
                    buttons.get("VIEW LECTURE MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                    buttons.get("VIEW LAB MATERIAL").setOnAction(event -> pageSetter("OPEN PDF", false));
                    buttons.get("CHANGE LECTURE MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), Integer.parseInt(lecUI.getValues().get("SEMESTER")), "Lecture", lecUI.uploadFile()));
                    buttons.get("CHANGE LAB MATERIAL").setOnAction(event -> updateModuleMaterial(Integer.parseInt(lecUI.getValues().get("WEEK")), Integer.parseInt(lecUI.getValues().get("SEMESTER")), "Lab", lecUI.uploadFile()));

                }
                catch(java.sql.SQLException e){
                    lecUI.makeNotificationModal(null, "FAILED TO GET MATERIALS FROM SERVER", false);
                    pageSetter("DASHBOARD", false);
                }

                break;
            case "OPEN PDF":
                lecUI.displayPDF(null, "LECTURER NOTES");
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
       /* String Semesters = "";
        CachedRowSet result = db.select(new String[]{"Curriculum"}, new String[]{"Semester1, Semester2"}, new String[]{"ModuleID = " + db.sqlString(moduleID)});
        result.next();
        if (result.getBoolean("Semester1")) {
            Semesters += "1";
            if(result.getBoolean("Semester2")) {
                Semesters += "&2";
            }
        } else Semesters += "2";*/

        moduleInfo.put("Id", mod.getModuleID());
        moduleInfo.put("Name", mod.getName());
        moduleInfo.put("Description", mod.getDescription());
        moduleInfo.put("Credit", Integer.toString(mod.getCredit()));
        //moduleInfo.put("Semesters", Semesters );
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



    /**Gets all the students in the lecturer's module alongisde their current scoring for the lecturer's module
     * @return List of maps with user fields and their values (eg: forename, "john"), including mark fields and values
     */
    public List<Map<String, String>> getEnrolledStudents() throws SQLException {
        String moduleID = getCurrentLecturer().getModule().getModuleID();
        List<Map<String, String>> enrolled = new ArrayList<>();
        DatabaseConnection db = App.getDatabaseConnection();
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

                if(stu.getMark(moduleID).getLabMark() != null){
                    studentDetailsMap.put("labMark", Double.toString(stu.getMark(moduleID).getLabMark()));
                }else{
                    studentDetailsMap.put("labMark", null);
                }

                if(stu.getMark(moduleID).getExamMark() != null){
                    studentDetailsMap.put("examMark", Double.toString(stu.getMark(moduleID).getExamMark()));
                }else{
                    studentDetailsMap.put("examMark", null);
                }

                studentDetailsMap.put("attemptNo", Integer.toString(stu.getMark(moduleID).getAttemptNo()));

                enrolled.add(studentDetailsMap);
            }

            return enrolled;

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
            db.update("Material", values, new String[]{"Week = '"+ week +"' AND ModuleID = '"+ getCurrentLecturer().getModule().getModuleID() +"'"});


            lecUI.makeNotificationModal(null, "ADDED MATERIAL", true);
            pageSetter("MATERIAL", false);
        } catch (SQLException e) {
            lecUI.makeNotificationModal(null, "FAILED TO UPLOAD " + file + e.getMessage(), false);
        }

    }



    /**add the student mark
     * @param studentID - the student ID for the student being added
     * @param attNo - the attempt number wanting to be added
     * @param labMark - the new lab mark for that student's attempt
     * @param examMark - the new exam mark for that student's attempt
     * @param existing - value representing if the student mark being updated exists or not
     */
    public void updateStudentMark(String studentID, int attNo, Double labMark, Double examMark, boolean existing){

        try {
            Map<String, String> values = new HashMap<>();
            values.put("Lab", String.valueOf(labMark));
            values.put("Exam", String.valueOf(examMark));
            Lecturer lec = new Lecturer(lecturerID);
            Mark m = new Mark(studentID, lec.getModule().getModuleID(),attNo);
            m.setLabMark(labMark);
            m.setExamMark(examMark);

            if(existing){
                lecUI.makeNotificationModal("CHANGE MARK", "MARK CHANGED SUCCESSFULLY", true);
            }else{
                lecUI.makeNotificationModal("ASSIGN MARK", "MARK ADDED SUCCESSFULLY", true);

            }
            pageSetter("GIVE MARK", false);
        } catch (SQLException e) {
            lecUI.makeNotificationModal("ASSIGN MARK", "FAILED TO UPDATE MARK FOR STUDENT " + studentID + " (ATTEMPT #" + attNo + ")." + e.getMessage(), false);

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
            lecUI.makeNotificationModal("EDIT", "MODULE UPDATED SUCCESSFULLY", true);
            pageSetter("VIEW MODULE", false);
        } catch (SQLException e) {
            lecUI.makeNotificationModal("EDIT", "ERROR FAILED TO UPDATE MODULE" + e.getMessage(), false);
        }

    }


}
