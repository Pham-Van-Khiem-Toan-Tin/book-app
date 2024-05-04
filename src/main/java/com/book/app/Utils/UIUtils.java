package com.book.app.Utils;

import com.book.app.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class UIUtils {
    private static String rootDirectory = "/com/book/app/";

    public static void setupUIElements(Text textWelcome, Text textUsername, ComboBox<String> choiceBoxLogout) {
        textWelcome.setText("Hello, " + AppUtils.getUsername());
        textUsername.setText(AppUtils.getUsername());
        choiceBoxLogout.getItems().add("Log out");
        choiceBoxLogout.setOnAction(event -> {
            if (choiceBoxLogout.getValue().equals("Log out")) {
                AppUtils.clearData();
                try {
                    handleLogout(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public  static void setupMenuEmployee(Button... buttons) {
        for (Button button: buttons) {
            String directoryClass = null, directoryCss = null;
            switch (button.getText()) {
                case "Home":
                    directoryClass = "home/home.fxml";
                    directoryCss = "static/css/home.css";
                    break;
                case "Book":
                    directoryClass = "book/book.fxml";
                    directoryCss = "static/css/book/books.css";
                    break;
                case "Category":
                    directoryClass = "category/category.fxml";
                    directoryCss = "static/css/category/category.css";
                    break;
                case "Author":
                    directoryClass = "author/authors.fxml";
                    directoryCss = "static/css/author/authors.css";
                    break;
                case "Publisher":
                    directoryClass = "publisher/publisher.fxml";
                    directoryCss = "static/css/publisher/publisher.css";
                    break;
                case "Inventory":
                    directoryClass = "inventory/inventory.fxml";
                    directoryCss = "static/css/inventory/inventory.css";
                    break;
                case "Order":
                    directoryClass = "order/order.fxml";
                    directoryCss = "static/css/order/order.css";
                    break;
                case "User Management":
                    directoryClass = "admin/user-management.fxml";
                    directoryCss = "static/css/user-management.css";
                    break;
                case "Sale Book":
                    directoryClass = "admin/sale/sale-book-management.fxml";
                    directoryCss = "static/css/sale/sale-book-management.css";
                    break;
                case "Sale Category":
                    directoryClass = "admin/sale/sale-category.fxml";
                    directoryCss = "static/css/sale/sale-book-management.css";
                    break;
                case "Sale Author":
                    directoryClass = "admin/sale/sale-author.fxml";
                    directoryCss = "static/css/sale/sale-book-management.css";
                    break;
                case "Sale Customer":
                    directoryClass = "admin/sale/sale-customer.fxml";
                    directoryCss = "static/css/sale/sale-book-management.css";
                    break;
                case "Sale Employee":
                    directoryClass = "admin/sale/sale-employee.fxml";
                    directoryCss = "static/css/sale/sale-book-management.css";
                    break;
                case "Sale Publisher":
                    directoryClass = "admin/sale/sale-publisher.fxml";
                    directoryCss = "static/css/sale/sale-book-management.css";
                    break;

            }
            String finalDirectoryClass = directoryClass;
            String finalDirectoryCss = directoryCss;
            button.setOnAction(event -> {
                try {
                    handleSwitchOtherScene(event, finalDirectoryClass, finalDirectoryCss);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }
    public static void handleSwitchOtherScene(ActionEvent event, String directoryClass, String directoryCss) throws IOException {
        try {

            // Sử dụng đường dẫn tương đối để lấy URL của tệp FXML
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(directoryClass));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            if (directoryCss != null) {
                scene.getStylesheets().add(MainApplication.class.getResource(rootDirectory + directoryCss).toExternalForm());
            }
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void handleLogout(ActionEvent event) throws IOException {
        try {
            TokenUtil.deleteToken();
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(rootDirectory + "login/authen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
