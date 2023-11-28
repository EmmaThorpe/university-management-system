package cs308.group7.usms.controller;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import cs308.group7.usms.model.Material;
import cs308.group7.usms.model.Module;
import cs308.group7.usms.model.User;
import cs308.group7.usms.ui.MainUI;
import cs308.group7.usms.ui.StudentUI;
import cs308.group7.usms.utils.Password;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

public abstract class BaseController {

    // Passwords

    public static void changePassword(MainUI uiParent, String userID, String oldPassword, String newPassword) {
        try {
            final User u = new User(userID);
            final boolean AUTHORISED = Password.matches(oldPassword, u.getEncryptedPassword());
            if (AUTHORISED) {
                final boolean success = u.changePassword(newPassword);
                if (success) uiParent.makeNotificationModal(null, "Successfully changed your password!", true);
                else throw new SQLException();
            } else {
                uiParent.makeNotificationModal(null, "Couldn't change your password! Old password provided is incorrect.", false);
            }
        } catch (SQLException e) {
            uiParent.makeNotificationModal(null, "Failed to change your password! A database error occurred.", false);
        }
    }

    // Modules

    /**
     * Gets whether a given module spans both semesters for a given course.
     */
    public static boolean spansTwoSems(String courseID, String moduleID) throws SQLException {
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
    }

    /**
     * Updates information about a module.
     * @param oldCode The old module code
     * @param code The new module code
     * @param name The new module name
     * @param credit The new module credit
     */
    public void editModule(String oldCode, String code, String name, String description, int credit) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("ModuleID", db.sqlString(code));
        values.put("Name", db.sqlString(name));
        values.put("Description", db.sqlString(description));
        values.put("Credit", String.valueOf(credit));
        db.update("Module", values, new String[]{"ModuleID = " + db.sqlString(oldCode)});
    }

    // Materials

    /**
     * Get the lecture note for a module, from a given week.
     * @return A file representing the lecture note
     * @throws SQLException If the lecture note does not exist
     */
    public File downloadLectureNote(String moduleID, int week) throws SQLException, IOException {
        Material m = new cs308.group7.usms.model.Module(moduleID).getMaterial(week);
        Optional<byte[]> lectureNote = m.getLectureNote();
        if (lectureNote.isEmpty()) return null;

        File f = new File(App.FILE_DIR + File.separator + "Material.pdf");
        try (OutputStream out = new FileOutputStream(f)) { out.write(lectureNote.get()); }
        return f;
    }

    /**
     * Get the lab note for a module, from a given week.
     * @return A file representing the lab note
     * @throws SQLException If the lab note does not exist
     */
    public File downloadLabNote(String moduleID, int week) throws SQLException, IOException {
        Material m = new Module(moduleID).getMaterial(week);
        Optional<byte[]> labNote = m.getLabNote();
        if (labNote.isEmpty()) return null;

        File f = new File(App.FILE_DIR + File.separator + "Material.pdf");
        try (OutputStream out = new FileOutputStream(f)) { out.write(labNote.get()); }
        return f;
    }

    /**
     * A list of maps representing whether lecture/lab materials exist for each week of a module.
     */
    public List<Map<String, Boolean>> getAllLectureMaterials(MainUI uiParent, String courseID, String moduleID) {
        DatabaseConnection db = App.getDatabaseConnection();
        List<Map<String, Boolean>> materials = new ArrayList<>();

        try {
            if (moduleID == null) throw new SQLException("Module ID cannot be null!");
            final boolean SPANS_TWO_SEMS = courseID == null || spansTwoSems(courseID, moduleID); // if courseID is null, let 24 weeks always display
            final int WEEKS = StudentUI.WEEKS_PER_SEM * (SPANS_TWO_SEMS ? 2 : 1);

            CachedRowSet res = db.executeQuery("SELECT Week, (CASE WHEN LectureNote IS NOT NULL THEN TRUE ELSE FALSE END) AS LectureNote, (CASE WHEN LabNote IS NOT NULL THEN TRUE ELSE FALSE END) AS LabNote FROM Material WHERE ModuleID = " + db.sqlString(moduleID) + " ORDER BY Week ASC");
            res.next();
            for (int i = 1; i <= WEEKS; i++) {
                int week;
                if (res.getRow() == 0) week = -1;
                else week = res.getInt("Week");
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
            uiParent.makeNotificationModal(null, "Couldn't get materials!: " + e.getMessage(), false);
            return Collections.emptyList();
        }
    }

}
