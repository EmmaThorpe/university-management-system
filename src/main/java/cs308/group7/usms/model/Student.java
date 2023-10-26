package cs308.group7.usms.model;

import java.sql.SQLException;

public class Student extends User {

    public Student(int userID) throws SQLException {
        // TODO: get from database
        super(userID);
    }

    public Student(int userID, int managerID, String forename, String surname, String email, String gender, boolean activated) {
        // TODO: Student specific fields
        super(userID, managerID, forename, surname, email, gender, activated);
    }

}
