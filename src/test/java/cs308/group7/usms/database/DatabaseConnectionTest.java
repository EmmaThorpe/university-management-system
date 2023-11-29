package cs308.group7.usms.database;

import com.zaxxer.hikari.HikariDataSource;
import cs308.group7.usms.App;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;

public class DatabaseConnectionTest {

    private static DatabaseConnection db;
    private static Connection mockConnection;

    private static final String test_filename = "test_dbConnect.txt";
    private static final String test_bad_filename = "test_dbConnect_bad.txt";
    private static final String test_address = "localhost";
    private static final String test_database = "test-db";
    private static final String test_username = "testuser";
    private static final String test_password = "testpass";

    @BeforeAll
    public static void setup() throws IOException, IllegalAccessException, SQLException {
        // Create test_dbConnect.txt in the project root
        File f = new File(App.FILE_DIR + File.separator + test_filename);
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(test_address + "\n" + test_database + "\n" + test_username + "\n" + test_password);
        }

        // Create test_dbConnect_bad.txt in the project root
        File f2 = new File(App.FILE_DIR + File.separator + test_bad_filename);
        try (FileWriter fw = new FileWriter(f2)) {
            fw.write(test_address + "\n" + test_database + "\n" + test_username); // Missing password
        }

        // Create a new DatabaseConnection object with a mocked data source
        db = new DatabaseConnection(test_filename);
        HikariDataSource mockDataSource = Mockito.mock(HikariDataSource.class);
        mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Mock the internal HikariDataSource object to avoid attempting to connect to an actual database
        Field dataSource = ReflectionUtils
                .findFields(DatabaseConnection.class, field -> field.getName().equals("dataSource"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        dataSource.setAccessible(true);
        dataSource.set(db, mockDataSource);
    }

    @Test
    public void test_constructor() {
        // Test that the constructor does not throw an exception when the file exists and is formatted correctly
        Assertions.assertDoesNotThrow(() -> {
            try (DatabaseConnection ignored = new DatabaseConnection(test_filename)) {}
        });
    }

    @Test
    public void test_bad_file() {
        // Test that a FileNotFoundException is thrown when the file does not exist
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            try (DatabaseConnection ignored = new DatabaseConnection("file_that_is_not_present.txt")) {}
        });

