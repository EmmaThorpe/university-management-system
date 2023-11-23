package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Module {
    private final String moduleID;
    private String name;
    private String description;
    private int credit;

    /**
     * Creates a new Module object from the database
     * @param moduleID The ID of the module to create
     * @throws SQLException If the module does not exist
     */
    public Module(String moduleID) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Module"}, null, new String[]{"ModuleID = " + db.sqlString(moduleID)});
        res.next();
        this.moduleID = res.getString("ModuleID");
        this.name = res.getString("Name");
        this.description = res.getString("Description");
        this.credit = res.getInt("Credit");
    }

    /**
     * Creates a new Module object from the given parameters without checking the database
     */
    public Module(String moduleID, String name, String description, int credit) {
        this.moduleID = moduleID;
        this.name = name;
        this.description = description;
        this.credit = credit;
    }

    public String getModuleID() { return moduleID; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public int getCredit() { return credit; }

    /**
     * Sets the name of the module
     * @return Whether the operation was successful
     */
    public boolean setName(String name) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("Name", db.sqlString(name));
        try {
            db.update("Module", values, new String[]{"ModuleID = " + moduleID});
            this.name = name;
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to change description of course " + moduleID + "!");
            return false;
        }
    }

    /**
     * Sets the description of the module
     * @return Whether the operation was successful
     */
    public boolean setDescription(String description) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("Name", db.sqlString(description));
        try {
            db.update("Module", values, new String[]{"ModuleID = " + moduleID});
            this.description = description;
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to change description of course " + moduleID + "!");
            return false;
        }
    }

    /**
     * Sets the credit of the module
     * @return Whether the operation was successful
     */
    public boolean setCredit(int credit) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("Name", db.sqlString(String.valueOf(credit)));
        try {
            db.update("Module", values, new String[]{"ModuleID = " + moduleID});
            this.credit = credit;
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to change credit of course " + moduleID + "!");
            return false;
        }
    }

    /**
     * Gets the material of the module for a given semester and week
     */
    public Material getMaterial(int semester, int week) { return new Material(moduleID, semester, week); }

    /**
     * Gets the students of a module in a given timeframe
     * @return The result of the query
     * @throws SQLException If the query fails
     */
    public List<Student> getStudents(boolean sem1, boolean sem2, int year) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        String semester1 = (sem1) ? "1" : "0";
        String semester2 = (sem2) ? "1" : "0";
        try {
            CachedRowSet result = db.select(new String[]{"Curriculum", "Student"},
                                            new String[]{"DISTINCT Student.UserID"},
                                            new String[]{"Student.CourseID = Curriculum.CourseID",
                                                         "Student.YearOfStudy = Curriculum.Year",
                                                         "Curriculum.Semester1 = " + semester1,
                                                         "Curriculum.Semester2 = " + semester2,
                                                         "Curriculum.Year = " + year,
                                                         "Curriculum.ModuleID = " + db.sqlString(moduleID)});
            List<Student> studentList = new ArrayList<>();

            while(result.next()){
                String userID = result.getString("UserID");
                Student s = new Student(userID);
                studentList.add(s);
            }
            return studentList;

        } catch (SQLException e) {
            System.out.println("Failed to query students.");
            throw new SQLException(e.getMessage() + " - " + moduleID + "'s getStudents failed");
        }
    }

    /**
     * Gets the lecturers of the module
     * @return The result of the query
     * @throws SQLException If the query fails
     */
    public  List<Lecturer> getLecturers() throws SQLException {
            DatabaseConnection db= App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Module", "Lecturer"},
                    new String[]{"Lecturer.UserID"},
                    new String[]{"Lecturer.ModuleID = Module.ModuleID",
                                 "Module.ModuleID = " + db.sqlString(moduleID)});
            List<Lecturer> lecturerList = new ArrayList<>();
            while(result.next()){
                String userID = result.getString("UserID");
                Lecturer l = new Lecturer(userID);
                lecturerList.add(l);
            }
            return lecturerList;

        } catch (SQLException e) {
            System.out.println("Failed to query students.");
            throw new SQLException(e.getMessage() + " - " + moduleID + "'s getLecturers failed");
        }
    }
}
