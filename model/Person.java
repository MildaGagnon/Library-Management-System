package FinalProject.model;

import java.io.Serializable;
//2431250 Milda Gagnon
// PDF Requirement 2.1: Person is the base class for Student.
public abstract class Person implements Serializable {

    // PDF Requirement 3.1: Student ID and Name must be stored.
    protected String id;
    protected String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Simple setter
    public void setName(String name) {
        this.name = name;
    }
}
