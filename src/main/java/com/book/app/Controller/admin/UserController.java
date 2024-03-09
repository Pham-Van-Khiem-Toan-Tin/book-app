package com.book.app.Controller.admin;

import com.book.app.Dao.impl.UserImpl;
import com.book.app.Entity.User;
import com.book.app.Utils.DateUtils;
import com.book.app.Utils.SortUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    @FXML
    private TextField textSearch;
    @FXML
    private Button btnSearch;
    @FXML
    private ComboBox<String> sortCombo;
    private UserImpl dao = new UserImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
        createdCol.setCellValueFactory(new PropertyValueFactory<User, LocalDate>("createdAt"));
        roleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> param) {
                User user = param.getValue();
                if (user != null) {
                    String role = user.getAdmin() ? "admin" : "user";
                    return new SimpleStringProperty(role);
                }
                return null;
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
                    String oldPassword = getTableView().getItems().get(getIndex()).getPassword();
                    int userId = getTableView().getItems().get(getIndex()).getId();
                    try {
                        openDialogResetPassword(event, oldPassword, userId);
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
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(user.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        btnSearch.setOnAction(event -> search());
        sortCombo.getItems().addAll("id: Ascending", "username: A-Z", "created_at: Ascending");
        sortCombo.setOnAction(event -> sort(tableview.getItems()));
        tableview.setItems(FXCollections.observableArrayList(dao.getAllUser()));

    }

    public void updateUserList() {
        tableview.setItems(FXCollections.observableArrayList(dao.getAllUser()));
    }

    public void sort(ObservableList<User> tableList) {
        String sortType = sortCombo.getValue();
        SortedList<User> sortedData = SortUtils.getSortList(sortType, tableList);
        tableview.setItems(sortedData);
    }
    public void search() {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        List<User> users = dao.getAllUser();
        if (textSearch.getText() != null && !textSearch.getText().trim().equals("")) {
            String keyword = textSearch.getText().trim();
            Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
            usersList = users
                    .stream()
                    .filter(user -> {
                        String admin = user.getAdmin() ? "admin" : "user";
                        return pattern.matcher(user.getUsername()).find() ||
                                pattern.matcher(user.getPhone()).find() ||
                                pattern.matcher(String.valueOf(user.getId())).find() ||
                                pattern.matcher(user.getEmail()).find() ||
                                pattern.matcher(admin).find() ||
                                pattern.matcher(DateUtils
                                        .convertLocalDateToStringPattern(user.getCreatedAt(), "dd/MM/yyyy")).find();
                            }
                            )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tableview.setItems(usersList);
        } else {
            tableview.setItems(FXCollections.observableArrayList(users));
        }
    }

    @FXML
    public void openDialogNewUser(ActionEvent event) throws IOException {
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
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
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
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }

    private void openLockDialogUser(ActionEvent event) throws IOException {

    }
    private void openDialogResetPassword(ActionEvent event, String oldPassword, int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/reset-password.fxml"));
        Parent root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/reset-password.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        ResetPasswordController controller = loader.getController();
        controller.setDialog(dialog);
        controller.setTableView(tableview);
        controller.setOldData(oldPassword);
        controller.setUserId(id);
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
}
