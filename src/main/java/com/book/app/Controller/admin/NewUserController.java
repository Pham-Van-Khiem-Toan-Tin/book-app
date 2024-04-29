package com.book.app.Controller.admin;

import com.book.app.Dao.impl.EmployeeDaoImpl;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.SearchUtils;
import com.book.app.Utils.SortUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class NewUserController implements Initializable {
    @FXML
    private TextField username, email, phone, address;
    @FXML
    private PasswordField password;
    private TableView<EmployeeEntity> tableView;
    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    public String getOldSearch() {
        return oldSearch;
    }
    public void setOldSearch(String oldSearch) {
        this.oldSearch = oldSearch;
    }

    public String getOldSort() {
        return oldSort;
    }

    public void setOldSort(String oldSort) {
        this.oldSort = oldSort;
    }

    public Dialog<String> getDialog() {
        return dialog;
    }
    public TableView<EmployeeEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<EmployeeEntity> tableView) {
        this.tableView = tableView;
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    @FXML
    private Button submitButton, cancel;
    private EmployeeDaoImpl dao = new EmployeeDaoImpl();
    @FXML
    public void submit(ActionEvent event) throws IOException {
            EmployeeEntity newUser = new EmployeeEntity();
            newUser.setUsername(username.getText().trim());
            newUser.setEmail(email.getText().trim());
            newUser.setPhone(phone.getText().trim());
            newUser.setAddress(address.getText().trim());
            newUser.setPassword(password.getText().trim());
            boolean result = dao.addEmployee(newUser);
            if (result) {
                this.dialog.setResult("successfully");
                this.dialog.close();
                tableView.setItems(SortUtils.getSortList(oldSort, SearchUtils.getAllUserOldSearch(oldSearch)));
            }
    }
    private boolean validateFields() {
        return username.getText() != null && !username.getText().trim().isEmpty() &&
                email.getText() != null && !email.getText().trim().isEmpty() &&
                phone.getText() != null && !phone.getText().trim().isEmpty() &&
                address.getText() != null && !address.getText().trim().isEmpty() &&
                password.getText() != null && !password.getText().trim().isEmpty();
    }
    private void checkFields() {
        submitButton.setDisable(!validateFields());
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
        username.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        address.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        email.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        phone.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        password.textProperty().addListener((observable, oldValue, newValue) -> checkFields());

        checkFields();
    }
}
