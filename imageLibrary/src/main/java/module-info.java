module com.example.imagelibrary {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.swing;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;

    opens com.example.imagelibrary to javafx.fxml;
    exports com.example.imagelibrary;
}