        // Test that a FileNotFoundException is thrown when the file is empty / does not contain enough lines
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            try (DatabaseConnection ignored = new DatabaseConnection(test_bad_filename)) {}
        });
    }

    @Test
    public void test_select() throws SQLException {
        DatabaseConnection dbSpy = Mockito.spy(db);
        Mockito.doReturn(null).when(dbSpy).executeQuery(anyString());

        // Test that the select method calls executeQuery with the correct query
        // One table, all columns, no WHERE conditions
        dbSpy.select(new String[]{"Table1"}, null, null);
        Mockito.verify(dbSpy).executeQuery("SELECT * FROM Table1;");
        // One table, all columns, one WHERE condition
        dbSpy.select(new String[]{"Table1"}, null, new String[]{"Column1 = 1"});
        Mockito.verify(dbSpy).executeQuery("SELECT * FROM Table1 WHERE Column1 = 1;");
        // One table, all columns, multiple WHERE conditions
        dbSpy.select(new String[]{"Table1"}, null, new String[]{"Column1 = 1", "Column2 = 'Two'"});
        Mockito.verify(dbSpy).executeQuery("SELECT * FROM Table1 WHERE Column1 = 1 AND Column2 = 'Two';");
        // Several tables, specific columns, multiple WHERE conditions
        dbSpy.select(new String[]{"Table1", "Table2", "Table3"}, new String[]{"Column1", "Column2", "Column3"}, new String[]{"Column1 = 1", "Column2 = 'Two'"});
        Mockito.verify(dbSpy).executeQuery("SELECT Column1, Column2, Column3 FROM Table1, Table2, Table3 WHERE Column1 = 1 AND Column2 = 'Two';");

        // No tables
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbSpy.select(new String[]{}, null, null));

        // Database timeout
        Mockito.doThrow(new SQLException("Timeout")).when(dbSpy).executeQuery(anyString());
        Assertions.assertThrows(SQLException.class, () -> dbSpy.select(new String[]{"Table1"}, null, null));
    }

    // Helper method to join a set of strings with commas
    private String joinCol(Collection<String> c) {
        return String.join(", ", c);
    }

    @Test
    public void test_insert() throws SQLException {
        DatabaseConnection dbSpy = Mockito.spy(db);
        Mockito.doReturn(1).when(dbSpy).executeUpdate(anyString());

        Map<String, String> testValues = new HashMap<>();

        // Test that the insert method calls executeUpdate with the correct query
        // One column
        testValues.put("Column1", "1");
        dbSpy.insert("Table1", testValues);
        Mockito.verify(dbSpy).executeUpdate("INSERT INTO Table1 (Column1) VALUES (1);");
        // Multiple columns
        testValues.put("Column2", "'Two'");
        testValues.put("Column3", "3");
        dbSpy.insert("Table1", testValues);
        Mockito.verify(dbSpy).executeUpdate( // Use joinCol() to ensure the order of the columns is the same since HashMap does not guarantee order
                "INSERT INTO Table1 (" + joinCol(testValues.keySet()) + ") " +
                "VALUES (" + joinCol(testValues.values()) + ");");
        // No columns
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbSpy.insert("Table1", new HashMap<>()));

        // Database timeout
        Mockito.doThrow(new SQLException("Timeout")).when(dbSpy).executeUpdate(anyString());
        Assertions.assertThrows(SQLException.class, () -> dbSpy.insert("Table1", testValues));
    }

    // Helper method to join a map with commas and a splitter
    private String joinMapSplit(Map<String, String> m, String splitter) {
        StringBuilder sb = new StringBuilder();
        m.forEach((key, value) -> sb.append(key).append(splitter).append(value).append(", "));
        return sb.substring(0, sb.length() - 2); // Remove the last ", "
    }

    @Test
    public void test_update() throws SQLException {
        DatabaseConnection dbSpy = Mockito.spy(db);
        Mockito.doReturn(1).when(dbSpy).executeUpdate(anyString());

        Map<String, String> testValues = new HashMap<>();

        // Test that the update method calls executeUpdate with the correct query
        // One column, no WHERE conditions
        testValues.put("Column1", "1");
        dbSpy.update("Table1", testValues, null);
        Mockito.verify(dbSpy).executeUpdate("UPDATE Table1 SET Column1 = 1;");
        // One column, one WHERE condition
        dbSpy.update("Table1", testValues, new String[]{"Column1 = 1"});
        Mockito.verify(dbSpy).executeUpdate("UPDATE Table1 SET Column1 = 1 WHERE Column1 = 1;");
        // Multiple columns, no WHERE conditions
        testValues.put("Column2", "'Two'");
        testValues.put("Column3", "3");
        dbSpy.update("Table1", testValues, null);
        Mockito.verify(dbSpy).executeUpdate( // Use joinCol() to ensure the order of the columns is the same since HashMap does not guarantee order
                "UPDATE Table1 SET " + joinMapSplit(testValues, " = ") + ";"
        );
        // Multiple columns, multiple WHERE conditions
        dbSpy.update("Table1", testValues, new String[]{"Column1 = 1", "Column2 = 'Two'"});
        Mockito.verify(dbSpy).executeUpdate(
                "UPDATE Table1 SET " + joinMapSplit(testValues, " = ") +
                " WHERE Column1 = 1 AND Column2 = 'Two';");

        // No columns
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbSpy.update("Table1", new HashMap<>(), null));

        // Database timeout
        Mockito.doThrow(new SQLException("Timeout")).when(dbSpy).executeUpdate(anyString());
        Assertions.assertThrows(SQLException.class, () -> dbSpy.update("Table1", testValues, null));
    }

    @Test
    public void test_delete() throws SQLException {
        DatabaseConnection dbSpy = Mockito.spy(db);
        Mockito.doReturn(1).when(dbSpy).executeUpdate(anyString());

        // Test that the delete method calls executeUpdate with the correct query
        // No WHERE conditions
        dbSpy.delete("Table1", null);
        Mockito.verify(dbSpy).executeUpdate("DELETE FROM Table1;");
        // One WHERE condition
        dbSpy.delete("Table1", new String[]{"Column1 = 1"});
        Mockito.verify(dbSpy).executeUpdate("DELETE FROM Table1 WHERE Column1 = 1;");
        // Multiple WHERE conditions
        dbSpy.delete("Table1", new String[]{"Column1 = 1", "Column2 = 'Two'"});
        Mockito.verify(dbSpy).executeUpdate("DELETE FROM Table1 WHERE Column1 = 1 AND Column2 = 'Two';");

        // Database timeout
        Mockito.doThrow(new SQLException("Timeout")).when(dbSpy).executeUpdate(anyString());
        Assertions.assertThrows(SQLException.class, () -> dbSpy.delete("Table1", null));
    }

    @Test
    public void test_execute_query() throws SQLException {
        final String testQuery = "SELECT * FROM Table1;";
        PreparedStatement mockStatement = Mockito.mock(PreparedStatement.class);
        Mockito.doReturn(mockStatement).when(mockConnection).prepareStatement(anyString());

        // Test that the executeQuery method runs the correct query
        try { db.executeQuery(testQuery); }
        catch (SQLException e) { /* */ } // Ignore the exception, rowSet.populate() will fail since we are not mocking the result set

        Mockito.verify(mockConnection).prepareStatement(eq(testQuery));
        Mockito.verify(mockStatement).executeQuery();
    }

    @Test
    public void test_execute_update() throws SQLException {
        final String testStatement = "UPDATE Table1 SET Column1 = 1;";
        PreparedStatement mockStatement = Mockito.mock(PreparedStatement.class);
        Mockito.doReturn(mockStatement).when(mockConnection).prepareStatement(anyString());
        Mockito.doReturn(1).when(mockStatement).executeUpdate();

        // Test that the executeUpdate method runs the correct statement & returns the number of affected rows
        final int result = db.executeUpdate(testStatement);

        Mockito.verify(mockConnection).prepareStatement(eq(testStatement));
        Mockito.verify(mockStatement).executeUpdate();
        Assertions.assertEquals(1, result);
    }

    @Test
    public void test_connection() throws SQLException {
        // Test that the getConnection method returns the correct connection
        Assertions.assertEquals(mockConnection, db.getConnection());
    }

    @Test
    public void test_sqlString() {
        // Backslashes
        final String input1 = "t\\est\\ OR 1=1";
        final String expected1 = "'t\\\\est\\\\ OR 1=1'";
        Assertions.assertEquals(expected1, db.sqlString(input1));

        // Single quotes
        final String input2 = "te'st' OR 1=1";
        final String expected2 = "'te\\'st\\' OR 1=1'";
        Assertions.assertEquals(expected2, db.sqlString(input2));

        // Both
        final String input3 = "test'\\'\\ OR 1=1";
        final String expected3 = "'test\\'\\\\\\'\\\\ OR 1=1'";
        Assertions.assertEquals(expected3, db.sqlString(input3));
    }

    @AfterAll
    public static void cleanup() {
        // Delete the test files
        File f = new File(App.FILE_DIR + File.separator + test_filename);
        f.delete();
        File f2 = new File(App.FILE_DIR + File.separator + test_bad_filename);
        f2.delete();
    }

}
