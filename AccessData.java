import java.io.*;
import java.sql.*;
import java.util.*;

public class AccessData implements Serializable {
    private final Scanner scan = new Scanner(System.in);
    ArrayList<Map<String, List<String>>> registrationsDatabase = new ArrayList<>();
    //How are things in registrationsDatabase stored:
    //-> ArrayList - saves EVERY registered person -> index(0) - HEADMASTER
    //-> Map - K: email; V: password(0) position(1)
    private String position;


    //JDBC
    JDBC jdbcNewRegistration = new JDBC("select * from registrations", "INSERT INTO registrations (email, password, position) VALUES (?, ?, ?)");
    JDBC jdbcRemoveRegistration = new JDBC("select * from registrations", "DELETE FROM registrations WHERE email = ?");
    JDBC jdbcUpdatePassword = new JDBC("select * from registrations", "UPDATE registrations SET password = ? WHERE email = ?");
    //JDBC


    //Getting all data from DB(`registrations`) in registrationsDatabase
    public ArrayList<Map<String, List<String>>> readDatabase() {
        try {
            while (jdbcNewRegistration.resultSet.next()) {
                String email = jdbcNewRegistration.resultSet.getString("email");
                String password = jdbcNewRegistration.resultSet.getString("password");
                String position = jdbcNewRegistration.resultSet.getString("position");
                List<String> values = new ArrayList<>();
                values.add(password);
                values.add(position);
                Map<String, List<String>> map = new LinkedHashMap<>();
                map.put(email, values);
                registrationsDatabase.add(map);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return registrationsDatabase;
    }
    //Getting all data from DB(`registrations`) in registrationsDatabase


    //Default construct
    public AccessData() throws SQLException {
        this.registrationsDatabase = readDatabase();
    }
    //Default construct


    //Checking if email + password are correct when logging in
    public boolean checkLogin(ArrayList<Map<String, List<String>>> databaseInfo, String email, String password) {
        for (int i = 0; i < databaseInfo.size(); i++) {
            for (Map<String, List<String>> stringListMap : databaseInfo) {
                if (stringListMap.containsKey(email)) {
                    String[] value = new String[3];
                    for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
                        value[0] = entry.getKey(); //email
                        value[1] = entry.getValue().get(0); //password
                        value[2] = entry.getValue().get(1); //position
                        position = value[2];
                        if (value[0].equals(email) && value[1].equals(password)) {
                            System.out.println("MATCH!");
                            return true;
                        } else {
                            System.out.println("Check email and password, then try again!");
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    //Checking if email + password are correct when logging in


    //Adding new registration
    public void newRegistration(String email, String password, String position) {
        List<String> values = new ArrayList<>();
        values.add(password);
        values.add(position);
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put(email, values);
        this.registrationsDatabase.add(map);
    }
    //Adding new registration


    //Getting whole registrationDatabase
    public ArrayList<Map<String, List<String>>> getList() {
        return this.registrationsDatabase;
    }
    //Getting whole registrationDatabase


    //When user logs in for the first time
    public void newRegisteredUser(String email) {
        boolean changed = false;
        for (int i = 0; i < registrationsDatabase.size(); i++) {
            for (Map<String, List<String>> stringListMap : registrationsDatabase) {
                if (stringListMap.containsKey(email)) {
                    for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
                        if (entry.getValue().get(0).equals("newStudent")) {
                            System.out.println("Headmaster has added you in the e-Journal!");
                            System.out.print("Please, enter password for your account: ");
                            String newPassword = scan.nextLine();
                            position = getPosition(email);
                            changePasswordAutomatic(email, newPassword);
                            System.out.println("Your registration is SUCCESSFUL!");
                            changed = true;
                            break;
                        }
                    }
                }
                if (changed) {
                    break;
                }
            }
            if (changed) {
                break;
            }
        }
    }
    //When user logs in for the first time


    //When adding a student we need to create his registration
    public void addRegistration(String email) {
        List<String> values = new ArrayList<>();
        values.add("newStudent");
        values.add("Student");
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put(email, values);
        registrationsDatabase.add(map);
        try {
            jdbcNewRegistration.writeData.setString(1, email);
            jdbcNewRegistration.writeData.setString(2, "newStudent");
            jdbcNewRegistration.writeData.setString(3, "Student");
            jdbcNewRegistration.writeData.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    //When adding a student we need to create his registration


    //When removing a student we need to remove his registration
    public void removeRegistration(String email) {
        registrationsDatabase.removeIf(stringListMap -> stringListMap.containsKey(email));
        try {
            jdbcRemoveRegistration.writeData.setString(1, email);
            jdbcRemoveRegistration.writeData.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    //When removing a student we need to remove his registration


    //Change password if the user wants
    public void changePassword(String email) {
        System.out.println("Changing password started!");
        System.out.println("Please enter current password: ");
        String currentPassword = scan.nextLine();
        System.out.println("Please enter new password: ");
        String newPassword = scan.nextLine();
        for (int i = 0; i < registrationsDatabase.size(); i++) {
            for (Map<String, List<String>> stringListMap : registrationsDatabase) {
                if (stringListMap.get(email).get(0).equals(currentPassword)) {
                    registrationsDatabase.get(i).get(email).set(0, newPassword);
                    try {
                        jdbcUpdatePassword.writeData.setString(1, newPassword);
                        jdbcUpdatePassword.writeData.setString(2, email);
                        jdbcUpdatePassword.writeData.executeUpdate();
                        //String sqlRequestUpdatePassword = "UPDATE registrations set password = ? where email = ?";
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println("SUCCESSFULLY changed password!");
                    System.out.printf("New password: %s", newPassword);
                    return;
                } else {
                    System.out.println("Current password is INCORRECT!");
                    System.out.println("TRY AGAIN!");
                }
            }
        }
    }
    //Change password if the user wants


    //When user joins for the first time, his HARDCODED password is changed to the inserted one
    public void changePasswordAutomatic(String email, String newPassword) {
        for (int i = 0; i < registrationsDatabase.size(); i++) {
            for (Map<String, List<String>> stringListMap : registrationsDatabase) {
                if (stringListMap.get(email) != null) {
                    if (stringListMap.get(email).get(0).equals("newStudent")) {
                        registrationsDatabase.get(i).get(email).set(0, newPassword);
                        try {
                            jdbcUpdatePassword.writeData.setString(1, newPassword);
                            jdbcUpdatePassword.writeData.setString(2, email);
                            jdbcUpdatePassword.writeData.executeUpdate();
                        } catch (SQLException throwable) {
                            System.out.println(throwable.getMessage());
                        }
                        break;
                    }
                } else {
                    i++;
                }
            }
        }
    }
    //When user joins for the first time, his HARDCODED password is changed to the inserted one


    //Getting position if wanted
    public String getPosition(String email) {
        for (int i = 0; i < registrationsDatabase.size(); i++) {
            for (Map<String, List<String>> stringListMap : registrationsDatabase) {
                for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
                    if (entry.getKey().equals(email)) {
                        return entry.getValue().get(1); //position
                    }
                }
            }
        }
        return null;
    }
    //Getting position if wanted


//    public String getPassword(String email) {
//        for (int i = 0; i < registrationsDatabase.size(); i++) {
//            for (Map<String, List<String>> stringListMap : registrationsDatabase) {
//                for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
//                    if(entry.getKey().equals(email)) {
//                        return entry.getValue().get(0); //position
//                    }
//                }
//            }
//        }
//        return null;
//    }


}