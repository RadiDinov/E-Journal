import java.io.*;
import java.sql.*;
import java.util.*;


public class Classroom implements Serializable {
    private final Scanner scan = new Scanner(System.in);
    ArrayList<Student> students = new ArrayList<>();
//    ArrayList<Integer> standardGrade = new ArrayList<>(Collections.singletonList(0));
    AccessData data = new AccessData();

    private String firstName;
    private String lastName;
    private String classNumber;
    private String MathematicsGrades;
    private String HistoryGrades;
    private String PhysicsGrades;
    private String ProgrammingGrades;
    private String SportsGrades;


    //JDBC
    JDBC jdbcNewRegistration = new JDBC("select * from students", "INSERT INTO registrations (email, password, position) VALUES (?, ?, ?)");
    JDBC jdbcNewStudent = new JDBC("select * from students", "INSERT INTO students (firstName, lastName, classNumber, Mathematics, History, Physics, Programming, Sports) VALUES (?, ?, ?, 0, 0 ,0 ,0 ,0)");
    JDBC jdbcRemoveStudent = new JDBC("select * from students", "DELETE FROM students WHERE classNumber = ?");
    JDBC jdbcNewGradeMathematics = new JDBC("select * from students", "UPDATE students SET Mathematics = ? WHERE classNumber = ?");
    JDBC jdbcNewGradeHistory = new JDBC("select * from students", "UPDATE students SET History = ? WHERE classNumber = ?");
    JDBC jdbcNewGradePhysics = new JDBC("select * from students", "UPDATE students SET Physics = ? WHERE classNumber = ?");
    JDBC jdbcNewGradeProgramming = new JDBC("select * from students", "UPDATE students SET Programming = ? WHERE classNumber = ?");
    JDBC jdbcNewGradeSports = new JDBC("select * from students", "UPDATE students SET Sports = ? WHERE classNumber = ?");
    //JDBC


    //Construct with group param
    public Classroom(String group) throws SQLException {
        this.students = readDatabase();
    }
    //Construct with group param


