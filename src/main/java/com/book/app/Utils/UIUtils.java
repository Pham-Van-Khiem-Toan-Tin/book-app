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
    public  static void setupMenuEmployee(Button btnAuthor, Button btnCategory, Button btnPublisher, Button btnHome, Button btnBook) {
        btnBook.setOnAction(event -> {
            try {
                handleSwitchOtherScene(event, "book/book.fxml", "static/css/book/books.css");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnAuthor.setOnAction(event -> {
            try {
                handleSwitchOtherScene(event, "author/authors.fxml", "static/css/author/authors.css");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnPublisher.setOnAction(event -> {
            try {
                handleSwitchOtherScene(event, "publisher/publisher.fxml", "static/css/publisher/publisher.css");
            } catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException();
            }
        });
        btnCategory.setOnAction(event -> {
            try {
                handleSwitchOtherScene(event, "category/category.fxml", "static/css/category/category.css");
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
        btnHome.setOnAction(event -> {
            try {
                handleSwitchOtherScene(event, "home/home.fxml", "static/css/home.css");
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
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
