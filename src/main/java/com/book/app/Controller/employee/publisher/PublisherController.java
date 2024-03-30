package com.book.app.Controller.employee.publisher;

import com.book.app.Dao.impl.PublisherDaoImpl;
import com.book.app.Dao.impl.PublisherDaoImpl;
import com.book.app.Entity.PublisherEntity;
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

public class PublisherController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    Text textWelcome, textUsername;
    @FXML
    TextField textSearch;
    @FXML
    ComboBox<String> sortCombo;
    @FXML
    private TableView<PublisherEntity> tableview;
    @FXML
    private TableColumn<PublisherEntity, String> nameCol;
    @FXML
    private TableColumn<PublisherEntity, String> desCol;
    @FXML
    private TableColumn<PublisherEntity, String> idCol;
    @FXML
    private TableColumn<PublisherEntity, String> createdCol;
    @FXML
    private TableColumn<PublisherEntity, Void> actionCol;
    @FXML
    private ChoiceBox<String> choiceBoxLogout;
    @FXML
    private Button newPublisher, btnSearch, btnAuthor, btnCategory, btnPublisher, btnHome;

    private Parent root;
    private PublisherDaoImpl dao = new PublisherDaoImpl();

    private void updatePublisherList() {
        String sortType = sortCombo.getValue();
        String keyword = null;
        if (textSearch.getText() != null) {
            keyword = textSearch.getText().trim();
        }
        tableview.setItems(FXCollections.observableArrayList(dao.getAllPublisher(keyword, sortType)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnAuthor, btnCategory, btnPublisher, btnHome);
        idCol.setCellValueFactory(new PropertyValueFactory<PublisherEntity, String>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<PublisherEntity, String>("name"));
        desCol.setCellValueFactory(new PropertyValueFactory<PublisherEntity, String>("description"));
        createdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PublisherEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PublisherEntity, String> param) {
                PublisherEntity author = param.getValue();
                if (author != null) {
                    String created_at = DateUtils.convertLocalDateTimeToStringPattern(author.getCreated_at(), "HH:mm:ss dd-MM-yyyy");
                    return new SimpleStringProperty(created_at);
                }
                return null;
            }


        });
        actionCol.setCellFactory(param -> new TableCell<PublisherEntity, Void>() {
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
                        PublisherEntity author = getTableView().getItems().get(getIndex());
                        openDialogEditPublisher(event, author);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                lockButton.setOnAction(event -> {
                    PublisherEntity publisher = getTableView().getItems().get(getIndex());
                    dao.lockOrUnLockPublisher(publisher.getId(), !publisher.getEnable());
                    updatePublisherList();
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PublisherEntity author = getTableView().getItems().get(getIndex());
                    if (author != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(author.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        newPublisher.setOnAction(event -> {
            try {
                openDialogNewPublisher(event);
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
            tableview.setItems(FXCollections.observableArrayList(dao.getAllPublisher(keyword, sortType)));
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllPublisher(null, null)));
    }

    public void openDialogEditPublisher(ActionEvent event, PublisherEntity category) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/publisher/edit-publisher.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/publisher/edit-publisher.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        EditPublisherController controller = loader.getController();
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
    public void openDialogNewPublisher(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/publisher/new-publisher.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/publisher/new-publisher.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewPublisherController controller = loader.getController();
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
