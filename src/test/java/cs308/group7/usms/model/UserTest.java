package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.sql.rowset.CachedRowSet;

import java.sql.Date;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;

public class UserTest {


    private DatabaseConnection db;

    private CachedRowSet res;

    private final String SampleUserID = "stu1";
    private final String SampleManagedBy = "mng1";
    private final String SampleForename = "Fred";
    private final String SampleSurname = "Figglehorn";
    private final String SampleEmail = "fred.figglehorn.2021@uni.strath.ac.uk";
    private final Date SampleDOB = java.sql.Date.valueOf("2002-12-18");
    private final String SampleGender = "Male";
    private final String SampleType = "Student";
    private final User.UserType SampleTypeValue = User.UserType.STUDENT;
    private final boolean SampleActivated = true;

    @BeforeEach
    public void setup() throws SQLException {
        db = Mockito.mock(DatabaseConnection.class);
        res = Mockito.mock(CachedRowSet.class);

        Mockito.when(db.select(any(), any(), any())).thenReturn(res);
        Mockito.when(res.next()).thenReturn(true);
    }

    private void setupMockUserResponse(String UserID, String ManagedBy, String Forename, String Surname, String Email, Date DOB, String Gender, String Type, boolean Activated) throws SQLException {
        Mockito.when(res.getString("UserID")).thenReturn(UserID);
        Mockito.when(res.getString("ManagedBy")).thenReturn(ManagedBy);
        Mockito.when(res.getString("Forename")).thenReturn(Forename);
        Mockito.when(res.getString("Surname")).thenReturn(Surname);
        Mockito.when(res.getString("Email")).thenReturn(Email);
        Mockito.when(res.getDate("DOB")).thenReturn(DOB);
        Mockito.when(res.getString("Gender")).thenReturn(Gender);
        Mockito.when(res.getString("Type")).thenReturn(Type);
        Mockito.when(res.getBoolean("Activated")).thenReturn(Activated);
    }

    @Test
    public void test_student() {
        final User.UserType Type = User.UserType.STUDENT;

        User user = new User(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, Type, SampleActivated);

        Assertions.assertEquals(SampleUserID, user.getUserID());
        Assertions.assertEquals(SampleForename, user.getForename());
        Assertions.assertEquals(SampleSurname, user.getSurname());
        Assertions.assertEquals(SampleEmail, user.getEmail());
        Assertions.assertEquals(SampleDOB, user.getDOB());
        Assertions.assertEquals(SampleGender, user.getGender());
        Assertions.assertEquals(SampleActivated, user.getActivated());

        Assertions.assertEquals(User.UserType.STUDENT, user.getType());
        Assertions.assertFalse(user.isManager());
    }

    @Test
    public void test_student_database_construction() throws SQLException {

        setupMockUserResponse(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, SampleType, SampleActivated);

        // Mock the App class to return the mock database connection & cached row set
        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            User user = new User(SampleUserID);

            Assertions.assertEquals(SampleUserID, user.getUserID());
            Assertions.assertEquals(SampleForename, user.getForename());
            Assertions.assertEquals(SampleSurname, user.getSurname());
            Assertions.assertEquals(SampleEmail, user.getEmail());
            Assertions.assertEquals(SampleDOB, user.getDOB());
            Assertions.assertEquals(SampleGender, user.getGender());
            Assertions.assertEquals(SampleActivated, user.getActivated());

            Assertions.assertEquals(SampleTypeValue, user.getType());
            Assertions.assertFalse(user.isManager());
        }
    }

    @Test
    public void test_lecturer() {
        final User.UserType Type = User.UserType.LECTURER;

        User user = new User(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, Type, SampleActivated);
        Assertions.assertEquals(User.UserType.LECTURER, user.getType());
        Assertions.assertFalse(user.isManager());
    }

    @Test
    public void test_lecturer_database_construction() throws SQLException {
        final String Type = "Lecturer";

        setupMockUserResponse(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, Type, SampleActivated);

        // Mock the App class to return the mock database connection & cached row set
        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            User user = new User(SampleUserID);
            Assertions.assertEquals(User.UserType.LECTURER, user.getType());
            Assertions.assertFalse(user.isManager());
        }
    }

    @Test
    public void test_manager() {
        final User.UserType TypeValue = User.UserType.MANAGER;

        User user = new User(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, TypeValue, SampleActivated);
        Assertions.assertEquals(User.UserType.MANAGER, user.getType());
        Assertions.assertTrue(user.isManager());
    }

    @Test
    public void test_manager_database_construction() throws SQLException {
        final String Type = "Manager";

        setupMockUserResponse(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, Type, SampleActivated);

        // Mock the App class to return the mock database connection & cached row set
        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            User user = new User(SampleUserID);
            Assertions.assertEquals(User.UserType.MANAGER, user.getType());
            Assertions.assertTrue(user.isManager());
        }
    }

    @Test
    public void test_unexpected_user_type_in_db() throws SQLException {
        final String Type = "Unexpected";

        setupMockUserResponse(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, Type, SampleActivated);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Assertions.assertThrows(SQLException.class, () -> new User(SampleUserID));
        }
    }

    @Test
    public void test_unknown_user() throws SQLException {
        Mockito.when(res.next()).thenReturn(false);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Assertions.assertThrows(SQLException.class, () -> new User(SampleUserID));
        }
    }

    @Test
    public void test_getManager() throws SQLException {
        final String Manager1 = "mng1";
        final String Manager2 = "mng2";

        setupMockUserResponse(SampleUserID, Manager1, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, SampleType, SampleActivated);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            User user = new User(SampleUserID);

            setupMockUserResponse(Manager1, Manager2, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, SampleType, SampleActivated);
            User manager = user.getManager();
            Assertions.assertEquals(Manager1, manager.getUserID());
        }
    }

    @Test
    public void test_activated() throws SQLException {
        final boolean Activated = false;

        setupMockUserResponse(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, SampleType, Activated);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            User user = new User(SampleUserID);
            Assertions.assertFalse(user.getActivated());

            // setActivated
            Mockito.when(db.update(any(), any(), any())).thenReturn(1);
            Assertions.assertTrue(user.setActivated());
            Mockito.verify(db, Mockito.times(1)).update(any(), any(), any());
            Assertions.assertTrue(user.getActivated());
        }
    }

    @Test
    public void test_db_fail() throws SQLException {
        setupMockUserResponse(SampleUserID, SampleManagedBy, SampleForename, SampleSurname, SampleEmail, SampleDOB, SampleGender, SampleType, SampleActivated);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            User user = new User(SampleUserID);

            // setActivated
            Mockito.when(db.update(any(), any(), any())).thenThrow(SQLException.class);
            Assertions.assertFalse(user.setActivated());
            Mockito.verify(db, Mockito.times(1)).update(any(), any(), any());
        }
    }

    // TODO: test passwords

}
