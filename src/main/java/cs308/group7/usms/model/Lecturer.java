package cs308.group7.usms.model;

import java.sql.Date;
import java.sql.SQLException;

public class Lecturer extends User {

    public Lecturer(String userID) throws SQLException {
        // TODO: get from database
        super(userID);
    }

    public Lecturer(String userID, String managerID, String forename, String surname, String email, Date dob, String gender, UserType type, boolean activated) {
        // TODO: Lecturer specific fields
        super(userID, managerID, forename, surname, email, dob, gender, type, activated);
    }

}
