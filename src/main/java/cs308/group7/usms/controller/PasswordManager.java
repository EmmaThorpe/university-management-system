package cs308.group7.usms.controller;

public class PasswordManager {

    /** Uses the database to check if a sign in is successful or not
     * @param Email
     * @param Password
     * @return A JSON String representing if the sign in was successful. Null is returned on details not being right and a json array containing the userID, the role and if the user is activated or not is returned if the details are correct.
     */
    public String login(String Email, String Password){
        return "user";
    }

    public boolean changePass(){
        return true;
    }
}
