package com.book.app.Controller.employee.order;

import com.book.app.Dao.impl.OrderDaoImpl;
import com.book.app.Entity.BookEntity;
import com.book.app.Entity.CustomerEntity;
import com.book.app.Entity.OrderEntity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewOrderController implements Initializable {
    @FXML
    private Text totalText;
    @FXML
    private CheckBox checkboxCustomer;
    @FXML
    private Pane boxCustomer;
    @FXML
    private TextField customerName, phoneNumber, customerMail;
    @FXML
    Pane searchBox;
    @FXML
    Button submitButton, cancel;
    @FXML
    TextField bookTitle;
    @FXML
    ListView<BookEntity> listBookSearch;
    @FXML
    TableView<BookEntity> listBook;
    @FXML
    TableColumn<BookEntity, String> nameCol;
    @FXML
    TableColumn<BookEntity, Void> quantityCol;
    @FXML
    TableColumn<BookEntity, String> priceCol;
    @FXML
    TableColumn<BookEntity, Void> actionCol;
    private List<BookEntity> listBookOfOrder = new ArrayList<>();
    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private TableView<OrderEntity> tableView;

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

    public TableView<OrderEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<OrderEntity> tableView) {
        this.tableView = tableView;
    }
    private List<BookEntity> allBook;
    private OrderDaoImpl dao = new OrderDaoImpl();
    private void resetListBook() {
        listBook.setItems(FXCollections.observableArrayList(listBookOfOrder));
    }
    private void resetListSearch() {
        listBookSearch.setItems(FXCollections.observableArrayList(dao.getBookToOrder(listBookOfOrder.stream().map(BookEntity::getId).collect(Collectors.toList()))));
    }
    private void resetTotalText() {
        double total = listBookOfOrder.stream().mapToDouble(item -> item.getPrice() * (double) item.getQuantity()).sum();
        totalText.setText("Total: " + String.valueOf(total));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("id"));
        priceCol.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("price"));
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
                        Integer newQuantity;
                    if (!newValue.isEmpty()) {
                        newQuantity = Integer.parseInt(newValue);
                        book.setQuantity(newQuantity);
                    } else {
                        newQuantity = 1;
                        book.setQuantity(1);
                    }
                    listBookOfOrder.stream().forEach(item -> {
                        if (item.getId().equals(book.getId())) {
                            item.setQuantity(newQuantity);
                            resetTotalText();
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
                    listBookOfOrder = listBookOfOrder.stream().filter(item -> !item.getId().equals(getTableView().getItems().get(getIndex()).getId())).collect(Collectors.toList());
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
//        List<BookEntity> test = dao.getBookToOrder(listBookOfOrder.stream().map(BookEntity::getId).collect(Collectors.toList()));
        listBook.setItems(FXCollections.observableArrayList(listBookOfOrder));
        allBook = dao.getBookToOrder(listBookOfOrder.stream().map(BookEntity::getId).collect(Collectors.toList()));
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
//
        bookTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Kiểm tra nếu mất focus
                // Đóng ContextMenu
                searchBox.setVisible(false);
            }
        });
        listBookSearch.setOnMousePressed(mouseEvent -> {
            BookEntity book = listBookSearch.getSelectionModel().getSelectedItem();
            book.setQuantity(1);
            listBookOfOrder.add(book);
            resetTotalText();
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
        checkboxCustomer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Xử lý sự kiện khi trạng thái của checkbox thay đổi
                if (checkboxCustomer.isSelected()) {
                    listBookOfOrder.stream().forEach(item -> {
                        item.setPrice(item.getPrice() - item.getCost()*0.05);
                    });
                    boxCustomer.setVisible(true);
                } else {
                    listBookOfOrder.stream().forEach(item -> {
                        item.setPrice(item.getCost()*1.1);
                    });
                    boxCustomer.setVisible(false);
                }
                listBook.refresh();
                resetTotalText();
            }
        });
        submitButton.setOnAction(event -> {
            try {
                submit(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void submit(ActionEvent event) throws IOException {
        CustomerEntity customer = new CustomerEntity();
        if (checkboxCustomer.isSelected()) {
            customer.setEmail(customerMail.getText().trim());
            customer.setName(customerName.getText().trim());
            customer.setPhoneNumber(phoneNumber.getText().trim());
        } else {
            customer = null;
        }
        boolean result = dao.addOrder(listBookOfOrder, customer);
        if (result) {
            this.dialog.setResult("Successfully");
            this.dialog.close();
            tableView.setItems(FXCollections.observableArrayList(dao.getAllOrder(null, null)));
        }
    }
}
