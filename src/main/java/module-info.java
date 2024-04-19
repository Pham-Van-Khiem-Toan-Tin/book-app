module com.book.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;
    requires cloudinary.core;
    requires dotenv.java;

    opens com.book.app to javafx.fxml;
    exports com.book.app;
    exports com.book.app.Controller;
    opens com.book.app.Controller to javafx.fxml;
    opens com.book.app.Entity to javafx.base;
//  admin
    exports com.book.app.Controller.admin;
    opens com.book.app.Controller.admin to javafx.fxml;
//  employee
    exports com.book.app.Controller.employee.author;
    opens com.book.app.Controller.employee.author to javafx.fxml;
    exports com.book.app.Controller.employee.category;
    opens com.book.app.Controller.employee.category to javafx.fxml;
    exports com.book.app.Controller.employee.publisher;
    opens com.book.app.Controller.employee.publisher to javafx.fxml;
    exports com.book.app.Controller.employee.book;
    opens com.book.app.Controller.employee.book to javafx.fxml;
}