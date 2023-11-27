package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class Material {

    private final String moduleID;
    private final int week;

    /**
     * Creates a new Material object. If the material does not exist, updating it will create it.
     * @param moduleID The module's ID
     * @param week The week of the module
     */
    public Material(String moduleID, int week) {
        this.moduleID = moduleID;
        this.week = week;
    }

    public String getModuleID() { return moduleID; }

    public int getWeek() { return week; }

    /**
     * Returns an array of bytes representing the lecture PDF for this week of the module, or Optional.empty() if unset
     */
    public Optional<byte[]> getLectureNote() {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet res = db.select(
                    new String[]{"Material"},
                    new String[]{"LectureNote"},
                    new String[]{"ModuleID = " + db.sqlString(moduleID), "Week = " + week}
            );
            res.next();
            return Optional.ofNullable(res.getBytes("LectureNote"));
        } catch (SQLException e) {
            System.err.println("Failed to get lecture note for " + moduleID + " week " + week + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Sets the lecture note for this week of the module
     * @param lectureNote The new lecture note
     * @return Whether the lecture note was set successfully
     */
    public boolean setLectureNote (File lectureNote) {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet res = db.select(
                    new String[]{"Material"},
                    new String[]{"LectureNote"},
                    new String[]{"ModuleID = " + db.sqlString(moduleID), "Week = " + week}
            );

            final boolean exists = res.next();
            if (!exists) {
                Map<String, String> values = Map.of(
                        "ModuleID", db.sqlString(moduleID),
                        "Week", String.valueOf(week)
                );
                db.insert("Material", values);
            }

            try (FileInputStream fis = new FileInputStream(lectureNote)) {
                byte[] bytes = fis.readAllBytes();
                try (Connection conn = db.getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement("UPDATE Material SET LectureNote = ? WHERE ModuleID = ? AND Week = ?");
                    pstmt.setBytes(1, bytes);
                    pstmt.setString(2, moduleID);
                    pstmt.setInt(3, week);
                    pstmt.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Failed to set lecture note for " + moduleID + " week " + week + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns an array of bytes representing the lab PDF for this week of the module, Optional.empty() if unset
     */
    public Optional<byte[]> getLabNote() {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet res = db.select(
                    new String[]{"Material"},
                    new String[]{"LabNote"},
                    new String[]{"ModuleID = " + db.sqlString(moduleID), "Week = " + week}
            );
            res.next();
            return Optional.ofNullable(res.getBytes("LabNote"));
        } catch (SQLException e) {
            System.err.println("Failed to get lab note for " + moduleID + " week " + week + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Sets the lab note for this week of the module
     * @param labNote The new lab note
     * @return Whether the lab note was set successfully
     */
    public boolean setLabNote (File labNote) {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet res = db.select(
                    new String[]{"Material"},
                    new String[]{"LabNote"},
                    new String[]{"ModuleID = " + db.sqlString(moduleID), "Week = " + week}
            );

            final boolean exists = res.next();
            if (!exists) {
                Map<String, String> values = Map.of(
                        "ModuleID", db.sqlString(moduleID),
                        "Week", String.valueOf(week)
                );
                db.insert("Material", values);
            }

            try (FileInputStream fis = new FileInputStream(labNote)) {
                byte[] bytes = fis.readAllBytes();
                try (Connection conn = db.getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement("UPDATE Material SET LabNote = ? WHERE ModuleID = ? AND Week = ?");
                    pstmt.setBytes(1, bytes);
                    pstmt.setString(2, moduleID);
                    pstmt.setInt(3, week);
                    pstmt.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Failed to set lab note for " + moduleID + " week " + week + ": " + e.getMessage());
            return false;
        }
    }

}
