package FinalProject.model;

import java.io.Serializable;
//2431250 Milda Gagnon
// PDF Requirement 2.1: Item is abstract base for Book.
public abstract class Item implements Serializable {

    // PDF Requirement 3.2: Book needs ISBN + Title
    protected String isbn;
    protected String title;

    public Item(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
}
