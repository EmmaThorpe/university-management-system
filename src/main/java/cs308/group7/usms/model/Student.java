package cs308.group7.usms.model;

import java.sql.Date;
import java.sql.SQLException;

public class Student extends User {

    public Student(String userID) throws SQLException {
        // TODO: get from database
        super(userID);
    }

    public Student(String userID, String managerID, String forename, String surname, String email, Date dob, String gender, UserType type, boolean activated) {
        // TODO: Student specific fields
        super(userID, managerID, forename, surname, email, dob, gender, type, activated);
    }

}
