import java.sql.*;
import java.util.*;


public class App {
    public static String position;
    public static String email;
    public static String password;

    //JDBC
    static JDBC jdbcNewRegistration;
    static {
        try {
            jdbcNewRegistration = new JDBC("select * from staff", "INSERT INTO registrations (email, password, position) VALUES (?, ?, ?)");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    //JDBC


    public static void main(String[] args) throws SQLException {
        Scanner scan = new Scanner(System.in);
        Headmaster headmaster = new Headmaster();
        AccessData registrations = new AccessData();
        System.out.println("============================== CHOOSE AN OPTION ==============================");
        System.out.println();
        boolean nextStep = false; //if login is completed successfully
        ArrayList<Map<String, List<String>>> registrationsList = registrations.getList(); //copy of registrationsDatabase from AccessData class


        //LOGIC FOR LOG IN
        // none -> ask headmaster to add your email
        // email -> enter new password
        // email + password -> log in
        //LOGIC FOR LOG IN


        //User Interface(console mode) - LOGIN
        while (!nextStep) {
            System.out.println("{Log in}(1)");
            System.out.println("{Exit}(2)");
            String clientInput = scan.nextLine();
            switch (clientInput) { //Check what is the input(1 or 2)
                case "1" -> { //If it is 1, begin the login process
                    System.out.print("Enter your e-mail: ");
                    email = scan.nextLine();


                    //If there are registrations, begin new registration
                    if (!registrationsList.isEmpty()) {
                        registrations.newRegisteredUser(email);
                    }
                    //If there are registrations, begin new registration


                    System.out.print("Enter your password: ");
                    password = scan.nextLine();


                    //If there aren't any registrations, hardcode the Headmaster
                    if (registrationsList.isEmpty()) {
                        headmaster.createHeadmaster("Radostin", "Dinov", "Headmaster");
                        registrations.newRegistration("radi2044@abv.bg", "123", "Headmaster");
                        try {
                            jdbcNewRegistration.writeData.setString(1, "radi2044@abv.bg");
                            jdbcNewRegistration.writeData.setString(2, "123");
                            jdbcNewRegistration.writeData.setString(3, "Headmaster");
                            jdbcNewRegistration.writeData.executeUpdate();
                        } catch (SQLException throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    //If there aren't any registrations, hardcode the Headmaster


                    //If email and password match to valid account, log in to the account
                    if (registrations.checkLogin(registrations.registrationsDatabase, email, password)) {
                        position = registrations.getPosition(email);
                        nextStep = true;
                    }
                    //If email and password match to valid account, log in to the account


                }

                case "2" -> { //If it is 2, exit the program
                    System.out.println("Shutting down...");
                    System.exit(0);
                }
            }
        }
        //User Interface(console mode) - LOGIN

        Classroom classroom = new Classroom("181"); //Must be coded other way
        System.out.println("============================== e-Journal ==============================");
        switch (position) {


            //If the logged in user is Headmaster
            case "Headmaster" -> {
                System.out.println(
                        """
                                {Add teacher}(1)                 {Add grade}(6)
                                {Remove teacher}(2)              {Remove grade}(7)
                                {Add students}(3)                {Print students}(8)
                                {Remove students}(4)             {Print student}(9)
                                {Change password}(5)             {End program}(10)
                                                                 {Session}(11)
                                """);
                String choice = scan.nextLine(); //What will be the Headmaster input(1-11)
                while (!choice.equalsIgnoreCase("10")) { //If the input is NOT 10(End program)


                    //Control room
                    switch (choice.toLowerCase()) {
                        case "1" -> System.out.println("SOON");
                        case "2" -> System.out.println("SOON");
                        case "3" -> classroom.addStudents();
                        case "4" -> classroom.removeStudent();
                        case "5" -> registrations.changePassword(email);
                        case "6" -> classroom.addGrade();
//                        case "7" -> classroom.removeGrade();
                        case "8" -> classroom.printAllStudents();
                        case "9" -> classroom.printStudent();
                        case "11" -> headmaster.getHeadmasterInfo(email, position);
                    }
                    //Control room


                    System.out.println();
                    System.out.println(
                            """
                                    {Add teacher}(1)                 {Add grade}(6)
                                    {Remove teacher}(2)              {Remove grade}(7)
                                    {Add students}(3)                {Print students}(8)
                                    {Remove students}(4)             {Print student}(9)
                                    {Change password}(5)             {End program}(10)
                                                                     {Session}(11)
                                    """);
                    choice = scan.nextLine();
                } //End of while


                //If the input is 10(End program)
                System.out.println("Shutting down...");
                System.exit(0);
                //If the input is 10(End program)


            }
            //If the logged in user is Headmaster


            //If the logged in user is Student
            case "Student" -> {
                System.out.println("You are logged in as student!");
            }
            //If the logged in user is Student


        }
    }
}

//TODO Add teacher -> put email + first and last name (AND AFTER ADDING THE TEACHER, YOU CAN ASSIGN IT A GROUP) (headmaster only)
//TODO new method -> add group (headmaster only)
//TODO new method -> remove group (headmaster only)
//TODO Когато се добави нов ученик и сесията НЕ СЕ ПРЕКЪСВА и се добави оценка - тя се добавя на всички предмети(само в арейлиста, в SQL-а е добре)
//TODO Когато се добави нов ученик и сесията НЕ СЕ ПРЕКЪСВА и се добави ОТНОВО оценка - предишната оценка се запазва и се добавят и двете оценки в SQL-а + продължава да се добавя на всички предмети в арейлиста

//!!!! if grades aren't shown in student info, the student has been added the same session !!!!