    //Getting all data from DB(`students`) in students
    public ArrayList<Student> readDatabase() {
        try {
            while (jdbcNewRegistration.resultSet.next()) {
                this.firstName = jdbcNewRegistration.resultSet.getString("firstName");
                this.lastName = jdbcNewRegistration.resultSet.getString("lastName");
                this.classNumber = jdbcNewRegistration.resultSet.getString("classNumber");
                this.MathematicsGrades = jdbcNewRegistration.resultSet.getString("Mathematics"); // "5 3 2"
                this.HistoryGrades = jdbcNewRegistration.resultSet.getString("History");
                this.PhysicsGrades = jdbcNewRegistration.resultSet.getString("Physics");
                this.ProgrammingGrades = jdbcNewRegistration.resultSet.getString("Programming");
                this.SportsGrades = jdbcNewRegistration.resultSet.getString("Sports");

                Student newStudent = new Student(firstName, lastName, classNumber);
                newStudent.grades.add(new Grade("Mathematics", getStudentGradesFromSubject(MathematicsGrades)));
                newStudent.grades.add(new Grade("History", getStudentGradesFromSubject(HistoryGrades)));
                newStudent.grades.add(new Grade("Physics", getStudentGradesFromSubject(PhysicsGrades)));
                newStudent.grades.add(new Grade("Programming", getStudentGradesFromSubject(ProgrammingGrades)));
                newStudent.grades.add(new Grade("Sports", getStudentGradesFromSubject(SportsGrades)));

                students.add(newStudent);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return students;
    }
    //Getting all data from DB(`students`) in students


    //Helper of readDatabase()
    private ArrayList<Integer> getStudentGradesFromSubject(String subjectGrades) {
        ArrayList<Integer> readGrade = new ArrayList<>();
        Scanner scan = new Scanner(subjectGrades);
        while (scan.hasNextInt()) {
            readGrade.add(scan.nextInt());
        }

        return readGrade;
    }
    //Helper of readDatabase()


    //Getters and Setters
    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }
    //Getters and Setters


    //View all students info if the user wants
    public void printAllStudents() {
        if (this.students.isEmpty()) {
            System.out.println("There are no students!");
            return;
        }
        for (Student s : this.students) {
            System.out.println("First name: " + s.getFirstName());
            System.out.println("Last name: " + s.getLastName());
            System.out.println("ClassNumber: " + s.getClassNumber());
            for (Grade g : s.grades) {
                System.out.println("Subject: " + g.getSubject() + " - Grade: " + g.getGrade());
            }
            System.out.println();
        }
    }
    //View all students info if the user wants


    //View a student(by classNumber)'s info if the user wants
    public void printStudent() {
        if (this.students.isEmpty()) {
            System.out.println("There are no students!");
            return;
        }
        System.out.print("Enter ClassNumber: ");
        String classNumber = scan.nextLine();
        if (checkValidClassNumber(classNumber)) {
            for (Student s : this.students) {
                if (s.getClassNumber().equals(classNumber)) {
                    System.out.println("First name: " + s.getFirstName());
                    System.out.println("Last name: " + s.getLastName());
                    System.out.println("ClassNumber: " + s.getClassNumber());
                    for (Grade g : s.grades) {
                        System.out.println("Subject: " + g.getSubject() + " - Grade: " + g.getGrade());
                    }
                    System.out.println();
                }
            }
        } else {
            System.out.println("There is no student with that ClassNumber!");
        }
    }
    //View a student(by classNumber)'s info if the user wants


    //Adding a student(only Headmaster can)
    public void addStudents() throws SQLException {
        System.out.print("How many students do you want to add: ");
        int count = Integer.parseInt(scan.nextLine());
        for (int i = 0; i < count; i++) {
            AccessData registrations = new AccessData();
            System.out.printf("\nStudent <%d>: \n", i + 1);
            System.out.print("First name: ");
            String firstName = scan.nextLine();
            System.out.print("Last name: ");
            String lastName = scan.nextLine();
            System.out.print("ClassNumber: ");
            String classNumber = scan.nextLine();
            System.out.print("Email: ");
            String email = scan.nextLine();
            while (true) {
                for (Map<String, List<String>> stringListMap : registrations.registrationsDatabase) {
                    if (stringListMap.containsKey(email)) {
                        System.out.println("This email already exists!");
                        return;
                    }
                }
                if (!checkValidClassNumber(classNumber)) {
                    break;
                } else {
                    System.out.println("This number already exists!");
                    System.out.print("ClassNumber: ");
                    classNumber = scan.nextLine();
                }
            }
            try {
                jdbcNewStudent.writeData.setString(1, firstName);
                jdbcNewStudent.writeData.setString(2, lastName);
                jdbcNewStudent.writeData.setString(3, classNumber);
                jdbcNewStudent.writeData.executeUpdate();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            registrations.addRegistration(email);
            Student newStudent = new Student(firstName, lastName, classNumber);
            ArrayList<Integer> standardGrade1 = new ArrayList<>(Collections.singletonList(0));
            ArrayList<Integer> standardGrade2 = new ArrayList<>(Collections.singletonList(0));
            ArrayList<Integer> standardGrade3 = new ArrayList<>(Collections.singletonList(0));
            ArrayList<Integer> standardGrade4 = new ArrayList<>(Collections.singletonList(0));
            ArrayList<Integer> standardGrade5 = new ArrayList<>(Collections.singletonList(0));
            newStudent.grades.add(new Grade("Mathematics", standardGrade1));
            newStudent.grades.add(new Grade("History", standardGrade2));
            newStudent.grades.add(new Grade("Physics", standardGrade3));
            newStudent.grades.add(new Grade("Programming", standardGrade4));
            newStudent.grades.add(new Grade("Sports", standardGrade5));
            this.students.add(newStudent);
        }
    }
    //Adding a student(only Headmaster can)


    //Validations
    public int checkValidGrade(Integer grade) {
        while (true) {
            if (grade >= 2 && grade <= 6) {
                return grade;
            }
            System.out.print("Please enter valid student grade (2 - 6): ");
            grade = Integer.parseInt(scan.nextLine());
        }
    }

    public boolean checkValidSubject(String classNum, String subject) {
        if (students.isEmpty()) {
            return false;
        }
        for (Student student : students) {
            if (student.getClassNumber().equals(classNum)) {
                for (Grade g : student.grades) {
                    if (g.getSubject().equals(subject)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkValidClassNumber(String classNum) {
        if (students.isEmpty()) {
            return false;
        }
        for (Student student : students) {
            if (classNum.equals(student.getClassNumber())) {
                return true;
            }
        }
        return false;
    }
    //Validations


    //Adding a grade(only Teacher/Headmaster can)
    public void addGrade() throws SQLException {
        if (students.isEmpty()) {
            System.out.println("There are no students!");
            return;
        }
        System.out.print("Enter ClassNumber: ");
        String classNum = scan.nextLine();
        if (checkValidClassNumber(classNum)) {
            System.out.println("Subjects:\n{Mathematics}(1)\n{History}(2)\n{Physics}(3)\n{Programming}(4)\n{Sports}(5)");
            System.out.print("Enter Subject: ");
            int subjectInt = Integer.parseInt(scan.nextLine()); //3
            String subject = "";
            while (true) {
                subject = (subjectInt == 1) ? subject = "Mathematics" : (subjectInt == 2) ? subject = "History" : (subjectInt == 3) ? subject = "Physics" : (subjectInt == 4) ? subject = "Programming" : (subjectInt == 5) ? subject = "Sports" : "a@!a"; //using ternary operator
                if (subject.equals("a@!a")) {
                    System.out.print("Please insert 1-5: ");
                    subjectInt = Integer.parseInt(scan.nextLine());
                } else {
                    break;
                }
            }

            System.out.print("Enter Grade: ");
            int grade = checkValidGrade(Integer.parseInt(scan.nextLine()));
            for (Student student : students) {
                if (student.getClassNumber().equals(classNum)) {
                    for (Grade g : student.grades) {
                        if (g.getSubject().equals(subject)) { //If subject is in the database
                            if (subject.equals("Mathematics")) {
                                helpAddGrade(0, grade, classNum, student, jdbcNewGradeMathematics, subject);
                                students = readDatabase();
                                return;
                            } else if (subject.equals("History")) {
                                helpAddGrade(1, grade, classNum, student, jdbcNewGradeHistory, subject);
                                students = readDatabase();
                                return;
                            } else if (subject.equals("Physics")) {
                                helpAddGrade(2, grade, classNum, student, jdbcNewGradePhysics, subject);
                                students = readDatabase();
                                return;
                            } else if (subject.equals("Programming")) {
                                helpAddGrade(3, grade, classNum, student, jdbcNewGradeProgramming, subject);
                                students = readDatabase();
                                return;
                            } else {
                                helpAddGrade(4, grade, classNum, student, jdbcNewGradeSports, subject);
                                students = readDatabase();
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("There is no student with that ClassNumber!");
        }
    }
    //Adding a grade(only Teacher/Headmaster can)


    //Helper of addGrade()
    public void helpAddGrade(int subjectIndex, int grade, String classNumber, Student student, JDBC database, String subject) throws SQLException {
        for (Grade g : student.grades) {
            if (g.getSubject().equals(subject)) {
                if (g.getGrade().get(0) == 0) {
                    g.getGrade().set(0, grade);
                    database.writeData.setString(1, String.valueOf(grade));
                    database.writeData.setString(2, classNumber);
                    database.writeData.executeUpdate();
                    System.out.println("Successfully added grade!");
                    return;
                } else {
                    Grade currentGrades = student.getGrades().get(subjectIndex);
                    ArrayList<Integer> gradesInArrayList = new ArrayList<>(currentGrades.getGrade());
                    StringBuilder sb = new StringBuilder();
                    for (Integer integer : gradesInArrayList) {
                        sb.append(integer).append(" ");
                    }
                    String convertToString = sb + "";
                    convertToString += grade;
                    database.writeData.setString(1, convertToString);
                    database.writeData.setString(2, classNumber);
                    database.writeData.executeUpdate();
                    System.out.println("Successfully added grade!");
                    g.getGrade().add(grade);
                    return;
                }
            }
        }

    }
    //Helper of addGrade()


//    public void removeGrade() {
//        if (this.students.isEmpty()) {
//            System.out.println("There are no students");
//            return;
//        }
//        System.out.print("Enter ClassNumber: ");
//        String classNum = scan.nextLine();
//        if (checkValidClassNumber(classNum)) {
//            System.out.print("Enter Subject: ");
//            String subject = scan.nextLine();
//            if (checkValidSubject(classNum, subject)) {
//                System.out.print("What grade do you want to remove: ");
//                int chosenGrade = checkValidGrade(Integer.parseInt(scan.nextLine()));
//                for (Student student : students) {
//                    if (student.getClassNumber().equals(classNum)) {
//                        for (Grade g : student.grades) {
//                            if (g.getSubject().equals(subject)) {
//                                if (g.getGrade().size() == 1 && g.getGrade().contains(chosenGrade)) {
//                                    student.grades.remove(g);
//                                } else {
//                                    g.getGrade().remove(g.getGrade().indexOf(chosenGrade));
//                                }
//                                return;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    public void removeStudent() throws SQLException {
        System.out.print("Enter the class number of the student you want to remove: ");
        String classNum = scan.nextLine();
        System.out.print("Enter the email of the student you want to remove: ");
        String email = scan.nextLine();
        if (checkValidClassNumber(classNum)) {
            for (Student student : students) {
                if (student.getClassNumber().equals(classNum)) {
                    students.remove(student);
                    jdbcRemoveStudent.writeData.setString(1, classNum);
                    jdbcRemoveStudent.writeData.executeUpdate();
                    data.removeRegistration(email);
                    return;
                }
            }
        }
    }
}