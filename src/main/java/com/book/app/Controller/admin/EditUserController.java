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
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class EditUserController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    private TextField username, email, phone, address;
    @FXML
    private ComboBox<String> role;
    @FXML
    private Button submit;
    private EmployeeEntity oldData;
    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private TableView<EmployeeEntity> tableView;
    private EmployeeDaoImpl dao = new EmployeeDaoImpl();

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

    public TableView<EmployeeEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<EmployeeEntity> tableView) {
        this.tableView = tableView;
    }

    public Dialog<String> getDialog() {
        return dialog;
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    public EmployeeEntity getOldData() {
        return oldData;
    }

    private boolean validateFields() {
        return username.getText() != null && !username.getText().trim().isEmpty() &&
                email.getText() != null && !email.getText().trim().isEmpty() &&
                phone.getText() != null && !phone.getText().trim().isEmpty() &&
                address.getText() != null && !address.getText().trim().isEmpty();
    }

    private boolean checkOldData() {
        if (!Objects.isNull(oldData)) {
        String newUsername = Objects.requireNonNullElse(username.getText(), "");
        String newEmail = Objects.requireNonNullElse(email.getText(), "");
        String newPhone = Objects.requireNonNullElse(phone.getText(), "");
        String newAddress = Objects.requireNonNullElse(address.getText(), "");
        String newRole = role.getValue() != null ? (role.getValue().equals("ADMIN") ? "ADMIN" : "USER") : null;

        boolean usernameSameAsOldData = newUsername.trim().equals(oldData.getUsername());
        boolean emailSameAsOldData = newEmail.trim().equals(oldData.getEmail());
        boolean phoneSameAsOldData = newPhone.trim().equals(oldData.getPhone());
        boolean addressSameAsOldData = newAddress.trim().equals(oldData.getAddress());
        boolean roleSameAsOldData = (newRole != null && newRole.equals(oldData.getAdmin() ? "ADMIN" : "USER"));

        return !(usernameSameAsOldData && emailSameAsOldData && phoneSameAsOldData && addressSameAsOldData && roleSameAsOldData);
        } else {
            return true;
        }
    }

    private void checkFields() {
        submit.setDisable(!checkOldData() || !validateFields());
    }

    public void setOldData(EmployeeEntity oldData) {
        this.oldData = oldData;
        username.setText(this.oldData.getUsername());
        email.setText(this.oldData.getEmail());
        phone.setText(this.oldData.getPhone());
        address.setText(this.oldData.getAddress());
        role.setValue(this.oldData.getAdmin() ? "ADMIN" : "USER");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        role.getItems().addAll("ADMIN", "USER");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        phone.setTextFormatter(textFormatter);
        username.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        address.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        email.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        phone.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        role.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkFields());
        checkFields();
    }

    @FXML
    public void submitEdit(ActionEvent event) throws IOException {
        EmployeeEntity newUser = new EmployeeEntity();
        newUser.setUsername(username.getText().trim());
        newUser.setEmail(email.getText().trim());
        newUser.setPhone(phone.getText().trim());
        newUser.setAddress(address.getText().trim());
        newUser.setAdmin(role.getValue().equals("ADMIN") ? true : false);
        newUser.setId(oldData.getId());
        newUser.setUpdatedAt(LocalDate.now());
        boolean resultEdit = dao.editEmployee(newUser);

        if (resultEdit) {
            this.dialog.setResult("submit");
            this.dialog.close();
        }
        tableView.setItems(SortUtils.getSortList(oldSort,SearchUtils.getAllUserOldSearch(oldSearch)));
    }
}
