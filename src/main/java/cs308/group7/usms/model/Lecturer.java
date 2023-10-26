package cs308.group7.usms.model;

import java.sql.SQLException;

public class Lecturer extends User {

    public Lecturer(int userID) throws SQLException {
        // TODO: get from database
        super(userID);
    }

    public Lecturer(int userID, int managerID, String forename, String surname, String email, String gender, boolean activated) {
        // TODO: Lecturer specific fields
        super(userID, managerID, forename, surname, email, gender, activated);
    }

}
