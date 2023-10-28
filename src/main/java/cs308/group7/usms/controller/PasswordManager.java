package cs308.group7.usms.controller;

import java.util.HashMap;
import java.util.Map;

public class PasswordManager {

    /** Uses the database to check if a sign in is successful or not
     * @param email The email the user enters
     * @param password The password the user enters
     * @return A Map representing if the sign in was successful. Null is returned on details not being right and a map containing the userID, the role and if the user is activated or not is returned if the details are correct.
     */
    public Map<String,String> login(String email, String password){
        Map<String,String> user= new HashMap<>();
        user.put("userID", "idk2?");
        if(email.equals("student") && password.equals("a")){
            user.put("role", "Student");
            user.put("activated", "True");

        }else if(email.equals("lecturer") && password.equals("a")){
            user.put("role", "Lecturer");
            user.put("activated", "True");

        }else if(email.equals("manager") && password.equals("a")){
            user.put("role", "Manager");
            user.put("activated", "True");

        }else if(email.equals("unactivated") && password.equals("a")){
            user.put("role", "Student");
            user.put("activated", "False");

        }else{
            return null;
        }
        System.out.println(user.toString());

        return user;
    }



}
