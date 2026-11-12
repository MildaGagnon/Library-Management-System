package FinalProject.service;

import FinalProject.model.Book;
import FinalProject.model.EventType;
import FinalProject.model.Student;
import FinalProject.storage.BinaryStudentRegistry;
import FinalProject.storage.LibraryCatalog;
import FinalProject.storage.StudentFileLog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//2431250 Milda Gagnon

// PDF Requirement 3.3: Class that handles renting and returning books
// PDF Requirement 3.5: Must use a thread (ExecutorService) for student lookup
public class LibraryService {

    // Handles students.dat
    private BinaryStudentRegistry registry;
    // Handles catalog.dat
    private LibraryCatalog catalog;
    // Folder where log files are stored
    private Path logsDir;

    // Very simple thread pool with only 1 worker thread
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // Constructor: create directories and load registry + catalog
    public LibraryService(Path baseDir) {
        try {
            // Make sure data directory and logs subfolder exist
            Files.createDirectories(baseDir);
            Files.createDirectories(baseDir.resolve("logs"));
        } catch (Exception e) {
            // Ignore errors
        }

        logsDir = baseDir.resolve("logs");
        registry = new BinaryStudentRegistry(baseDir);
        catalog = new LibraryCatalog(baseDir);
    }

    // Getters used by the GUI (MainApp)
    public BinaryStudentRegistry getRegistry() { return registry; }
    public LibraryCatalog getCatalog() { return catalog; }

    // PDF Requirement 3.3: Rent a book
    // renting thread-safe
    public boolean rentBook(String studentId, String isbn) {
        try {
            // PDF Requirement 3.5: search for student using a background thread
            Student found = executor.submit(new RecordMatcher(registry, studentId)).get();
            // If student not found, fail
            if (found == null) return false;

            // Look up the book in the catalog
            Book book = catalog.get(isbn);
            // If missing or no avlailable copies, fail
            if (book == null || !book.isAvailable()) return false;

            // Mark one copy as checked out
            book.checkout();
            // Save updated catalog to catalog.dat
            catalog.saveCatalog();

            // PDF Requirement 3.3: Write RENT event to student's log file
            new StudentFileLog(logsDir, studentId)
                    .append(EventType.RENT, isbn, book.getTitle());

            return true;

        } catch (Exception e) {
            // If any error happens, rent failed
            return false;
        }
    }

    // PDF Requirement 3.3: Return a book
    public boolean returnBook(String studentId, String isbn) {
        // Look up the book first
        Book book = catalog.get(isbn);
        if (book == null) return false;

        // Increase available copies
        book.checkin();
        // Save catalog back to file
        catalog.saveCatalog();

        // Log RETURN event for this student
        new StudentFileLog(logsDir, studentId)
                .append(EventType.RETURN, isbn, book.getTitle());

        return true;
    }

    // Called when application closes (from MainApp.stop)
    public void shutdown() {
        executor.shutdown();
    }
}
