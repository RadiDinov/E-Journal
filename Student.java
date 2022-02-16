import java.io.Serializable;
import java.util.ArrayList;

public class Student implements Serializable {
    public String firstName;
    public String lastName;
    private String classNumber;
    ArrayList<Grade> grades;

    public Student(String firstName, String lastName, String classNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.classNumber = classNumber;
        this.grades = new ArrayList<Grade>();
    }

    public Student() {
        this.grades = new ArrayList<Grade>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        this.grades = grades;
    }

}