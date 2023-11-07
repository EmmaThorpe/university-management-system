package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;

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
        CachedRowSet res = db.select(new String[]{"Course"}, null, new String[]{"CourseID = " + courseID});
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
     */
    public void getModules(boolean sem1, boolean sem2, int year) {
        DatabaseConnection db = App.getDatabaseConnection();
        String semester1 = (sem1) ? "1" : "0";
        String semester2 = (sem2) ? "1" : "0";
        try {
            CachedRowSet result = db.select(new String[]{"Curriculum", "Module"}, new String[]{"Module.ModuleID"},
                                            new String[]{"Module.ModuleID = Curriculum.ModuleID",
                                                         "Curriculum.CourseID = " + courseID,
                                                         "Curriculum.Semester1 = " + semester1,
                                                         "Curriculum.Semester2 = " + semester2,
                                                         "Curriculum.Year = " + year});

            while(result.next()){
                String moduleID = result.getString("ModuleID");
                System.out.println(moduleID);
            }

            return; //TODO: change return when Module is implemented
        } catch (SQLException e) {
            System.out.println("Failed to query modules.");
            return;
        }
    }

    /**
     * Gets all the modules for the course
     * @return The result of the query
     */
    public void getModules() {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Curriculum", "Module"},
                                            new String[]{"Module.ModuleID"},
                                            new String[]{"Module.ModuleID = Curriculum.ModuleID",
                                                         "Curriculum.CourseID = " + courseID});

            while(result.next()){
                String moduleID = result.getString("ModuleID");
                System.out.println(moduleID);
            }

            return; //TODO: change return when Module is implemented
        } catch (SQLException e) {
            System.out.println("Failed to query modules.");
            return;
        }
    }

    /**
     * Gets all the students for the course
     * @return The result of the query
     */
    public Student[] getStudents() {
        DatabaseConnection db = App.getDatabaseConnection();
        try {
            CachedRowSet result = db.select(new String[]{"Curriculum", "Student"},
                                            new String[]{"DISTINCT Student.UserID"},
                                            new String[]{"Curriculum.CourseID = Student.CourseID",
                                                         "Curriculum.CourseID = " + db.sqlString(courseID)});
            Student[] studentList = new Student[result.size()];
            int i = 0;

            while(result.next()){
                String userID = result.getString("UserID");
                Student s = new Student(userID);
                studentList[i] = s;
                i++;
            }

            return studentList;

        } catch (SQLException e) {
            System.out.println("Failed to query students.");
            System.out.println(e.getMessage());
            return new Student[0];
        }
    }
}
