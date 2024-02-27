module com.book.app {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.book.app to javafx.fxml;
    exports com.book.app;
    exports com.book.app.controller;
    opens com.book.app.controller to javafx.fxml;
}