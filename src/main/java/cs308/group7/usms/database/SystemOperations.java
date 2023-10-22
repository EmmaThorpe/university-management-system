package cs308.group7.usms.database;

import cs308.group7.usms.database.DatabaseConnection;

/**
 * Contains the methods that the system will use to interact with the database.
 */
public class SystemOperations {

    private final DatabaseConnection conn;

    public SystemOperations(DatabaseConnection conn) {
        this.conn = conn;
    }

}
