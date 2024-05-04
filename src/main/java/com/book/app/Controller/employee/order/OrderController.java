package com.book.app.Controller.employee.order;

import com.book.app.Controller.employee.inventory.NewInventoryController;
import com.book.app.Dao.impl.OrderDaoImpl;
import com.book.app.Entity.InventoryEntity;
import com.book.app.Entity.OrderEntity;
import com.book.app.Utils.DateUtils;
import com.book.app.Utils.UIUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class OrderController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    Text textWelcome, textUsername;
    @FXML
    TextField textSearch;
    @FXML
    ComboBox<String> sortCombo;
    @FXML
    private ComboBox<String> choiceBoxLogout;

    @FXML
    private Button createOrder, btnSearch, btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory, btnOrder;
    @FXML
    private TableView<OrderEntity> tableview;
    @FXML
    private TableColumn<OrderEntity, String> totalCol;
    @FXML
    private TableColumn<OrderEntity, String> quantityCol;
    @FXML
    private TableColumn<OrderEntity, String> idCol;
    @FXML
    private TableColumn<OrderEntity, String> createdCol;
    @FXML
    private TableColumn<OrderEntity, Void> actionCol;
    private Parent root;
    private OrderDaoImpl dao = new OrderDaoImpl();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory , btnOrder);
        idCol.setCellValueFactory(new PropertyValueFactory<OrderEntity, String>("id"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<OrderEntity, String>("quantity"));
        totalCol.setCellValueFactory(new PropertyValueFactory<OrderEntity, String>("total"));
        createdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<OrderEntity, String> param) {
                OrderEntity order = param.getValue();
                if (order != null) {
                    String created_at = DateUtils.convertLocalDateTimeToStringPattern(order.getCreated_at(), "HH:mm:ss dd-MM-yyyy");
                    return new SimpleStringProperty(created_at);
                }
                return null;
            }
        });
        actionCol.setCellFactory(param -> new TableCell<OrderEntity, Void>() {
            private final Button viewButton = new Button();
            private final HBox pane = new HBox(viewButton);

            {
                viewButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px; -fx-padding: 0; -fx-cursor: HAND");
                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(12.0);
                FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                viewButton.setGraphic(viewIcon);
//                viewButton.setOnAction(event -> {
//                    try {
//                        InventoryEntity inventory = getTableView().getItems().get(getIndex());
//                        openDialogView(event, inventory);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
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

        createOrder.setOnAction(event -> {
            try {
                openDialogCreateOrder(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllOrder(null, null)));
    }
    public void openDialogCreateOrder(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/order/new-order.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/order/new-order.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewOrderController controller = loader.getController();
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
