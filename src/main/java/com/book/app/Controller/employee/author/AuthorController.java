package com.book.app.Controller.employee.author;

import com.book.app.Controller.admin.NewUserController;
import com.book.app.Dao.AuthorDao;
import com.book.app.Dao.impl.AuthorDaoImpl;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.AppUtils;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    TextField textSearch;
    @FXML
    ComboBox<String> sortCombo;
    @FXML
    private TableView<AuthorEntity> tableview;
    @FXML
    private TableColumn<AuthorEntity, String> nameCol;
    @FXML
    private TableColumn<AuthorEntity, Void> imageCol;
    @FXML
    private TableColumn<AuthorEntity, String> desCol;
    @FXML
    private TableColumn<AuthorEntity, String> idCol;
    @FXML
    private TableColumn<AuthorEntity, String> createdCol;
    @FXML
    private TableColumn<AuthorEntity, Void> actionCol;
    @FXML
    private ChoiceBox<String> choiceBoxLogout;
    @FXML
    private Button newAuthor, btnSearch, btnAuthor, btnCategory, btnPublisher, btnHome;

    private Parent root;
    private AuthorDaoImpl dao = new AuthorDaoImpl();

    private void updateAuthorList() {
        String sortType = sortCombo.getValue();
        String keyword = null;
        if (textSearch.getText() != null) {
            keyword = textSearch.getText().trim();
        }
        tableview.setItems(FXCollections.observableArrayList(dao.getAllAuthor(keyword, sortType)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnAuthor, btnCategory, btnPublisher, btnHome);
        idCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("name"));
        imageCol.setCellFactory(param -> new TableCell<AuthorEntity, Void>() {
            private final ImageView imageView = new ImageView();
            private final HBox pane = new HBox(imageView);

            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AuthorEntity author = getTableView().getItems().get(getIndex());
                    if (author != null) {
                        Image image = new Image(getTableView().getItems().get(getIndex()).getImage_url());
                        imageView.setImage(image);
                    }
                    setGraphic(pane);
                }
            }
        });
        desCol.setCellValueFactory(new PropertyValueFactory<AuthorEntity, String>("description"));
        createdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AuthorEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AuthorEntity, String> param) {
                AuthorEntity author = param.getValue();
                if (author != null) {
                    String created_at = DateUtils.convertLocalDateTimeToStringPattern(author.getCreated_at(), "HH:mm:ss dd-MM-yyyy");
                    return new SimpleStringProperty(created_at);
                }
                return null;
            }


        });
        actionCol.setCellFactory(param -> new TableCell<AuthorEntity, Void>() {
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
                        AuthorEntity author = getTableView().getItems().get(getIndex());
                        openDialogEditAuthor(event, author);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                lockButton.setOnAction(event -> {
                    AuthorEntity author = getTableView().getItems().get(getIndex());
                    dao.lockOrUnLockAuthor(author.getId(), !author.getEnable());
                    updateAuthorList();
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AuthorEntity author = getTableView().getItems().get(getIndex());
                    if (author != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(author.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        newAuthor.setOnAction(event -> {
            try {
                openDialogNewAuthor(event);
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
            tableview.setItems(FXCollections.observableArrayList(dao.getAllAuthor(keyword, sortType)));
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllAuthor(null, null)));
    }

    public void openDialogEditAuthor(ActionEvent event, AuthorEntity author) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/author/edit-author.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/author/edit-author.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        EditAuthorController controller = loader.getController();
        controller.setStage(stage);
        controller.setTableView(tableview);
        controller.setDialog(dialog);
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        controller.setOldData(author);
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
    public void openDialogNewAuthor(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/author/new-author.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/author/new-author.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewAuthorController controller = loader.getController();
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
