package com.book.app.Controller.employee.inventory;

import com.book.app.Controller.employee.category.NewCategoryController;
import com.book.app.Dao.impl.InventoryDaoImpl;
import com.book.app.Entity.CategoryEntity;
import com.book.app.Entity.InventoryEntity;
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
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    Text textWelcome, textUsername;
    @FXML
    TextField textSearch;
    @FXML
    ComboBox<String> sortCombo;
    @FXML
    private TableView<InventoryEntity> tableview;
    @FXML
    private TableColumn<InventoryEntity, String> costCol;
    @FXML
    private TableColumn<InventoryEntity, String> quantityCol;
    @FXML
    private TableColumn<InventoryEntity, String> idCol;
    @FXML
    private TableColumn<InventoryEntity, String> createdCol;
    @FXML
    private TableColumn<InventoryEntity, Void> actionCol;
    @FXML
    private ComboBox<String> choiceBoxLogout;
    private Parent root;
    private InventoryDaoImpl dao = new InventoryDaoImpl();
    @FXML
    private Button createInventory, btnSearch, btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory);
        idCol.setCellValueFactory(new PropertyValueFactory<InventoryEntity, String>("id"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<InventoryEntity, String>("quantityBook"));
        costCol.setCellValueFactory(new PropertyValueFactory<InventoryEntity, String>("totalCost"));
        createdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<InventoryEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<InventoryEntity, String> param) {
                InventoryEntity inventory = param.getValue();
                if (inventory != null) {
                    String created_at = DateUtils.convertLocalDateTimeToStringPattern(inventory.getCreatedAt(), "HH:mm:ss dd-MM-yyyy");
                    return new SimpleStringProperty(created_at);
                }
                return null;
            }
        });
        actionCol.setCellFactory(param -> new TableCell<InventoryEntity, Void>() {
            private final Button viewButton = new Button();
            private final HBox pane = new HBox(viewButton);

            {
                viewButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px; -fx-padding: 0; -fx-cursor: HAND");
                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(12.0);
                FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                viewButton.setGraphic(viewIcon);
                viewButton.setOnAction(event -> {
                    try {
                        InventoryEntity inventory = getTableView().getItems().get(getIndex());
                        openDialogView(event, inventory);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
        createInventory.setOnAction(event -> {
            try {
                openDialogCreateInventory(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllInventory(null, null)));
    }
    public void openDialogView(ActionEvent event, InventoryEntity inventory) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/category/edit-category.fxml"));
//        root = loader.load();
//        Dialog<String> dialog = new Dialog<>();
//        dialog.getDialogPane().setContent(root);
//        dialog.setResizable(false);
//        Scene dialogScene = dialog.getDialogPane().getScene();
//        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/category/edit-category.css").toExternalForm());
//        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
//        EditCategoryController controller = loader.getController();
//        controller.setStage(stage);
//        controller.setTableView(tableview);
//        controller.setDialog(dialog);
//        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
//        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
//        controller.setOldData(category);
//        stage.setOnCloseRequest(e -> {
//            dialog.setResult("close");
//            dialog.close();
//        });
//        // Hiển thị dialog và đợi cho đến khi nó đóng
//        dialog.show();
    }
    public void openDialogCreateInventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/inventory/new-inventory.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/inventory/new-inventory.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewInventoryController controller = loader.getController();
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
