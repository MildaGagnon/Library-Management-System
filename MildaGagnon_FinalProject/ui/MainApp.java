package FinalProject.ui;

import FinalProject.model.Book;
import FinalProject.model.Student;
import FinalProject.service.LibraryService;
import FinalProject.storage.StudentFileLog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.nio.file.Paths;
//2431250 Milda Gagnon

// Main JavaFX Application class
// PDF: "GUI application using JavaFX" requirement
public class MainApp extends Application {

    // PDF: Service layer object – connects UI to storage (students, books, logs)
    private LibraryService service;

    // Observable lists are used by tables / list views and update the UI automatically
    private ObservableList<Student> studentData = FXCollections.observableArrayList();
    private ObservableList<Book> bookData = FXCollections.observableArrayList();
    private ObservableList<String> logData = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        // PDF: data is stored under a "data" directory
        // Create the service and load students + books from files
        service = new LibraryService(Paths.get("data"));
        refreshStudents(); // fill studentData list
        refreshBooks();    // fill bookData list

        // Main layout of the window: a BorderPane
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar()); // top: menu bar (File, Student, Book, Rental)
        root.setCenter(createTabs()); // center: tab pane (3 tabs)

        // Create scene and show main window
        Scene scene = new Scene(root, 900, 500);
        stage.setScene(scene);
        stage.setTitle("Campus Library Management System");
        stage.show();
    }

    // ========== MENU BAR ===========
    // PDF: requirement "Menu bar with File, Student, Book, Rental"
    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();

        // ---------- File menu ----------
        Menu fileMenu = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            // PDF: clean shutdown of service when closing program
            service.shutdown();
            Platform.exit();
        });
        fileMenu.getItems().add(exit);

        // ---------- Student menu ----------
        // PDF: requirement "Student → Add Student"
        Menu studentMenu = new Menu("Student");
        MenuItem addStudent = new MenuItem("Add Student");
        addStudent.setOnAction(e -> showAddStudentDialog()); // opens dialog window
        studentMenu.getItems().add(addStudent);

        // ---------- Book menu ----------
        // PDF: requirement "Book → Add Book"
        Menu bookMenu = new Menu("Book");
        MenuItem addBook = new MenuItem("Add Book");
        addBook.setOnAction(e -> showAddBookDialog()); // opens dialog window
        bookMenu.getItems().add(addBook);

        // ---------- Rental menu ----------
        // PDF: requirement "Rental → Rent Book / Return Book"
        Menu rentalMenu = new Menu("Rental");
        MenuItem rent = new MenuItem("Rent Book");
        rent.setOnAction(e -> showRentDialog()); // opens rent dialog
        MenuItem ret = new MenuItem("Return Book");
        ret.setOnAction(e -> showReturnDialog()); // opens return dialog
        rentalMenu.getItems().addAll(rent, ret);

        // Add all menus to the menu bar
        bar.getMenus().addAll(fileMenu, studentMenu, bookMenu, rentalMenu);
        return bar;
    }

    // ========= TABS ==========
    // PDF: requirement "Three tabs: Student Records, Book Catalog, Student Logs"
    private TabPane createTabs() {
        TabPane pane = new TabPane();

        // Student Records tab (students table)
        Tab t1 = new Tab("Student Records", createStudentTab());
        t1.setClosable(false); // user cannot close this tab

        // Book Catalog tab (books table)
        Tab t2 = new Tab("Book Catalog", createBookTab());
        t2.setClosable(false);

        // Student Logs tab (split pane: students on left, logs on right)
        Tab t3 = new Tab("Student Logs", createLogsTab());
        t3.setClosable(false);

        pane.getTabs().addAll(t1, t2, t3);
        return pane;
    }

    // ========= STUDENT TAB ==========
    // PDF: requirement "Display all students and allow adding new ones"
    private BorderPane createStudentTab() {
        BorderPane pane = new BorderPane();

        // Table that shows the list of students
        TableView<Student> table = new TableView<>(studentData);

        // Column for student ID
        TableColumn<Student, String> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Column for student name
        TableColumn<Student, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Column for program (ex. CS)
        TableColumn<Student, String> prog = new TableColumn<>("Program");
        prog.setCellValueFactory(new PropertyValueFactory<>("program"));

        // Column for year (ex. 2)
        TableColumn<Student, Integer> year = new TableColumn<>("Year");
        year.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Add columns to the table and show table in the center
        table.getColumns().addAll(id, name, prog, year);
        pane.setCenter(table);

        // ------- Bottom quick-add form (same info as Add Student dialog) -------
        TextField f1 = new TextField();
        f1.setPromptText("ID"); // user hint

        TextField f2 = new TextField();
        f2.setPromptText("Name");

        TextField f3 = new TextField();
        f3.setPromptText("Program");

        TextField f4 = new TextField();
        f4.setPromptText("Year");

        Button add = new Button("Add Student");
        add.setOnAction(e -> {
            try {
                // Create new Student from the text fields
                Student s = new Student(
                        f1.getText(), f2.getText(), f3.getText(),
                        Integer.parseInt(f4.getText())
                );
                // Ask the service / registry to add or update this student
                service.getRegistry().addOrUpdateStudent(s);
                refreshStudents(); // reload data into table
                // Clear input fields after adding
                f1.clear(); f2.clear(); f3.clear(); f4.clear();
            } catch (Exception ex) {
                // If year is not a number, we show an error popup
                showAlert("Invalid year");
            }
        });

        // Put the fields and button in a horizontal bar at the bottom
        HBox bottom = new HBox(5, f1, f2, f3, f4, add);
        bottom.setPadding(new Insets(5));
        pane.setBottom(bottom);

        return pane;
    }

    // ========= BOOK TAB ==========
    // PDF: requirement "Display book catalog and allow adding books"
    private BorderPane createBookTab() {
        BorderPane pane = new BorderPane();

        // Table that shows list of books
        TableView<Book> table = new TableView<>(bookData);

        // Column: ISBN
        TableColumn<Book, String> isbn = new TableColumn<>("ISBN");
        isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        // Column: title
        TableColumn<Book, String> title = new TableColumn<>("Title");
        title.setCellValueFactory(new PropertyValueFactory<>("title"));

        // Column: total copies in library
        TableColumn<Book, Integer> total = new TableColumn<>("Total");
        total.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));

        // Column: how many copies are currently checked out
        TableColumn<Book, Integer> out = new TableColumn<>("Checked Out");
        out.setCellValueFactory(new PropertyValueFactory<>("checkedOut"));

        // Column: available copies (calculated from Book.getAvailable)
        TableColumn<Book, Integer> avail = new TableColumn<>("Available");
        avail.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Show all book columns in table
        table.getColumns().addAll(isbn, title, total, out, avail);
        pane.setCenter(table);

        // ------- Bottom quick-add form for books -------
        TextField f1 = new TextField();
        f1.setPromptText("ISBN");

        TextField f2 = new TextField();
        f2.setPromptText("Title");

        TextField f3 = new TextField();
        f3.setPromptText("Copies"); // total number of copies

        Button add = new Button("Add Book");
        add.setOnAction(e -> {
            try {
                // Create new Book from the text fields
                Book b = new Book(
                        f1.getText(), f2.getText(),
                        Integer.parseInt(f3.getText())
                );
                // Ask catalog to save this book
                service.getCatalog().add(b);
                refreshBooks(); // reload book table
                f1.clear(); f2.clear(); f3.clear();
            } catch (Exception ex) {
                showAlert("Invalid copies");
            }
        });

        HBox bottom = new HBox(5, f1, f2, f3, add);
        bottom.setPadding(new Insets(5));
        pane.setBottom(bottom);

        return pane;
    }

    // ===================== STUDENT LOGS TAB =====================
    // PDF: requirement "Student Logs tab uses a SplitPane: students on left, log lines on right"
    private SplitPane createLogsTab() {

        // List of students on the left side
        ListView<Student> studentList = new ListView<>(studentData);

        // List of log lines (strings) on the right side
        ListView<String> logList = new ListView<>(logData);

        // When the selected student changes...
        studentList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldV, newV) -> {
                    logData.clear(); // clear previous log
                    if (newV != null) {
                        // For the chosen student, open his/her log file
                        // PDF: requirement "Each student has their own binary log file"
                        StudentFileLog log = new StudentFileLog(Paths.get("data/logs"), newV.getId());
                        // readAllPretty() returns lines like "DATE - RENT - Title (ISBN)"
                        logData.addAll(log.readAllPretty()); // show in the right list view
                    }
                });

        // SplitPane automatically shows students on left and logs on right
        return new SplitPane(studentList, logList);
    }

    // ========= DIALOGS ==========
    // These dialogs provide the same operations as the bottom forms,
    // but they are attached to the menu items (Student / Book / Rental).

    // ---- Dialog to add a student (Student → Add Student) ----
    private void showAddStudentDialog() {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Add Student");

        TextField id = new TextField();
        id.setPromptText("ID");

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField prog = new TextField();
        prog.setPromptText("Program");

        TextField year = new TextField();
        year.setPromptText("Year");

        HBox box = new HBox(10, id, name, prog, year);
        box.setPadding(new Insets(10));
        d.getDialogPane().setContent(box);

        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    // Build Student object from inputs and save it
                    Student s = new Student(
                            id.getText(), name.getText(),
                            prog.getText(), Integer.parseInt(year.getText())
                    );
                    service.getRegistry().addOrUpdateStudent(s);
                    refreshStudents();
                } catch (Exception ex) {
                    showAlert("Invalid input");
                }
            }
            return null; // dialog does not return any real value
        });

        d.showAndWait();
    }

    // ---- Dialog to add a book (Book → Add Book) ----
    private void showAddBookDialog() {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Add Book");

        TextField isbn = new TextField();
        isbn.setPromptText("ISBN");

        TextField title = new TextField();
        title.setPromptText("Title");

        TextField copies = new TextField();
        copies.setPromptText("Copies");

        HBox box = new HBox(10, isbn, title, copies);
        box.setPadding(new Insets(10));
        d.getDialogPane().setContent(box);

        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    // Build Book from inputs and save it in catalog
                    Book b = new Book(
                            isbn.getText(), title.getText(),
                            Integer.parseInt(copies.getText())
                    );
                    service.getCatalog().add(b);
                    refreshBooks();
                } catch (Exception ex) {
                    showAlert("Invalid input");
                }
            }
            return null;
        });

        d.showAndWait();
    }

    // ---- Dialog to rent a book (Rental → Rent Book) ----
    private void showRentDialog() {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Rent Book");

        TextField id = new TextField();
        id.setPromptText("Student ID");

        TextField isbn = new TextField();
        isbn.setPromptText("Book ISBN");

        HBox box = new HBox(10, id, isbn);
        box.setPadding(new Insets(10));
        d.getDialogPane().setContent(box);

        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                // Ask service to perform rent operation
                boolean ok = service.rentBook(id.getText(), isbn.getText());
                if (ok) {
                    refreshBooks(); // update "Checked Out" and "Available" columns
                    showAlert("Book rented successfully");
                } else {
                    showAlert("Rent failed");
                }
            }
            return null;
        });

        d.showAndWait();
    }

    // ---- Dialog to return a book (Rental → Return Book) ----
    private void showReturnDialog() {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Return Book");

        TextField id = new TextField();
        id.setPromptText("Student ID");

        TextField isbn = new TextField();
        isbn.setPromptText("Book ISBN");

        HBox box = new HBox(10, id, isbn);
        box.setPadding(new Insets(10));
        d.getDialogPane().setContent(box);

        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                // Ask service to perform return operation
                boolean ok = service.returnBook(id.getText(), isbn.getText());
                if (ok) {
                    refreshBooks(); // update inventory in table
                    showAlert("Book returned successfully");
                } else {
                    showAlert("Return failed");
                }
            }
            return null;
        });

        d.showAndWait();
    }

    // Reload table data from the service into the observable lists
    private void refreshStudents() {
        studentData.setAll(service.getRegistry().listAll());
    }

    private void refreshBooks() {
        bookData.setAll(service.getCatalog().all());
    }

    // Small helper to show information/error messages
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    @Override
    public void stop() {
        // Called when the application is closing
        service.shutdown();
    }

    public static void main(String[] args) {
        launch(args); // start JavaFX application
    }
}
