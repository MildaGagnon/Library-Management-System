package FinalProject.model;

import java.io.Serializable;
//2431250 Milda Gagnon
// PDF Requirement 1: Student extends Person
public class Student extends Person implements Serializable {

    private String program;
    private int year;

    public Student(String id, String name, String program, int year) {
        super(id, name);
        this.program = program;
        this.year = year;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // PDF Requirement: EXACT format for Student Logs
    @Override
    public String toString() {
        return getId() + " - " + getName() + " (" + program + " Y" + year + ")";
    }
}
