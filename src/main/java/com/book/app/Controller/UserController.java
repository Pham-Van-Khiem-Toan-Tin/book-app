package com.book.app.Controller;

import com.book.app.Dao.impl.UserImpl;
import com.book.app.Entity.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    private TableView<User> tableview;
    @FXML
    private TableColumn<User, String> nameCol;
    @FXML
    private TableColumn<User, String> phoneCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> roleCol;
    @FXML
    private TableColumn<User, Integer> idCol;
    @FXML
    private TableColumn<User, LocalDate> createdCol;
    @FXML
    private TableColumn<User, Void> actionCol;
    private UserImpl dao = new UserImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
        createdCol.setCellValueFactory(new PropertyValueFactory<User, LocalDate>("createdAt"));
        roleCol.setCellFactory(param -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user.getAdmin()) {
                        setText("admin");
                    } else {
                        setText("user");
                    }
                }
            }
        });

        actionCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button();
            private final Button lockButton = new Button();
            private final Button resetPassButton = new Button();
            private final HBox pane = new HBox(editButton, lockButton, resetPassButton);

            {
                editButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px; -fx-padding: 0");
                lockButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px;-fx-padding: 0");
                resetPassButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px;-fx-padding: 0");
                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(12.0);
                FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    User getPatient = getTableView().getItems().get(getIndex());
                    try {
                        openEditDialogUser(event, getPatient);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                lockButton.setOnAction(event -> {
                    User getPatient = getTableView().getItems().get(getIndex());
                    dao.lockOrUnLockUser(getPatient.getId(), !getPatient.getEnable());
                    updateUserList();
                });
                FontAwesomeIconView resetPassIcon = new FontAwesomeIconView(FontAwesomeIcon.KEY);
                resetPassButton.setGraphic(resetPassIcon);
                resetPassButton.setOnAction(event -> {

                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(user.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllUser()));

    }

    public void updateUserList() {
        tableview.setItems(FXCollections.observableArrayList(dao.getAllUser()));
    }

    @FXML
    public void openDialogNewUser(ActionEvent event) throws IOException {
        System.out.println("chay vao day");
        System.out.println(UserController.class.getResource("dialog/new-user.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/new-user.fxml"));
        Parent root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/new-user.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewUserController controller = loader.getController();
        controller.setDialog(dialog);
        controller.setTableView(tableview);
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
    private void openEditDialogUser(ActionEvent event, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/edit-user.fxml"));
        Parent root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/edit-user.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        EditUserController controller = loader.getController();
        controller.setDialog(dialog);
        controller.setTableView(tableview);
        controller.setOldData(user);
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
    private void openDeleteDialogUser(ActionEvent event) throws IOException {

    }
    private void openLockDialogUser(ActionEvent event) throws IOException {

    }
}
