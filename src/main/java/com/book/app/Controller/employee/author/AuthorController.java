package com.book.app.Controller.employee.author;

import com.book.app.Controller.admin.NewUserController;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.AppUtils;
import com.book.app.Utils.UIUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthorController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    Text textWelcome, textUsername;
    @FXML
    private TableView<AuthorEntity> tableview;
    @FXML
    private TableColumn<AuthorEntity, String> nameCol;
    @FXML
    private TableColumn<AuthorEntity, Void> image;
    @FXML
    private TableColumn<AuthorEntity, String> desCol;
    @FXML
    private TableColumn<AuthorEntity, String> enableCol;
    @FXML
    private TableColumn<AuthorEntity, Integer> idCol;
    @FXML
    private TableColumn<AuthorEntity, LocalDateTime> createdCol;
    @FXML
    private TableColumn<AuthorEntity, Void> actionCol;
    @FXML
    private ChoiceBox<String> choiceBoxLogout;
    @FXML
    private Button newAuthor;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        idCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("name"));
        nameCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("description"));
        createdCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, LocalDateTime>("created_at"));
        actionCol.setCellFactory(param -> new TableCell<AuthorEntity, Void>() {
            private final Button editButton = new Button();
            private final Button lockButton = new Button();
            private final HBox pane = new HBox(editButton, lockButton);

            {
                editButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px; -fx-padding: 0");
                lockButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px;-fx-padding: 0");
                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(12.0);
                FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    AuthorEntity getPatient = getTableView().getItems().get(getIndex());
                    //                        openEditDialogEmployee(event, getPatient);
                });
                lockButton.setOnAction(event -> {
//                    EmployeeEntity getPatient = getTableView().getItems().get(getIndex());
//                    dao.lockOrUnLockEmployee(getPatient.getId(), !getPatient.getEnable());
//                    updateEmployeeList();
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AuthorEntity user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(user.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        newAuthor.setOnAction(event -> {
            try {
                openDialogNewEmployee(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @FXML
    public void openDialogNewEmployee(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/new-author.fxml"));
        Parent root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/new-user.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
//        NewUserController controller = loader.getController();
//        controller.setDialog(dialog);
//        controller.setTableView(tableview);
//        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
//        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
}
