package cs308.group7.usms.model;

import cs308.group7.usms.App;
import cs308.group7.usms.database.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.sql.rowset.CachedRowSet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class MaterialTest {

    private DatabaseConnection db;
    private Connection conn;
    private CachedRowSet res;

    private static final String testFilePath = App.FILE_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test.pdf";

    private static final String testModuleID = "CS308";
    private static final int testWeek = 1;
    private static final byte[] testBytes = new byte[]{1, 2, 3};

    private static void createTestFile(byte[] bytes) throws IOException {
        File testFile = new File(testFilePath);
        testFile.getParentFile().mkdirs();
        testFile.createNewFile();
        java.io.FileOutputStream fos = new java.io.FileOutputStream(testFile);
        fos.write(bytes);
        fos.close();
    }

    private static void deleteTestFile() {
        File testFile = new File(testFilePath);
        testFile.delete();
    }

    @BeforeAll
    public static void setupAll() throws IOException { createTestFile(testBytes); }

    @AfterAll
    public static void teardownAll() { deleteTestFile(); }

    @BeforeEach
    public void setup() throws SQLException {
        db = Mockito.mock(DatabaseConnection.class);
        conn = Mockito.mock(Connection.class);
        res = Mockito.mock(CachedRowSet.class);

        Mockito.when(db.select(any(), any(), any())).thenReturn(res);
        Mockito.when(db.sqlString(anyString())).thenCallRealMethod();
        Mockito.when(db.getConnection()).thenReturn(conn);
        Mockito.when(res.next()).thenReturn(true);
    }

    @Test
    public void test_material_constructor() {
        Material material = new Material(testModuleID, testWeek);
        Assertions.assertEquals(testModuleID, material.getModuleID());
        Assertions.assertEquals(testWeek, material.getWeek());
    }

    @Test
    public void test_get_notes() throws SQLException {

        Material material = new Material(testModuleID, testWeek);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Mockito.when(res.getBytes("LabNote")).thenReturn(testBytes);
            Mockito.when(res.getBytes("LectureNote")).thenReturn(testBytes);

            final Optional<byte[]> labNote = material.getLabNote();
            Assertions.assertTrue(labNote.isPresent());
            Assertions.assertArrayEquals(testBytes, labNote.get());

            final Optional<byte[]> lectureNote = material.getLectureNote();
            Assertions.assertTrue(lectureNote.isPresent());
            Assertions.assertArrayEquals(testBytes, lectureNote.get());
        }
    }

    @Test
    public void test_get_empty_notes() throws SQLException {
        Material material = new Material(testModuleID, testWeek);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Mockito.when(res.getBytes("LabNote")).thenReturn(null);
            Mockito.when(res.getBytes("LectureNote")).thenReturn(null);

            final Optional<byte[]> labNote = material.getLabNote();
            Assertions.assertFalse(labNote.isPresent());

            final Optional<byte[]> lectureNote = material.getLectureNote();
            Assertions.assertFalse(lectureNote.isPresent());
        }
    }

    @Test
    public void test_set_notes() throws SQLException {
        Material material = new Material(testModuleID, testWeek);

        PreparedStatement pstmt = Mockito.mock(PreparedStatement.class);
        Mockito.when(conn.prepareStatement(any())).thenReturn(pstmt);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Mockito.when(res.next()).thenReturn(false); // Row doesn't exist

            material.setLabNote(new File(testFilePath));
            material.setLectureNote(new File(testFilePath));

            // Inserts (since the row doesn't exist)
            Mockito.verify(db, Mockito.times(2)).insert(eq("Material"), any());

            // Updates
            Mockito.verify(conn, Mockito.times(1)).prepareStatement(Mockito.contains("LabNote = ?"));
            Mockito.verify(conn, Mockito.times(1)).prepareStatement(Mockito.contains("LectureNote = ?"));
            Mockito.verify(pstmt, Mockito.times(2)).setBytes(anyInt(), eq(testBytes));
            Mockito.verify(pstmt, Mockito.times(2)).setString(anyInt(), eq(testModuleID));
            Mockito.verify(pstmt, Mockito.times(2)).setInt(anyInt(), eq(testWeek));
        }

    }

    @Test
    public void test_db_fail() throws SQLException {
        Material material = new Material(testModuleID, testWeek);

        PreparedStatement pstmt = Mockito.mock(PreparedStatement.class);
        Mockito.when(conn.prepareStatement(any())).thenReturn(pstmt);

        try (MockedStatic<App> mockApp = Mockito.mockStatic(App.class)) {
            mockApp.when(App::getDatabaseConnection).thenReturn(db);

            Mockito.when(db.select(any(), any(), any())).thenThrow(new SQLException()); // Database error

            Assertions.assertFalse(material.getLabNote().isPresent());
            Assertions.assertFalse(material.getLectureNote().isPresent());
            Assertions.assertFalse(material.setLabNote(new File(testFilePath)));
            Assertions.assertFalse(material.setLectureNote(new File(testFilePath)));
        }
    }

}
