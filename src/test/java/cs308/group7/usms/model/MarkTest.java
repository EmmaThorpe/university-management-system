package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MarkTest {

    Mark mark;

    Mark mark2;

    Mark failMark;

    @BeforeEach
    public void setUp() throws SQLException {
        // Creates new mark object without checking database
        mark = new Mark("stu1", "CS104", 99, 75.0, 82.5);
        mark.setLabMark(75.0);
        mark.setExamMark(82.5);

        // Creates new mark object using db connection
        mark2 = new Mark("stu3", "ML107", 98);
        mark2.setLabMark(62.0);
        mark2.setExamMark(50.0);

        failMark = new Mark ("Sample", "CS106", 1, 20.0, 10.0);

    }

    @Test
    public void testGetModuleID(){
        assertEquals("CS104", mark.getModuleID());
        assertEquals("ML107", mark2.getModuleID());
    }

    @Test
    public void testGetUserID(){
        assertEquals("stu1", mark.getUserID());
        assertEquals("stu3", mark2.getUserID());
    }

    @Test
    public void testGetAttemptNo(){
        assertEquals(99, mark.getAttemptNo());
        assertEquals(98, mark2.getAttemptNo());
    }

    @Test
    public void testGetLabMark() {
        assertEquals(75.0, mark.getLabMark());
        assertEquals(62.0, mark2.getLabMark());
    }

    @Test
    public void testGetExamMark() {
        assertEquals(82.5, mark.getExamMark());
        assertEquals(50.0, mark2.getExamMark());
    }

    @Test
    public void testSetLabMark() {
        assertTrue(mark.setLabMark(90.0));
        assertEquals(90.0, mark.getLabMark());
        assertTrue(mark2.setLabMark(20.0));
        assertEquals(20.0, mark2.getLabMark());
    }

    @Test
    public void testSetExamMark() {
        assertTrue(mark.setExamMark(88.0));
        assertEquals(88.0, mark.getExamMark());
        assertTrue(mark2.setExamMark(45.0));
        assertEquals(45.0, mark2.getExamMark());
    }

    @Test
    public void testPasses() {
        // Test if the mark passes the module, has fail mark to test both cases
        assertTrue(mark.passes());
        assertTrue(mark2.passes());
        assertFalse(failMark.passes());
    }

    @Test
    public void testCanBeCompensated() {
        mark.setLabMark(45.0);
        mark.setExamMark(42.0);
        assertTrue(mark.canBeCompensated());

        assertFalse(failMark.canBeCompensated());
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        DatabaseConnection db = App.getDatabaseConnection();
        db.delete("BusinessRuleApplication", new String[]{"UserID = 'stu1'", "ModuleID = 'CS104'", "AttNo = 99"});
        db.delete("Mark", new String[]{"UserID = 'stu1'", "ModuleID = 'CS104'", "AttNo = 99"});
        db.delete("BusinessRuleApplication", new String[]{"UserID = 'stu3'", "ModuleID = 'ML107'", "AttNo = 98"});
        db.delete("Mark", new String[]{"UserID = 'stu3'", "ModuleID = 'ML107'", "AttNo = 98"});
    }

}