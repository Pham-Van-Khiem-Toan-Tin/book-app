package com.book.app.Controller.admin;

import com.book.app.Dao.impl.EmployeeDaoImpl;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.PasswordUtils;
import com.book.app.Utils.SearchUtils;
import com.book.app.Utils.SortUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.ResourceBundle;

public class ResetPasswordController implements Initializable {
    @FXML
    private PasswordField newPassword, confirmPassword;
    @FXML
    private Button submitBtn;
    private String oldData;
    private int userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public Dialog<String> getDialog() {
        return dialog;
    }
    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }
    public TableView<EmployeeEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<EmployeeEntity> tableView) {
        this.tableView = tableView;
    }
    private boolean validateFields() {
        return newPassword.getText() != null && !newPassword.getText().trim().isEmpty() &&
                confirmPassword.getText() != null && !confirmPassword.getText().trim().isEmpty();
    }
    private boolean checkOldData() throws NoSuchAlgorithmException {
        if (!Objects.isNull(oldData)) {
            String newPass = Objects.requireNonNullElse(newPassword.getText(), "");
            String confirmPass = Objects.requireNonNullElse(confirmPassword.getText(), "");


            boolean newPassSameAsOldData = PasswordUtils.checkPassword(newPass.trim(),oldData);
            boolean confirmPassSameAsOldData = PasswordUtils.checkPassword(confirmPass.trim(),oldData);;
            boolean checkMatchNewPass = newPass.trim().equals(confirmPass);


            return !(newPassSameAsOldData && confirmPassSameAsOldData && checkMatchNewPass);
        } else {
            return true;
        }
    }
    private boolean checkComparePassword() {
        if (validateFields()) {
            return newPassword.getText().trim().equals(confirmPassword.getText().trim());
        }
        return false;
    }
    private void checkFields() throws NoSuchAlgorithmException {
        submitBtn.setDisable(!checkOldData() || !validateFields() || checkComparePassword());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9]*$")) {
                newPassword.setText(oldValue);
            }
            try {
                checkFields();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9]*$")) {
                confirmPassword.setText(oldValue);
            }
            try {
                checkFields();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            checkFields();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void submit(ActionEvent event) throws IOException, NoSuchAlgorithmException {
        boolean result = dao.resetPassword(userId, PasswordUtils.hashPassword(newPassword.getText().trim()));
        if (result) {
            this.dialog.setResult("successfully");
            this.dialog.close();
            tableView.setItems(SortUtils.getSortList(oldSort, SearchUtils.getAllUserOldSearch(oldSearch)));
        }
    }
}
