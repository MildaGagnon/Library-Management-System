package FinalProject.storage;

import FinalProject.model.Student;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
//2431250 Milda Gagnon
// The registry handles ONLY student file operations.
// Separation of concerns:
//   - NO UI code
//   - NO rental logic
//   - Only saving and loading students
// This prevents the code from becoming tangled.

// PDF Requirement 3.1: Store all students in students.dat using binary format.
public class BinaryStudentRegistry {

    // Path to students.dat
    private Path dataFile;
    // List holding all Student objects in memory
    private List<Student> students = new ArrayList<>();

    // Constructor: build path and immediately load from file
    public BinaryStudentRegistry(Path baseDir) {
        dataFile = baseDir.resolve("students.dat");
        load();
    }

    // Load the student list from students.dat if it exists
    private void load() {
        if (!Files.exists(dataFile)) return;

        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(dataFile))) {
            // File contains List<Student>
            students = (List<Student>) in.readObject(); // <-- Reading from file
        } catch (Exception e) {
            // Ignore errors
        }
    }

    // Save the current list of students to the binary file
    private void save() {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(dataFile))) {
            out.writeObject(students); // <-- Writing to file
        } catch (Exception e) {
            // Ignore errors
        }
    }

    // Add new student or update an existing one with the same ID
    public void addOrUpdateStudent(Student s) {
        // Simple linear search
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(s.getId())) {
                // Replace existing student object
                students.set(i, s);
                save();
                return;
            }
        }
        // If not found, just add to the list
        students.add(s);
        save();
    }

    // PDF Requirement 3.1: ability to search by ID
    public Student findById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null; // not found
    }

    // Return a copy of the whole list (used by GUI to show table)
    public List<Student> listAll() {
        return new ArrayList<>(students);
    }
}
