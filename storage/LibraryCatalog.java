package FinalProject.storage;

import FinalProject.model.Book;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
//2431250 Milda Gagnon
// PDF Requirement 3.2: Must save book catalog in a binary file catalog.dat.
public class LibraryCatalog {

    // Path to catalog.dat
    private Path dataFile;
    // Map ISBN -> Book object kept in memory
    private Map<String, Book> byIsbn = new HashMap<>();

    // Constructor: points dataFile to catalog.dat and loads it
    public LibraryCatalog(Path baseDir) {
        dataFile = baseDir.resolve("catalog.dat");
        loadCatalog();
    }

    // Load the catalog from the file into the map
    private void loadCatalog() { // <-- Reads saved books from disk at startup
        // If file does not exist, we keep an empty catalog
        if (!Files.exists(dataFile)) return;

        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(dataFile))) {
            // File contains List<Book> saved with ObjectOutputStream
            List<Book> list = (List<Book>) in.readObject();
            for (Book b : list) {
                byIsbn.put(b.getIsbn(), b); // rebuild map using ISBN as key
            }
        } catch (Exception e) {
            // Ignore errors for now
        }
    }

    // PDF Requirement 3.2: Save the catalog in catalog.dat (binary)
    public void saveCatalog() { // <-- Writes all books to disk after add/update
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(dataFile))) {
            // We save only the values (list of books)
            out.writeObject(new ArrayList<>(byIsbn.values()));
        } catch (Exception e) {
            // Ignore errors
        }
    }

    // Add or replace a book using its ISBN, then save to file
    public void add(Book b) {
        byIsbn.put(b.getIsbn(), b);
        saveCatalog();
    }

    // Get ONE book by its ISBN (returns null if not found)
    public Book get(String isbn) {
        return byIsbn.get(isbn);
    }

    // Return a copy of all books – used to fill the table in the GUI
    public Collection<Book> all() {
        return new ArrayList<>(byIsbn.values());
    }
}
