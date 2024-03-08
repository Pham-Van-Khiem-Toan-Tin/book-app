package com.book.app.Controller;

import com.book.app.Dao.impl.UserImpl;
import com.book.app.Entity.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class NewUserController implements Initializable {
    @FXML
    private TextField username, email, phone, address;
    @FXML
    private PasswordField password;
    private TableView<User> tableView;


    private Dialog<String> dialog;

    public Dialog<String> getDialog() {
        return dialog;
    }
    public TableView<User> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<User> tableView) {
        this.tableView = tableView;
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    @FXML
    private Button submitButton, cancel;
    private UserImpl dao = new UserImpl();
    @FXML
    public void submit(ActionEvent event) throws IOException {
            User newUser = new User();
            newUser.setUsername(username.getText().trim());
            newUser.setEmail(email.getText().trim());
            newUser.setPhone(phone.getText().trim());
            newUser.setAddress(address.getText().trim());
            newUser.setPassword(password.getText().trim());
            boolean result = dao.addUser(newUser);
            if (result) {
                this.dialog.setResult("successfully");
                this.dialog.close();
                tableView.setItems(FXCollections.observableArrayList(dao.getAllUser()));
            }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UnaryOperator<TextFormatter.Change> phoneFilter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> phoneFormatter = new TextFormatter<>(phoneFilter);
        phone.setTextFormatter(phoneFormatter);
        UnaryOperator<TextFormatter.Change> usernameFilter = change -> {
            String text = change.getControlNewText();
            if (text.isEmpty() || text.matches("^[a-zA-Z0-9]*$")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> usernameFormatter = new TextFormatter<>(usernameFilter);
        username.setTextFormatter(usernameFormatter);
    }
}
