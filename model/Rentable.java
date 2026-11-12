package FinalProject.model;
//2431250 Milda Gagnon
// PDF Requirement 2.1: Rentable interface must define these 3 methods.
public interface Rentable {
    boolean isAvailable();  // PDF Requirement 3.3: Check if book can be rented
    void checkout();        // reduce available
    void checkin();         // increase available
}
