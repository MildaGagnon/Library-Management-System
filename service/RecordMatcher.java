package FinalProject.service;

import FinalProject.model.Student;
import FinalProject.storage.BinaryStudentRegistry;

import java.util.concurrent.Callable;
//2431250 Milda Gagnon
// PDF Requirement 3.5: RecordMatcher must run in its own thread
// and be used to search for a student.
public class RecordMatcher implements Callable<Student> {

    // Where we look for the student (binary registry)
    // PDF Requirement 3.5: search in BinaryStudentRegistry
    private BinaryStudentRegistry registry;
    // The ID we want to find
    private String studentId;

    // Constructor: remember registry + student ID
    public RecordMatcher(BinaryStudentRegistry registry, String studentId) {
        this.registry = registry;
        this.studentId = studentId;
    }

    @Override
    public Student call() {
        // This method is executed inside a background thread.
        //  call findById on the registry.
        return registry.findById(studentId);
    }
}
