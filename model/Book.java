package FinalProject.model;

import java.io.Serializable;
//2431250 Milda Gagnon
// PDF Requirement 2: Book extends Item & implements Rentable
public class Book extends Item implements Rentable, Serializable {

    private int totalCopies;
    private int checkedOut;

    public Book(String isbn, String title, int totalCopies) {
        super(isbn, title);
        this.totalCopies = totalCopies;
        this.checkedOut = 0;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getCheckedOut() {
        return checkedOut;
    }

    public int getAvailable() {
        return totalCopies - checkedOut; // PDF Requirement: Available column
    }

    @Override
    public boolean isAvailable() {
        return getAvailable() > 0;
    }

    @Override
    public void checkout() {
        if (isAvailable()) {
            checkedOut++;
        }
    }

    @Override
    public void checkin() {
        if (checkedOut > 0) {
            checkedOut--;
        }
    }

    @Override
    public String toString() {
        return isbn + " - " + title;
    }
}
