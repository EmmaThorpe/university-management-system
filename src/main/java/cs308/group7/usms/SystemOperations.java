package cs308.group7.usms;

/**
 * Contains the methods that the system will use to interact with the database.
 */
public class SystemOperations {

    private final DatabaseConnection conn;

    public SystemOperations(DatabaseConnection conn) {
        this.conn = conn;
    }

}
