package cs308.group7.usms.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Map;
import java.util.Scanner;

/**
 * Contains the simple methods that will be used to interact directly with the database. (<b>Assumes safe input.</b>)
 */
public class DatabaseConnection implements Closeable {

    private final HikariDataSource dataSource;

    /**
     * Creates a new DatabaseConnection object using the parameters in the given file
     * @param parameterFilePath The path to the file containing database parameters separated by newlines (address, database, username, password)
     * @throws FileNotFoundException If the file does not exist
     */
    public DatabaseConnection(String parameterFilePath) throws FileNotFoundException {
        File file = new File(parameterFilePath);
        Scanner sc = new Scanner(file);
        String address = sc.nextLine();
        String database = sc.nextLine();
        String username = sc.nextLine();
        String password = sc.nextLine();
        sc.close();

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://" + address + "/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

    /**
     * Performs a SELECT query on the database
     * @param tables The tables to select from
     * @param columns The columns to select from the table (null/[] for all)
     * @param whereConditions The conditions to filter the query by (null/[] for none)
     * @return The result of the query
     * @throws SQLException If the query fails / times out
     */
    public CachedRowSet select(@NotNull String[] tables, String[] columns, String[] whereConditions) throws SQLException {
        if (tables.length == 0) throw new IllegalArgumentException("At least one table must be selected!");

        final boolean SELECT_ALL = (columns == null) || (columns.length == 0); // Select all columns if none are specified
        final boolean NO_WHERE = (whereConditions == null) || (whereConditions.length == 0); // No WHERE conditions if none are specified

        final String columnsString = SELECT_ALL ? "*" : String.join(", ", columns);
        final String tablesString = String.join(", ", tables);
        final String whereString = (NO_WHERE) ? "" : " WHERE " + String.join(" AND ", whereConditions);

        return executeQuery(
            "SELECT " + columnsString +
            " FROM " + tablesString +
            whereString + ";"
        );
    }

    /**
     * Performs an INSERT statement on the database
     * @param table The table to insert into
     * @param columnValueMap The map of column names to values to insert
     * @return The number of affected rows
     * @throws SQLException If the query fails / times out
     */
    public int insert(@NotNull String table, @NotNull Map<String, String> columnValueMap) throws SQLException {
        final String columnsString = String.join(", ", columnValueMap.keySet());
        final String valuesString = String.join(", ", columnValueMap.values());

        return executeUpdate(
            "INSERT INTO " + table +
            " (" + columnsString + ")" +
            " VALUES (" + valuesString + ")" + ";"
        );
    }

    /**
     * Performs an UPDATE statement on the database
     * @param table The table to update
     * @param columnValueMap The map of column names to values to update
     * @param whereConditions The conditions to filter the query by (null/[] for none)
     * @return The number of affected rows
     * @throws SQLException If the query fails / times out
     */
    public int update(@NotNull String table, @NotNull Map<String, String> columnValueMap, String[] whereConditions) throws SQLException {
        final boolean NO_WHERE = (whereConditions == null) || (whereConditions.length == 0); // No WHERE conditions if none are specified

        StringBuilder updateString = new StringBuilder();
        for (Map.Entry<String, String> entry : columnValueMap.entrySet())
            updateString.append(entry.getKey()).append(" = ").append(entry.getValue()).append(", ");
        updateString.delete(updateString.length() - 2, updateString.length()); // Remove the last ", "

        final String whereString = (NO_WHERE) ? "" : " WHERE " + String.join(" AND ", whereConditions);

        return executeUpdate(
            "UPDATE " + table +
            " SET " + updateString +
            whereString + ";"
        );
    }

    /**
     * Performs a DELETE statement on the database
     * @param table The table to delete from
     * @param whereConditions The conditions to filter the query by (null/[] for none)
     * @return The number of affected rows
     * @throws SQLException If the query fails / times out
     */
    public int delete(@NotNull String table, String[] whereConditions) throws SQLException {
        final boolean NO_WHERE = (whereConditions == null) || (whereConditions.length == 0); // No WHERE conditions if none are specified

        final String whereString = (NO_WHERE) ? "" : " WHERE " + String.join(" AND ", whereConditions);

        return executeUpdate(
            "DELETE FROM " + table +
             whereString + ";"
        );
    }

    /**
     * Performs a query on the database
     * @param query The query to perform
     * @return The result of the query
     * @throws SQLException If the query fails / times out
     */
    public CachedRowSet executeQuery(@NotNull String query) throws SQLException {
        CachedRowSet crs;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(pstmt.executeQuery());
        }
        return crs;
    }

    /**
     * Performs an update on the database
     * @param statement The statement to perform
     * @return The number of affected rows
     * @throws SQLException If the update fails / times out
     */
    public int executeUpdate(@NotNull String statement) throws SQLException {
        int result;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(statement);
            result = pstmt.executeUpdate();
        }
        return result;
    }

    /**
     * Used to get a connection to the database for more complex updates.
     * @deprecated Not recommended for use unless absolutely necessary.
     */
    public Connection getConnection() throws SQLException { return dataSource.getConnection(); }

    public String sqlString(String s) {
        s = s.replaceAll("'", "\\\\'");
        return "'" + s + "'";
    }

    /**
     * Closes the database connection pool
     */
    @Override
    public void close() {
        dataSource.close();
    }
}