package com.book.app.Controller.employee.category;

import com.book.app.Dao.impl.CategoryDaoImpl;
import com.book.app.Entity.CategoryEntity;
import com.book.app.Utils.DateUtils;
import com.book.app.Utils.UIUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    Text textWelcome, textUsername;
    @FXML
    TextField textSearch;
    @FXML
    ComboBox<String> sortCombo;
    @FXML
    private TableView<CategoryEntity> tableview;
    @FXML
    private TableColumn<CategoryEntity, String> nameCol;
    @FXML
    private TableColumn<CategoryEntity, String> desCol;
    @FXML
    private TableColumn<CategoryEntity, String> idCol;
    @FXML
    private TableColumn<CategoryEntity, String> createdCol;
    @FXML
    private TableColumn<CategoryEntity, Void> actionCol;
    @FXML
    private ComboBox<String> choiceBoxLogout;
    @FXML
    private Button newCategory, btnSearch, btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory;

    private Parent root;
    private CategoryDaoImpl dao = new CategoryDaoImpl();

    private void updateCategoryList() {
        String sortType = sortCombo.getValue();
        String keyword = null;
        if (textSearch.getText() != null) {
            keyword = textSearch.getText().trim();
        }
        tableview.setItems(FXCollections.observableArrayList(dao.getAllCategory(keyword, sortType)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory);
        idCol.setCellValueFactory(new PropertyValueFactory<CategoryEntity, String>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<CategoryEntity, String>("name"));
        desCol.setCellValueFactory(new PropertyValueFactory<CategoryEntity, String>("description"));
        createdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CategoryEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CategoryEntity, String> param) {
                CategoryEntity author = param.getValue();
                if (author != null) {
                    String created_at = DateUtils.convertLocalDateTimeToStringPattern(author.getCreated_at(), "HH:mm:ss dd-MM-yyyy");
                    return new SimpleStringProperty(created_at);
                }
                return null;
            }


        });
        actionCol.setCellFactory(param -> new TableCell<CategoryEntity, Void>() {
            private final Button editButton = new Button();
            private final Button lockButton = new Button();
            private final HBox pane = new HBox(editButton, lockButton);

            {
                editButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px; -fx-padding: 0; -fx-cursor: HAND");
                lockButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px;-fx-padding: 0; -fx-cursor: HAND");
                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(12.0);
                FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    try {
                        CategoryEntity author = getTableView().getItems().get(getIndex());
                        openDialogEditCategory(event, author);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                lockButton.setOnAction(event -> {
                    CategoryEntity category = getTableView().getItems().get(getIndex());
                    dao.lockOrUnLockCategory(category.getId(), !category.getEnable());
                    updateCategoryList();
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CategoryEntity author = getTableView().getItems().get(getIndex());
                    if (author != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(author.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        newCategory.setOnAction(event -> {
            try {
                openDialogNewCategory(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnSearch.setOnAction(event -> {
            String sortType = sortCombo.getValue();
            String keyword = null;
            if (textSearch.getText() != null) {
                keyword = textSearch.getText().trim();
            }
            tableview.setItems(FXCollections.observableArrayList(dao.getAllCategory(keyword, sortType)));
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllCategory(null, null)));
    }

    public void openDialogEditCategory(ActionEvent event, CategoryEntity category) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/category/edit-category.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/category/edit-category.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        EditCategoryController controller = loader.getController();
        controller.setStage(stage);
        controller.setTableView(tableview);
        controller.setDialog(dialog);
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        controller.setOldData(category);
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
    public void openDialogNewCategory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/category/new-category.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/category/new-category.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewCategoryController controller = loader.getController();
        controller.setStage(stage);
        controller.setTableView(tableview);
        controller.setDialog(dialog);
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
}
