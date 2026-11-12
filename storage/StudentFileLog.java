package FinalProject.storage;

import FinalProject.model.EventType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
//2431250 Milda Gagnon
// PDF Requirement 3.3: Each student must have logs/<id>.bin as binary file.
/**
 * Manages the log file for a single student.
 * Each student has logs/<studentId>.bin.
 * Every call to append(...) adds ONE log entry.
 */
public class StudentFileLog {

    // Path to ONE student's log file, for example logs/S1001.bin
    private Path logFile;

    // Constructor: creates the file path logs/<studentId>.bin
    public StudentFileLog(Path logsDir, String studentId) {
        this.logFile = logsDir.resolve(studentId + ".bin");
    }

    // PDF Requirement 3.3: binary log frame = timestamp, event type, isbn, title.

    // log borrowing/returning activities?
    // Each student has a binary file logs/<id>.bin.

    /**
     * Append ONE log entry to the student's binary log file.
     * Format stored:
     *    [timestamp][eventType][isbn][title]
     */
    public void append(EventType type, String isbn, String title) {
        try {
            // Make sure logs directory exists (creates folder if missing)
            Files.createDirectories(logFile.getParent());

            // Open the file in APPEND mode using DtaOutputStream
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(logFile.toFile(), true)))) {

                // Time when the event happened
                out.writeLong(System.currentTimeMillis()); // <-- timestamp
                // Event type as a small integer (0 = RENT, 1 = RETURN)
                out.writeInt(type.ordinal()); // <-- RENT or RETURN
                // Book information
                out.writeUTF(isbn); // <-- Book ISBN
                out.writeUTF(title); // <-- Book title
            }
        } catch (Exception e) {
            // Silently ignore for simple student project
        }
    }

    // Read the whole file and convert each record to a pretty string
    // Example line: "2025-11-27 10:59:39 - RENT - Clean Code (978-0132350884)"
    public List<String> readAllPretty() {
        List<String> list = new ArrayList<>();
        // If there is no log file yet, return an empty list
        if (!Files.exists(logFile)) return list;

        // Formatter for a readable date and time
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (DataInputStream in =
                     new DataInputStream(new BufferedInputStream(Files.newInputStream(logFile)))) {

            // Keep reading until we reach end of file (EOFException)
            while (true) {
                long ts = in.readLong();   // timestamp
                int ord = in.readInt();    // event type index
                String isbn = in.readUTF();
                String title = in.readUTF();

                // Convert timestamp to local date-time string
                 String date = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .format(fmt);

                // Build final line and add to list
                list.add(date + " - " + EventType.values()[ord] + " - " + title + " (" + isbn + ")");
            }

        } catch (EOFException end) {
            // Normal end of file – do nothing
        } catch (Exception e) {
            // Ignore other errors
        }

        return list;
    }
}
