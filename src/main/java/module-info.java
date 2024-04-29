module com.book.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;

    opens com.book.app to javafx.fxml;
    exports com.book.app;
    exports com.book.app.Controller;
    opens com.book.app.Controller to javafx.fxml;
    opens com.book.app.Entity to javafx.base;
    exports com.book.app.Controller.admin;
    opens com.book.app.Controller.admin to javafx.fxml;
}