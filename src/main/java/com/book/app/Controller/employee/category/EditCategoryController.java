package com.book.app.Controller.employee.category;

import com.book.app.Config.ImageUploader;
import com.book.app.Dao.impl.CategoryDaoImpl;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.CategoryEntity;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditCategoryController implements Initializable {
    @FXML
    Button submitButton, cancel;

    @FXML
    TextField categoryName, description;

    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private TableView<CategoryEntity> tableView;
    private CategoryEntity oldData;
    private CategoryDaoImpl dao = new CategoryDaoImpl();

    public String getOldSort() {
        return oldSort;
    }

    public void setOldSort(String oldSort) {
        this.oldSort = oldSort;
    }

    public String getOldSearch() {
        return oldSearch;
    }

    public void setOldSearch(String oldSearch) {
        this.oldSearch = oldSearch;
    }

    public Dialog<String> getDialog() {
        return dialog;
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public TableView<CategoryEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<CategoryEntity> tableView) {
        this.tableView = tableView;
    }

    public CategoryEntity getOldData() {
        return oldData;
    }

    public void setOldData(CategoryEntity oldData) {
        this.oldData = oldData;
        categoryName.setText(this.oldData.getName());
        description.setText(this.oldData.getDescription());
    }
    private boolean validateFields() {
        return categoryName.getText() != null && !categoryName.getText().trim().isEmpty() &&
                description.getText() != null && !description.getText().trim().isEmpty();
    }
    private boolean checkOldData() {
        if (!Objects.isNull(oldData)) {
            String newCategoryrName = Objects.requireNonNullElse(categoryName.getText(), "");
            String newDescription = Objects.requireNonNullElse(description.getText(), "");
            boolean categoryNameSameAsOldData = newCategoryrName.trim().equals(oldData.getName());
            boolean descriptionSameAsOldData = newDescription.trim().equals(oldData.getDescription());
            return !(categoryNameSameAsOldData && descriptionSameAsOldData);
        } else {
            return true;
        }
    };
    private void checkFields() {
        submitButton.setDisable(!checkOldData() || !validateFields());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.setOnAction(event -> {
            try {
                submit(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        categoryName.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        description.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
    }
    public void submit(ActionEvent event) throws IOException {
        String name = categoryName.getText().trim();
        String descriptionText = description.getText().trim();
        CategoryEntity category = new CategoryEntity();
        category.setId(this.oldData.getId());
        category.setName(name);
        category.setDescription(descriptionText);
        category.setUpdated_at(LocalDateTime.now());
        boolean result = dao.editCategory(category);
        if (result) {
            this.dialog.setResult("successfully");
            this.dialog.close();
            tableView.setItems(FXCollections.observableArrayList(dao.getAllCategory(oldSearch, oldSort)));
        }
    }
}
