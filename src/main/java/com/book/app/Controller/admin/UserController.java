package com.book.app.Controller.admin;

import com.book.app.Dao.impl.EmployeeDaoImpl;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.AppUtils;
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
import javafx.scene.Node;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    private Text textWelcome, textUsername;
    @FXML
    private TableView<EmployeeEntity> tableview;
    @FXML
    private TableColumn<EmployeeEntity, String> nameCol;
    @FXML
    private TableColumn<EmployeeEntity, String> phoneCol;
    @FXML
    private TableColumn<EmployeeEntity, String> emailCol;
    @FXML
    private TableColumn<EmployeeEntity, String> roleCol;
    @FXML
    private TableColumn<EmployeeEntity, Integer> idCol;
    @FXML
    private TableColumn<EmployeeEntity, LocalDate> createdCol;
    @FXML
    private TableColumn<EmployeeEntity, Void> actionCol;
    @FXML
    private ChoiceBox<String> choiceBoxLogout;
    @FXML
    private TextField textSearch;
    @FXML
    private Button btnSearch;
    @FXML
    private ComboBox<String> sortCombo;
    private EmployeeDaoImpl dao = new EmployeeDaoImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textWelcome.setText("Hello, "+ AppUtils.getUsername());
        textUsername.setText(AppUtils.getUsername());
        choiceBoxLogout.getItems().add("Log out");
        choiceBoxLogout.setOnAction(event -> {
            if (choiceBoxLogout.getValue().equals("Log out")) {
                AppUtils.clearData();
                try {
                    handleLogout(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        idCol.setCellValueFactory(new PropertyValueFactory<EmployeeEntity, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<EmployeeEntity, String>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<EmployeeEntity, String>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<EmployeeEntity, String>("phone"));
        createdCol.setCellValueFactory(new PropertyValueFactory<EmployeeEntity, LocalDate>("createdAt"));
        roleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<EmployeeEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<EmployeeEntity, String> param) {
                EmployeeEntity employee = param.getValue();
                if (employee != null) {
                    String role = employee.getAdmin() ? "admin" : "user";
                    return new SimpleStringProperty(role);
                }
                return null;
            }
        });

        actionCol.setCellFactory(param -> new TableCell<EmployeeEntity, Void>() {
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
                    EmployeeEntity getPatient = getTableView().getItems().get(getIndex());
                    try {
                        openEditDialogEmployee(event, getPatient);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                lockButton.setOnAction(event -> {
                    EmployeeEntity getPatient = getTableView().getItems().get(getIndex());
                    dao.lockOrUnLockEmployee(getPatient.getId(), !getPatient.getEnable());
                    updateEmployeeList();
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
                    EmployeeEntity user = getTableView().getItems().get(getIndex());
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
        tableview.setItems(FXCollections.observableArrayList(dao.getAllEmployee()));

    }

    public void updateEmployeeList() {
        tableview.setItems(FXCollections.observableArrayList(dao.getAllEmployee()));
    }

    public void sort(ObservableList<EmployeeEntity> tableList) {
        String sortType = sortCombo.getValue();
        SortedList<EmployeeEntity> sortedData = SortUtils.getSortList(sortType, tableList);
        tableview.setItems(sortedData);
    }
    public void search() {
        ObservableList<EmployeeEntity> employeesList = FXCollections.observableArrayList();
        List<EmployeeEntity> employees = dao.getAllEmployee();
        if (textSearch.getText() != null && !textSearch.getText().trim().equals("")) {
            String keyword = textSearch.getText().trim();
            Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
            employeesList = employees
                    .stream()
                    .filter(employee -> {
                        String admin = employee.getAdmin() ? "admin" : "user";
                        return pattern.matcher(employee.getUsername()).find() ||
                                pattern.matcher(employee.getPhone()).find() ||
                                pattern.matcher(String.valueOf(employee.getId())).find() ||
                                pattern.matcher(employee.getEmail()).find() ||
                                pattern.matcher(admin).find() ||
                                pattern.matcher(DateUtils
                                        .convertLocalDateToStringPattern(employee.getCreatedAt(), "dd/MM/yyyy")).find();
                            }
                            )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tableview.setItems(employeesList);
        } else {
            tableview.setItems(FXCollections.observableArrayList(employees));
        }
    }

    @FXML
    public void openDialogNewEmployee(ActionEvent event) throws IOException {
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

    private void openEditDialogEmployee(ActionEvent event, EmployeeEntity employee) throws IOException {
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
        controller.setOldData(employee);
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
    private void handleLogout(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "login/authen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
