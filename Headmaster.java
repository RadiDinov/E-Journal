import java.io.*;
import java.sql.*;
import java.util.*;

public class Headmaster implements Serializable {
    private String firstName;
    private String lastName;
    ArrayList<Headmaster> headmasters = new ArrayList<>();
    private String position;

    //JDBC
    JDBC jdbcNewHeadmaster = new JDBC("select * from staff", "INSERT INTO staff (firstName, lastName, position) VALUES (?, ?, ?)");
    //JDBC


    //Getting all data from DB(`staff`) in headmasters
    public ArrayList<Headmaster> readDatabase() {
        try {
            while (jdbcNewHeadmaster.resultSet.next()) {
                String firstName = jdbcNewHeadmaster.resultSet.getString("firstName");
                String lastName = jdbcNewHeadmaster.resultSet.getString("lastName");
                String position = jdbcNewHeadmaster.resultSet.getString("position");
                Headmaster headmaster = new Headmaster(firstName, lastName, position);
                headmasters.add(headmaster);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return headmasters;
    }
    //Getting all data from DB(`staff`) in headmasters


    //Default construct
    public Headmaster() throws SQLException {
        headmasters = new ArrayList<>();
        this.headmasters = readDatabase();
    }
    //Default construct


    //Construct with firstName, lastName, position param
    public Headmaster(String firstName, String lastName, String position) throws SQLException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }
    //Construct with firstName, lastName, position param


    //Construct with firstName, lastName, position param + adding new headmaster to headmasters
    public void createHeadmaster(String firstName, String lastName, String position) throws SQLException {
        try {
            jdbcNewHeadmaster.writeData.setString(1, firstName);
            jdbcNewHeadmaster.writeData.setString(2, lastName);
            jdbcNewHeadmaster.writeData.setString(3, position);
            jdbcNewHeadmaster.writeData.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        Headmaster newHeadmaster = new Headmaster(firstName, lastName, position);

        this.headmasters.add(newHeadmaster);
    }
    //Construct with firstName, lastName, position param + adding new headmaster to headmasters


    //View logged session if the headmaster wants
    public void getHeadmasterInfo(String email, String receivedPosition) throws SQLException {
        AccessData data = new AccessData();
        String firstName = null;
        String lastName = null;
        if (data.getPosition(email).equals(receivedPosition)) {
            for (Headmaster headmaster : this.headmasters) {
                firstName = headmaster.firstName;
                lastName = headmaster.lastName;
            }
        } else {
            System.out.println("Your positions don't match in our database! Please send this message to our administrators!");
        }
        String position = data.getPosition(email);
        System.out.println("Current session");
        System.out.printf("First name: %s\nLast name: %s\nPosition: %s\n", firstName, lastName, position);
    }
    //View logged session if the headmaster wants
}
