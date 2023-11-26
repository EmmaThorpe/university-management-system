package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.sql.rowset.CachedRowSet;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class CourseTest {
    private DatabaseConnection db;

    private CachedRowSet res;

    private final String SampleCourseID = "BScSE";
    private final String SampleName = "Software Engineering";
    private final String SampleDescription = "Examine the best ways to design, build, maintain and evaluate software systems.";
    private final String SampleLevelOfStudy = "Undergraduate";
    private final int SampleAmountOfYears = 5;
    private final String SampleDepartment = "Computing and Information Sciences";
    private final int SampleDeptNo = 1;

    @BeforeEach
    public void setup() throws SQLException {
        db = Mockito.mock(DatabaseConnection.class);
        res = Mockito.mock(CachedRowSet.class);

        Mockito.when(db.select(any(), any(), any())).thenReturn(res);
        Mockito.when(res.next()).thenReturn(true);
    }

    private void setupMockCourseResponse(String CourseID, String Name, String Description, String LevelOfStudy, int AmountOfYears, int DeptNo, String Department) throws SQLException {
        Mockito.when(res.getString("CourseID")).thenReturn(CourseID);
        Mockito.when(res.getString("Name")).thenReturn(Name, Department);
        Mockito.when(res.getString("Description")).thenReturn(Description);
        Mockito.when(res.getString("LevelOfStudy")).thenReturn(LevelOfStudy);
        Mockito.when(res.getInt("AmountOfYears")).thenReturn(AmountOfYears);
        Mockito.when(res.getInt("DeptNo")).thenReturn(DeptNo);
    }

    private void setupMockModuleResponse(String ModuleID, String Name, String Description, int Credit) throws SQLException {
        Mockito.when(res.getString("ModuleID")).thenReturn(ModuleID);
        Mockito.when(res.getString("Name")).thenReturn(Name);
        Mockito.when(res.getString("Description")).thenReturn(Description);
        Mockito.when(res.getInt("Credit")).thenReturn(Credit);
    }

    @Test
    public void test_course() {
        Course course = new Course(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDepartment);

        Assertions.assertEquals(SampleCourseID, course.getCourseID());
        Assertions.assertEquals(SampleName, course.getName());
        Assertions.assertEquals(SampleDescription, course.getDescription());
        Assertions.assertEquals(SampleLevelOfStudy, course.getLevel());
        Assertions.assertEquals(SampleAmountOfYears, course.getLength());
        Assertions.assertEquals(SampleDepartment, course.getDepartment());
    }

    @Test
    public void test_course_database_construction() throws SQLException {

        setupMockCourseResponse(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDeptNo, SampleDepartment);

        // Mock the App class to return the mock database connection & cached row set
        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Course course = new Course(SampleCourseID);

            Assertions.assertEquals(SampleCourseID, course.getCourseID());
            Assertions.assertEquals(SampleName, course.getName());
            Assertions.assertEquals(SampleDescription, course.getDescription());
            Assertions.assertEquals(SampleLevelOfStudy, course.getLevel());
            Assertions.assertEquals(SampleAmountOfYears, course.getLength());
            Assertions.assertEquals(SampleDepartment, course.getDepartment());
        }
    }

    @Test
    public void test_unknown_course() throws SQLException {
        Mockito.when(res.next()).thenReturn(false);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Assertions.assertThrows(SQLException.class, () -> new Course(SampleCourseID));
        }
    }

    @Test
    public void test_setDescription() throws SQLException {
        final String NewDescription = "It's when you engineer the software.";

        setupMockCourseResponse(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDeptNo, SampleDepartment);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Course course = new Course(SampleCourseID);
            Assertions.assertEquals(SampleDescription, course.getDescription());

            // setDescription
            Mockito.when(db.update(any(), any(), any())).thenReturn(1);
            Assertions.assertTrue(course.setDescription(NewDescription));
            Mockito.verify(db, Mockito.times(1)).update(any(), any(), any());
            Assertions.assertEquals(NewDescription, course.getDescription());
        }
    }

    @Test
    public void test_addModule() throws SQLException {
        final String ModuleID = "CS101";
        final boolean Sem1 = true;
        final boolean Sem2 = false;
        final int Year = 1;

        setupMockCourseResponse(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDeptNo, SampleDepartment);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Course course = new Course(SampleCourseID);

            // addModule
            Mockito.when(db.insert(any(), any())).thenReturn(1);
            boolean result = course.addModule(ModuleID, Sem1, Sem2, Year);
            Mockito.verify(db, Mockito.times(1)).insert(any(), any());
            Assertions.assertTrue(result);
        }
    }

    @Test
    public void test_getModules_with_year() throws SQLException {
        final int Year = 1;

        setupMockCourseResponse(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDeptNo, SampleDepartment);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Course course = new Course(SampleCourseID);

            final String ModuleID = "CS101";
            final String ModuleName = "Topics in Computing";
            final String ModuleDescription = "Four broad introductory topics to Computing Science.";
            final int ModuleCredit = 20;

            // getModules
            setupMockModuleResponse(ModuleID, ModuleName, ModuleDescription, ModuleCredit);
            Mockito.when(res.next()).thenReturn(true, true, false);
            List<Module> modules = course.getModules(Year);

            Assertions.assertNotNull(modules);
            for(Module m : modules){
                Assertions.assertEquals(m.getModuleID(), ModuleID);
            }
        }
    }

    @Test
    public void test_getModules_without_year() throws SQLException {
        setupMockCourseResponse(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDeptNo, SampleDepartment);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Course course = new Course(SampleCourseID);

            final String ModuleID = "CS101";
            final String ModuleName = "Topics in Computing";
            final String ModuleDescription = "Four broad introductory topics to Computing Science.";
            final int ModuleCredit = 20;

            // getModules
            setupMockModuleResponse(ModuleID, ModuleName, ModuleDescription, ModuleCredit);
            Mockito.when(res.next()).thenReturn(true, true, false);
            List<Module> modules = course.getModules();

            Assertions.assertNotNull(modules);
            for(Module m : modules){
                Assertions.assertEquals(m.getModuleID(), ModuleID);
            }
        }
    }

    @Test
    public void test_db_fail() throws SQLException {
        setupMockCourseResponse(SampleCourseID, SampleName, SampleDescription, SampleLevelOfStudy, SampleAmountOfYears, SampleDeptNo, SampleDepartment);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Course course = new Course(SampleCourseID);

            final String ModuleID = "CS101";
            final String ModuleName = "Topics in Computing";
            final String ModuleDescription = "Four broad introductory topics to Computing Science.";
            final int ModuleCredit = 20;

            // getModules
            setupMockModuleResponse(ModuleID, ModuleName, ModuleDescription, ModuleCredit);
            Mockito.when(db.select(any(), any(), any())).thenThrow(SQLException.class);
            Assertions.assertThrows(SQLException.class, course::getModules);

        }
    }
}
