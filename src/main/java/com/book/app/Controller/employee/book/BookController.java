package com.book.app.Controller.employee.book;

import com.book.app.Controller.employee.book.EditBookController;
import com.book.app.Controller.employee.book.NewBookController;
import com.book.app.Dao.impl.BookDaoImpl;
import com.book.app.Entity.BookEntity;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    Text textWelcome, textUsername;
    @FXML
    TextField textSearch;
    @FXML
    ComboBox<String> sortCombo;
    @FXML
    private TableView<BookEntity> tableview;
    @FXML
    private TableColumn<BookEntity, String> nameCol;
    @FXML
    private TableColumn<BookEntity, Void> imageCol;
    @FXML
    private TableColumn<BookEntity, String> desCol;
    @FXML
    private TableColumn<BookEntity, String> priceCol;
    @FXML
    private TableColumn<BookEntity, String> idCol;
    @FXML
    private TableColumn<BookEntity, String> createdCol;
    @FXML
    private TableColumn<BookEntity, Void> actionCol;
    @FXML
    private ComboBox<String> choiceBoxLogout;
    @FXML
    private Button newBook, btnSearch, btnBook, btnCategory, btnPublisher, btnHome, btnInventory, btnAuthor, btnOrder;

    private Parent root;
    private BookDaoImpl dao = new BookDaoImpl();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnAuthor, btnCategory, btnPublisher, btnHome, btnBook, btnInventory, btnOrder);
        idCol.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("name"));
        imageCol.setCellFactory(param -> new TableCell<BookEntity, Void>() {
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
                    BookEntity book = getTableView().getItems().get(getIndex());
                    if (book != null) {
                        Image image = new Image(getTableView().getItems().get(getIndex()).getImage_url());
                        imageView.setImage(image);
                    }
                    setGraphic(pane);
                }
            }
        });
        priceCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BookEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<BookEntity, String> param) {
                BookEntity book = param.getValue();
                if (book != null) {
                    String price = Optional.ofNullable(book.getPrice()).map(String::valueOf).orElse("not imported yet");
                    return new SimpleStringProperty(price);
                }
                return null;
            }
        });
        desCol.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("description"));
        createdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BookEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<BookEntity, String> param) {
                BookEntity book = param.getValue();
                if (book != null) {
                    String created_at = DateUtils.convertLocalDateTimeToStringPattern(book.getCreated_at(), "HH:mm:ss dd-MM-yyyy");
                    return new SimpleStringProperty(created_at);
                }
                return null;
            }


        });
        actionCol.setCellFactory(param -> new TableCell<BookEntity, Void>() {
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
                    BookEntity book = dao.getBookDetail(getTableView().getItems().get(getIndex()).getId());
                    try {
                        openDialogEditBook(event, book);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                lockButton.setOnAction(event -> {
                    BookEntity author = getTableView().getItems().get(getIndex());
                    dao.lockOrUnLockBook(author.getId(), !author.getEnable());
//                    updateBookList();
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BookEntity author = getTableView().getItems().get(getIndex());
                    if (author != null) {
                        FontAwesomeIconView lockIcon = new FontAwesomeIconView(author.getEnable() ? FontAwesomeIcon.LOCK : FontAwesomeIcon.UNLOCK);
                        lockButton.setGraphic(lockIcon);
                    }
                    setGraphic(pane);
                }
            }
        });
        newBook.setOnAction(event -> {
            try {
                openDialogNewBook(event);
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
            tableview.setItems(FXCollections.observableArrayList(dao.getAllBook(keyword, sortType)));
        });
        sortCombo.getItems().addAll("Name", "Price", "Created_at");
        sortCombo.setOnAction(event -> {
            tableview.setItems(FXCollections.observableArrayList(dao.getAllBook(null, sortCombo.getValue())));
        });
        tableview.setItems(FXCollections.observableArrayList(dao.getAllBook(null, null)));
    }
    public void openDialogEditBook(ActionEvent event, BookEntity book) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/book/edit-book.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/book/edit-book.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        EditBookController controller = loader.getController();
        controller.setStage(stage);
        controller.setTableView(tableview);
        controller.setDialog(dialog);
        controller.setOldSort(Objects.requireNonNullElse(sortCombo.getValue(), ""));
        controller.setOldSearch(Objects.requireNonNullElse(textSearch.getText(), ""));
        controller.setOldData(book);
        stage.setOnCloseRequest(e -> {
            dialog.setResult("close");
            dialog.close();
        });
        // Hiển thị dialog và đợi cho đến khi nó đóng
        dialog.show();
    }
    public void openDialogNewBook(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory + "dialog/book/new-book.fxml"));
        root = loader.load();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(false);
        Scene dialogScene = dialog.getDialogPane().getScene();
        dialogScene.getStylesheets().add(getClass().getResource(rootDirectory + "static/css/book/new-book.css").toExternalForm());
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        NewBookController controller = loader.getController();
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
