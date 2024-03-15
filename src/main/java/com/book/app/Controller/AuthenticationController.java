package com.book.app.Controller;

import com.book.app.Dao.impl.EmployeeDaoImpl;
import com.book.app.Entity.EmployeeEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthenticationController implements Initializable {
    @FXML
    private Label userNameLogin, passwordLogin;
    @FXML
    private TextField inputNameLogin;
    @FXML
    private PasswordField inputPasswordLogin;
    @FXML
    private Button submitButton;
    private EmployeeDaoImpl dao = new EmployeeDaoImpl();
    private String root = "/com/book/app/";
    private boolean validateFields() {
        return inputNameLogin.getText() != null && !inputNameLogin.getText().trim().isEmpty() &&
                inputPasswordLogin.getText() != null && !inputPasswordLogin.getText().trim().isEmpty();
    }
    private void checkFields() {
        submitButton.setDisable(!validateFields());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameLogin.setOnMouseClicked(mouseEvent -> {
            inputNameLogin.requestFocus();
        });
        passwordLogin.setOnMouseClicked(mouseEvent -> {
            inputPasswordLogin.requestFocus();
        });
        submitButton.setOnAction(event -> {
            login(event);
            System.out.println("chay vao day");
        });
        inputNameLogin.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        inputPasswordLogin.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        checkFields();
    }
    private void login(ActionEvent event) {
        EmployeeEntity user = dao.login(inputNameLogin.getText().trim(), inputPasswordLogin.getText().trim());
        if (user != null) {
            String fxmFile, cssFile;
            fxmFile = root + (user.getAdmin() ? "admin/user-management.fxml": "home/home.fxml");
            cssFile = root + (user.getAdmin() ? "static/css/user-management.css": "static/css/home.css");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmFile));
                System.out.println(getClass().getResource(""));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1280, 800);
                stage.setResizable(false);
                scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {

        }
    }
}
