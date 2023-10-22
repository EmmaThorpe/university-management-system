package cs308.group7.usms.database;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Contains the simple methods that will be used to interact directly with the database. (UNIMPLEMENTED)
 */
public class DatabaseConnection {

    public DatabaseConnection(String parameterFilePath) {

    }

    /**
     * Performs a SELECT query on the database
     * @param tables The tables to select from
     * @param columns The columns to select from the table (null/[] for all)
     * @param whereConditions The conditions to filter the query by (null/[] for none)
     * @return The result of the query
     * @throws IllegalArgumentException If no tables are specified
     */
    public ResultSet select(@NotNull String[] tables, String[] columns, String[] whereConditions) throws SQLException, IllegalArgumentException {
        return null;
    }

    /**
     * Performs an INSERT query on the database
     * @param table The table to insert into
     * @param columnValueMap The map of column names to values to insert
     * @return The result of the query
     * @throws SQLException If the query fails / times out
     */
    public ResultSet insert(@NotNull String table, @NotNull Map<String, Object> columnValueMap) throws SQLException {
        return null;
    }

    /**
     * Performs an UPDATE query on the database
     * @param table The table to update
     * @param columnValueMap The map of column names to values to update
     * @param whereConditions The conditions to filter the query by (null/[] for none)
     * @return The result of the query
     * @throws SQLException If the query fails / times out
     */
    public ResultSet update(@NotNull String table, @NotNull Map<String, Object> columnValueMap, String[] whereConditions) throws SQLException {
        return null;
    }

    /**
     * Performs a DELETE query on the database
     * @param table The table to delete from
     * @param whereConditions The conditions to filter the query by (null/[] for none)
     * @return The result of the query
     * @throws SQLException If the query fails / times out
     */
    public ResultSet delete(@NotNull String table, String[] whereConditions) throws SQLException {
        return null;
    }

    /**
     * Performs a query on the database
     * @param query The query to perform
     * @return The result of the query
     * @throws SQLException If the query fails / times out
     */
    public ResultSet query(@NotNull String query) throws SQLException {
        return null;
    }

}