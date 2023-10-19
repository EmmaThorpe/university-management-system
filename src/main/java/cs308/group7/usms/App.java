package cs308.group7.usms;

public class App {

    public static DatabaseConnection databaseConnection;

    public static void main(String[] args) {
        databaseConnection = new DatabaseConnection("src/main/resources/dbConnect.txt");
        System.out.println("Hello world!");
    }
}