package com.book.app.Controller.employee.inventory;

import com.book.app.Dao.impl.InventoryDaoImpl;
import com.book.app.Entity.BookEntity;
import com.book.app.Entity.InventoryEntity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewInventoryController implements Initializable {
    @FXML
    TextField bookTitle;
    @FXML
    ListView<BookEntity> listBookSearch;
    @FXML
    Pane searchBox;
    @FXML
    Button submitButton, cancel;
    @FXML
    TableView<BookEntity> listBook;
    @FXML
    TableColumn<BookEntity, String> nameCol;
    @FXML
    TableColumn<BookEntity, Void> quantityCol;
    @FXML
    TableColumn<BookEntity, Void> costCol;
    @FXML
    TableColumn<BookEntity, Void> actionCol;
    private List<BookEntity> listBookOfInventory = new ArrayList<>();
    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private TableView<InventoryEntity> tableView;

    public String getOldSearch() {
        return oldSearch;
    }

    public void setOldSearch(String oldSearch) {
        this.oldSearch = oldSearch;
    }

    public String getOldSort() {
        return oldSort;
    }

    public void setOldSort(String oldSort) {
        this.oldSort = oldSort;
    }

    public Dialog<String> getDialog() {
        return dialog;
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public TableView<InventoryEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<InventoryEntity> tableView) {
        this.tableView = tableView;
    }

    private List<BookEntity> allBook;
    private InventoryDaoImpl dao = new InventoryDaoImpl();
    private void resetListBook() {
        listBook.setItems(FXCollections.observableArrayList(listBookOfInventory));
    }
    private void resetListSearch() {
        listBookSearch.setItems(FXCollections.observableArrayList(dao.getBookToInventory(listBookOfInventory.stream().map(BookEntity::getId).collect(Collectors.toList()))));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("name"));
        quantityCol.setCellFactory(param -> new TableCell<BookEntity, Void>() {
            private final TextField quantityInput = new TextField();
            private final HBox pane = new HBox(quantityInput);
            {
                pane.setAlignment(Pos.CENTER);
                UnaryOperator<TextFormatter.Change> filter = change -> {
                    String newText = change.getControlNewText();
                    // Kiểm tra xem văn bản mới có chứa chữ cái hay không
                    if (Pattern.matches("\\d*", newText)) {
                        return change; // Trả về thay đổi nếu văn bản mới chỉ chứa số
                    }
                    return null; // Ngược lại, không thay đổi nội dung của TextField
                };

                // Tạo TextFormatter sử dụng UnaryOperator để lọc nội dung của TextField
                TextFormatter<String> textFormatter = new TextFormatter<>(filter);
                quantityInput.setTextFormatter(textFormatter);
                quantityInput.textProperty().addListener((observable, oldValue, newValue) -> {
                    BookEntity book = getTableView().getItems().get(getIndex());
                    Integer newQuantity = Integer.parseInt(newValue);
                    book.setQuantity(newQuantity);
                    listBookOfInventory.stream().forEach(item -> {
                        if (item.getId().equals(book.getId())) {
                            item.setQuantity(newQuantity);
                        }
                    });
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
        costCol.setCellFactory(param -> new TableCell<>() {
            private final TextField costInput = new TextField();
            private final HBox pane = new HBox(costInput);

            {
                pane.setAlignment(Pos.CENTER);
                UnaryOperator<TextFormatter.Change> filter = change -> {
                    String newText = change.getControlNewText();
                    // Kiểm tra xem văn bản mới có chứa số hoặc dấu chấm hay không
                    if (Pattern.matches("[\\d.]*", newText)) {
                        return change; // Trả về thay đổi nếu văn bản mới chỉ chứa số hoặc dấu chấm
                    }
                    return null; // Ngược lại, không thay đổi nội dung của TextField
                };

                // Tạo TextFormatter sử dụng UnaryOperator để lọc nội dung của TextField
                TextFormatter<String> textFormatter = new TextFormatter<>(filter);
                costInput.setTextFormatter(textFormatter);
                costInput.textProperty().addListener((observable, oldValue, newValue) -> {
                    BookEntity book = getTableView().getItems().get(getIndex());
                    Double newQuantity = Double.parseDouble(newValue);
                    book.setCost(newQuantity);
                    listBookOfInventory.stream().forEach(item -> {
                        if (item.getId().equals(book.getId())) {
                            item.setCost(newQuantity);
                        }
                    });
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
        actionCol.setCellFactory(param -> new TableCell<BookEntity, Void>() {
            private final Button deleteButton = new Button();
            private final HBox pane = new HBox(deleteButton);

            {
                deleteButton.setStyle("-fx-background-color: transperent;-fx-font-size: 15px; -fx-padding: 0; -fx-cursor: HAND");
                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(12.0);
                FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setOnAction(event -> {
                    listBookOfInventory = listBookOfInventory.stream().filter(item -> !item.getId().equals(getTableView().getItems().get(getIndex()).getId())).collect(Collectors.toList());
                    resetListBook();
                    resetListSearch();
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
        listBook.setItems(FXCollections.observableArrayList(listBookOfInventory));
        allBook = dao.getBookToInventory(listBookOfInventory.stream().map(BookEntity::getId).collect(Collectors.toList()));
        listBookSearch.setItems(FXCollections.observableArrayList(allBook));
        listBookSearch.setCellFactory(param -> new ListCell<BookEntity>() {
            @Override
            protected void updateItem(BookEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName() + " - " + item.getPublisher().getName());
                }
            }
        });

        bookTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Kiểm tra nếu mất focus
                // Đóng ContextMenu
                searchBox.setVisible(false);
            }
        });
        listBookSearch.setOnMousePressed(mouseEvent -> {
            BookEntity book = listBookSearch.getSelectionModel().getSelectedItem();
            book.setQuantity(1);
            book.setCost(0.0);
            listBookOfInventory.add(book);
            resetListBook();
            resetListSearch();
        });
        bookTitle.setOnMouseClicked(mouseEvent -> {
            if (!searchBox.isVisible()) {
                searchBox.setVisible(true);
            }
        });
        bookTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = Optional.ofNullable(newValue).orElse("");
            List<BookEntity> resultSearch = new ArrayList<>();
            if (searchText.isEmpty()) {
                resultSearch = allBook;
            } else {
                Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                resultSearch = allBook.stream().filter(item -> pattern.matcher(item.getName()).find()).collect(Collectors.toList());
            }
            listBookSearch.setItems(FXCollections.observableArrayList(resultSearch));
        });

    }
}
