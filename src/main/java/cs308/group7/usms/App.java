package cs308.group7.usms;

public class App {

    public static DatabaseConnection databaseConnection;
    public static SystemOperations systemOperations;

    public static void main(String[] args) {
        databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
        systemOperations = new SystemOperations(databaseConnection);
        System.out.println("Hello world!");
    }
}