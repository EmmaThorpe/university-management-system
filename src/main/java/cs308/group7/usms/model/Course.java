package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Course {
    private final String courseID;
    private final String name;
    private String description;
    private final String levelOfStudy;
    private final int amountOfYears;

    /**
     * Creates a new Course object from the database
     * @param courseID The ID of the course to create
     * @throws SQLException If the course does not exist
     */
    public Course(String courseID) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        CachedRowSet res = db.select(new String[]{"Course"}, null, new String[]{"CourseID = " + db.sqlString(courseID)});
        res.next();
        this.courseID = res.getString("CourseID");
        this.name = res.getString("Name");
        this.description = res.getString("Description");
        this.levelOfStudy = res.getString("LevelOfStudy");
        this.amountOfYears = res.getInt("AmountOfYears");
    }

    /**
     * Creates a new Course object from the given parameters without checking the database
     */
    public Course(String courseID, String name, String description, String levelOfStudy, int amountOfYears) {
        this.courseID = courseID;
        this.name = name;
        this.description = description;
        this.levelOfStudy = levelOfStudy;
        this.amountOfYears = amountOfYears;
    }

    public String getCourseID() { return courseID; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getLevel() { return levelOfStudy; }

    public int getLength() { return amountOfYears; }

    /**
     * Sets the description of the course
     * @return Whether the operation was successful
     */
    public boolean setDescription(String description) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        values.put("Description", db.sqlString(description));
        try {
            db.update("Course", values, new String[]{"CourseID = " + courseID});
            this.description = description;
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to change description of course " + courseID + "!");
            return false;
        }
    }

    /**
     * Adds a module to the course curriculum
     * @return Whether the operation was successful
     */
    public boolean addModule(String moduleID, boolean sem1, boolean sem2, int year) {
        DatabaseConnection db = App.getDatabaseConnection();
        HashMap<String, String> values = new HashMap<>();
        String semester1 = (sem1) ? "1" : "0";
        String semester2 = (sem2) ? "1" : "0";

        values.put("CourseID", db.sqlString(courseID));
        values.put("ModuleID", db.sqlString(moduleID));
        values.put("Semester1", db.sqlString(semester1));
        values.put("Semester2", db.sqlString(semester2));
        values.put("Year", db.sqlString(String.valueOf(year)));
        try {
            db.insert("Curriculum", values);
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add module " + moduleID + " to the curriculum!");
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Gets the modules for the course within the curriculum timeframe specified
     * @return The result of the query
     * @throws SQLException If the query fails
     */
    public List<Module> getModules(boolean sem1, boolean sem2, int year) throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();

        List<String> conditionsList = new ArrayList<>();
        conditionsList.add("Module.ModuleID = Curriculum.ModuleID");
        conditionsList.add("Curriculum.CourseID = " + db.sqlString(courseID));
        conditionsList.add("Curriculum.Year = " + year);
        if (sem1) conditionsList.add("Curriculum.Semester1 = 1");
        if (sem2) conditionsList.add("Curriculum.Semester2 = 1");

        try {
            // https://stackoverflow.com/a/9572820/13460028
            CachedRowSet result = db.select(new String[]{"Curriculum", "Module"}, new String[]{"Module.ModuleID"}, conditionsList.toArray(new String[0]));

            List<Module> moduleList = new ArrayList<>();
            while(result.next()){
                String moduleID = result.getString("ModuleID");
                Module m = new Module(moduleID);
                moduleList.add(m);
            }

            return moduleList;
        } catch (SQLException e) {
            System.out.println("Failed to query modules.");
            throw new SQLException(e.getMessage() + " - " + courseID + "'s getModules failed");
        }
    }

    /**
     * Gets all the modules for the course
     * @return The result of the query
     */
    public List<Module> getModules() throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Curriculum", "Module"},
                                            new String[]{"DISTINCT Module.ModuleID"},
                                            new String[]{"Module.ModuleID = Curriculum.ModuleID",
                                                         "Curriculum.CourseID = " + db.sqlString(courseID)});

            List<Module> moduleList = new ArrayList<>();
            while(result.next()){
                String moduleID = result.getString("ModuleID");
                Module m = new Module(moduleID);
                moduleList.add(m);
            }

            return moduleList;
        } catch (SQLException e) {
            System.out.println("Failed to query modules.");
            throw new SQLException(e.getMessage() + " - " + courseID + "'s getModules failed");
        }
    }

    /**
     * Gets all the students for the course
     * @return The result of the query
     * @throws SQLException if the query fails
     */
    public List<Student> getStudents() throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Curriculum", "Student"},
                                            new String[]{"DISTINCT Student.UserID"},
                                            new String[]{"Curriculum.CourseID = Student.CourseID",
                                                         "Curriculum.CourseID = " + db.sqlString(courseID)});
            List<Student> studentList = new ArrayList<>();

            while(result.next()){
                String userID = result.getString("UserID");
                Student s = new Student(userID);
                studentList.add(s);
            }

            return studentList;

        } catch (SQLException e) {
            System.out.println("Failed to query students.");
            throw new SQLException(e.getMessage() + " - " + courseID + "'s getStudents failed");
        }
    }
}
