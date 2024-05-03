package com.book.app.Controller.employee.publisher;

import com.book.app.Dao.impl.PublisherDaoImpl;
import com.book.app.Entity.PublisherEntity;
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

public class EditPublisherController implements Initializable {
    @FXML
    Button submitButton, cancel;

    @FXML
    TextField publisherName, description;

    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private TableView<PublisherEntity> tableView;
    private PublisherEntity oldData;
    private PublisherDaoImpl dao = new PublisherDaoImpl();

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

    public TableView<PublisherEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<PublisherEntity> tableView) {
        this.tableView = tableView;
    }

    public PublisherEntity getOldData() {
        return oldData;
    }

    public void setOldData(PublisherEntity oldData) {
        this.oldData = oldData;
        publisherName.setText(this.oldData.getName());
        description.setText(this.oldData.getDescription());
    }
    private boolean validateFields() {
        return publisherName.getText() != null && !publisherName.getText().trim().isEmpty() &&
                description.getText() != null && !description.getText().trim().isEmpty();
    }
    private boolean checkOldData() {
        if (!Objects.isNull(oldData)) {
            String newPublisherName = Objects.requireNonNullElse(publisherName.getText(), "");
            String newDescription = Objects.requireNonNullElse(description.getText(), "");
            boolean publisherNameSameAsOldData = newPublisherName.trim().equals(oldData.getName());
            boolean descriptionSameAsOldData = newDescription.trim().equals(oldData.getDescription());
            return !(publisherNameSameAsOldData && descriptionSameAsOldData);
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
        publisherName.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        description.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
    }
    public void submit(ActionEvent event) throws IOException {
        String name = publisherName.getText().trim();
        String descriptionText = description.getText().trim();
        PublisherEntity publisher = new PublisherEntity();
        publisher.setId(this.oldData.getId());
        publisher.setName(name);
        publisher.setDescription(descriptionText);
        publisher.setUpdated_at(LocalDateTime.now());
        boolean result = dao.editPublisher(publisher);
        if (result) {
            this.dialog.setResult("successfully");
            this.dialog.close();
            tableView.setItems(FXCollections.observableArrayList(dao.getAllPublisher(oldSearch, oldSort)));
        }
    }
}
