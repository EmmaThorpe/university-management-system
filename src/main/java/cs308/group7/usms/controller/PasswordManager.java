package cs308.group7.usms.controller;

import java.util.HashMap;
import java.util.Map;

public class PasswordManager {

    /** Uses the database to check if a sign in is successful or not
     * @param Email
     * @param Password
     * @return A Map representing if the sign in was successful. Null is returned on details not being right and a map containing the userID, the role and if the user is activated or not is returned if the details are correct.
     */
    public Map<String,String> login(String Email, String Password){
        Map<String,String> user= new HashMap<String, String>();
        user.put(Email, Password);
        return user;
    }



}
