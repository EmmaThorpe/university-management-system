package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import org.jetbrains.annotations.Nullable;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Material {

    private final String moduleID;
    private final int semester;
    private final int week;
    private String lectureNote = null;
    private String labNote = null;

    /**
     * Creates a new Material object from the database. If the material does not exist, updating it will create it.
     * @param moduleID The module's ID
     * @param semester The semester of the module
     * @param week The week of the module
     * @throws SQLException If the query fails
     */
    public Material(String moduleID, int semester, int week) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        try (CachedRowSet res = db.select(new String[]{"Material"}, null, new String[]{"ModuleID = '" + moduleID + "'", "Semester = " + semester, "Week = " + week})) {
            this.moduleID = moduleID;
            this.semester = semester;
            this.week = week;
            if (res.next()) {
                this.lectureNote = res.getString("LectureNote");
                this.labNote = res.getString("LabNote");
            }
        }
    }

    /**
     * Creates a new Material object from the given parameters without checking the database
     */
    public Material(String moduleID, int semester, int week, String lectureNote, String labNote) {
        this.moduleID = moduleID;
        this.semester = semester;
        this.week = week;
        this.lectureNote = lectureNote;
        this.labNote = labNote;
    }

    public Module getModule() throws SQLException { return new Module(moduleID); }

    public int getSemester() { return semester; }

    public int getWeek() { return week; }

    /**
     * Returns the lecture note for this week of the module, or null if unset
     */
    @Nullable
    public String getLectureNote() { return lectureNote; }

    /**
     * Sets the lecture note for this week of the module
     * @param lectureNote The new lecture note
     * @return Whether the lecture note was set successfully
     */
    public boolean setLectureNote (String lectureNote) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("LectureNote", "'" + lectureNote + "'"); // TODO: Need a better method, vulnerable to SQL injection
        try {
            if (db.update("Material", values, new String[]{"ModuleID = '" + moduleID + "'", "Semester = " + semester, "Week = " + week}) > 0) {
                this.lectureNote = lectureNote;
                return true;
            } else {
                values.put("ModuleID", "'" + moduleID + "'");
                values.put("Semester", String.valueOf(semester));
                values.put("Week", String.valueOf(week));
                if (db.insert("Material", values) > 0) {
                    this.lectureNote = lectureNote;
                    return true;
                } else {
                    throw new SQLException("Couldn't insert material entry!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to set lecture note for " + moduleID + " week " + week + " of semester " + semester + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the lab note for this week of the module, or null if unset
     */
    @Nullable
    public String getLabNote() { return labNote; }

    /**
     * Sets the lab note for this week of the module
     * @param labNote The new lab note
     * @return Whether the lab note was set successfully
     */
    public boolean setLabNote (String labNote) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("LabNote", "'" + labNote + "'"); // TODO: Need a better method, vulnerable to SQL injection
        try {
            if (db.update("Material", values, new String[]{"ModuleID = '" + moduleID + "'", "Semester = " + semester, "Week = " + week}) > 0) {
                this.labNote = labNote;
                return true;
            } else {
                values.put("ModuleID", "'" + moduleID + "'");
                values.put("Semester", String.valueOf(semester));
                values.put("Week", String.valueOf(week));
                if (db.insert("Material", values) > 0) {
                    this.labNote = labNote;
                    return true;
                } else {
                    throw new SQLException("Couldn't insert material entry!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to set lab note for " + moduleID + " week " + week + " of semester " + semester + ": " + e.getMessage());
            return false;
        }
    }

}
