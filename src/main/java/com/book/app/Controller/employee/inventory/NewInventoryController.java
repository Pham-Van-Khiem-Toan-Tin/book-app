package com.book.app.Controller.employee.inventory;

import com.book.app.Dao.impl.BookDaoImpl;
import com.book.app.Dao.impl.InventoryDaoImpl;
import com.book.app.Entity.BookEntity;
import com.book.app.Entity.CategoryEntity;
import com.book.app.Entity.InventoryEntity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewInventoryController implements Initializable {
    @FXML
    TextField bookTitle;
    @FXML
    ListView<BookEntity> listBook, listBookSearch;
    @FXML
    Pane searchBox;
    @FXML
    Button submitButton, cancel;
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allBook = dao.getBookToInventory();
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
        listBook.setItems(FXCollections.observableArrayList(listBookOfInventory));
        listBook.setCellFactory(param -> new ListCell<BookEntity>() {
            @Override
            protected void updateItem(BookEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Pane pane = new Pane();
                    pane.setPadding(new  Insets(2,2,2,2));
                    pane.setPrefWidth(490);
                    pane.setPrefHeight(50);
                    ImageView imageView = new ImageView(new Image(item.getImage_url()));
                    imageView.setFitWidth(40);
                    imageView.setFitHeight(40);
                    imageView.setLayoutX(4);
                    imageView.setLayoutY(5);
                    Label titleLabel = new Label(item.getName());
                    titleLabel.setPrefWidth(121);
                    titleLabel.setPrefHeight(17);
                    titleLabel.setLayoutX(67);
                    titleLabel.setLayoutY(17);
                    Label priceLabel = new Label("price");
                    priceLabel.setLayoutX(218);
                    priceLabel.setLayoutY(17);
                    TextField price = new TextField();
                    price.setLayoutX(266);
                    price.setLayoutY(13);
                    price.setPrefWidth(69);
                    price.setText("0.0");
                    Label quantityLabel = new Label("quantity");
                    quantityLabel.setLayoutX(353);
                    quantityLabel.setLayoutY(17);
                    TextField quantity = new TextField();
                    quantity.setPrefWidth(33);
                    quantity.setPrefHeight(25);
                    quantity.setLayoutX(411);
                    quantity.setLayoutY(13);
                    quantity.setText("1");
                    Button btnDelete = new Button();
                    FontAwesomeIconView iconDelete = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                    btnDelete.setGraphic(iconDelete);
                    btnDelete.setPrefWidth(30);
                    btnDelete.setPrefHeight(27);
                    btnDelete.setLayoutX(464);
                    btnDelete.setLayoutY(12);
                    pane.getChildren().addAll(imageView, titleLabel, priceLabel, price, quantityLabel, quantity, btnDelete);
                    setGraphic(pane);
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
            listBookOfInventory.add(book);
            listBook.setItems(FXCollections.observableArrayList(listBookOfInventory));
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
