package com.book.app.Controller.employee.book;

import com.book.app.Config.ImageUploader;
import com.book.app.DTO.CloudinaryForm;
import com.book.app.Dao.impl.AuthorDaoImpl;
import com.book.app.Dao.impl.BookDaoImpl;
import com.book.app.Dao.impl.CategoryDaoImpl;
import com.book.app.Dao.impl.PublisherDaoImpl;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.BookEntity;
import com.book.app.Entity.CategoryEntity;
import com.book.app.Entity.PublisherEntity;
import com.book.app.Utils.UUIDUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NewBookController implements Initializable {
    @FXML
    Button btnImage, submitButton, cancel;
    @FXML
    Text nameFile;
    @FXML
    ImageView imagePreview;
    @FXML
    TextField bookTitle, description;
    @FXML
    ChoiceBox<PublisherEntity> choicePublisher;
    @FXML
    ListView<AuthorEntity> listViewAuthor;
    @FXML
    ListView<CategoryEntity> listViewCategory;
    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private File selectedFile;
    private CloudinaryForm imageBook;
    private TableView<BookEntity> tableView;
    private BookDaoImpl dao = new BookDaoImpl();
    private PublisherDaoImpl pls = new PublisherDaoImpl();
    private AuthorDaoImpl authorDao = new AuthorDaoImpl();
    private CategoryDaoImpl categoryDao = new CategoryDaoImpl();
    private ArrayList<AuthorEntity> authors = new ArrayList<AuthorEntity>();
    private ArrayList<CategoryEntity> categories = new ArrayList<CategoryEntity>();


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

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public CloudinaryForm getImageBook() {
        return imageBook;
    }

    public void setImageBook(CloudinaryForm imageBook) {
        this.imageBook = imageBook;
    }

    public TableView<BookEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<BookEntity> tableView) {
        this.tableView = tableView;
    }

    private boolean validateFields() {
        return bookTitle.getText() != null && !bookTitle.getText().trim().isEmpty() &&
                description.getText() != null && !description.getText().trim().isEmpty() &&
                !authors.isEmpty() &&
                choicePublisher.getValue() != null && selectedFile != null;
    }

    private void checkFields() {
        submitButton.setDisable(!validateFields());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a file");
            fileChooser.setInitialDirectory(new File("C:\\"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG image", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("All images", "*.jpg", "*.png"));
            setSelectedFile(fileChooser.showOpenDialog(stage));
            if (selectedFile != null) {
                nameFile.setText(getSelectedFile().getName());
                imagePreview.setImage(new Image(getSelectedFile().getPath()));
            } else {
                System.out.println("No file selected");
            }
            checkFields();
        });
        submitButton.setOnAction(event -> {
            try {
                submit(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ObservableList<PublisherEntity> publisherEntityList = FXCollections.observableArrayList(pls.getAllPublisherActive());
        choicePublisher.setItems(publisherEntityList);
        choicePublisher.setConverter(new StringConverter<PublisherEntity>() {
            @Override
            public String toString(PublisherEntity publisherEntity) {
                return (publisherEntity != null) ? publisherEntity.getName() : "Chose Publisher";
            }

            @Override
            public PublisherEntity fromString(String string) {
                // Không cần implement vì chúng ta chỉ đọc dữ liệu từ ChoiceBox
                return null;
            }
        });

        ObservableList<AuthorEntity> authorEntityObservableList = FXCollections.observableArrayList(authorDao.getAllAuthorName());

        listViewAuthor.setItems(authorEntityObservableList);
        listViewAuthor.setCellFactory(param -> new ListCell<AuthorEntity>() {
            @Override
            protected void updateItem(AuthorEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getName());
                    checkBox.setOnAction(event -> {
                        AuthorEntity selectedAuthor = getItem();
                        if (checkBox.isSelected() && selectedAuthor != null) {
                            authors.add(selectedAuthor);
                        } else {
                            authors = (ArrayList<AuthorEntity>) authors.stream().filter(author -> !author.equals(selectedAuthor.getName())).collect(Collectors.toList());
                        }
                        StringBuilder result = new StringBuilder();

                        // Duyệt qua từng phần tử trong mảng
                        for (AuthorEntity author : authors) {
                            // Thêm phần tử vào chuỗi kết quả, cùng với một khoảng trắng
                            result.append(author.getName()).append(",");
                        }
                        if (result.length() > 0) {
                            result.deleteCharAt(result.length() - 1);
                        }
                        checkFields();
                    });
                    setGraphic(checkBox);
                }
            }
        });


        ObservableList<CategoryEntity> categoryEntityObservableList = FXCollections.observableArrayList(categoryDao.getAllCategoryName());

        listViewCategory.setItems(categoryEntityObservableList);
        listViewCategory.setCellFactory(param -> new ListCell<CategoryEntity>() {
            @Override
            protected void updateItem(CategoryEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getName());
                    checkBox.setOnAction(event -> {
                        CategoryEntity selectedCate = getItem();
                        if (checkBox.isSelected() && selectedCate != null) {
                            categories.add(selectedCate);
                        } else {
                            categories = (ArrayList<CategoryEntity>) categories.stream().filter(author -> !author.equals(selectedCate.getName())).collect(Collectors.toList());
                        }
                        StringBuilder result = new StringBuilder();

                        // Duyệt qua từng phần tử trong mảng
                        for (CategoryEntity category : categories) {
                            // Thêm phần tử vào chuỗi kết quả, cùng với một khoảng trắng
                            result.append(category.getName()).append(",");
                        }
                        if (result.length() > 0) {
                            result.deleteCharAt(result.length() - 1);
                        }
                        checkFields();
                    });
                    setGraphic(checkBox);
                }
            }
        });

        bookTitle.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        description.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        choicePublisher.setOnAction(event -> checkFields());
        checkFields();
    }

    public void submit(ActionEvent event) throws IOException {
        String title = bookTitle.getText().trim();
        String descriptionText = description.getText().trim();
        BookEntity book = new BookEntity();
        String id = UUIDUtils.generateUniqueId(title);
        book.setId(id);
        book.setName(title);
        book.setDescription(descriptionText);
        try {
            ImageUploader imageUploader = new ImageUploader();
            CloudinaryForm uploadResult = imageUploader.uploadImage(getSelectedFile(), "book");
            setImageBook(uploadResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        book.setImage_url(imageBook.getUrl());
        book.setImage_public_id(imageBook.getPublic_id());
        book.setEnable(true);
        book.setPublisher(choicePublisher.getValue());
        book.setAuthors(listViewAuthor.getItems());
        book.setCategories(listViewCategory.getItems());
        book.setCreated_at(LocalDateTime.now());
        boolean result = dao.addBook(book);
        if (result) {
            this.dialog.setResult("successfully");
            this.dialog.close();
            tableView.setItems(FXCollections.observableArrayList(dao.getAllBook(oldSearch, oldSort)));
        } else {
            try {
                ImageUploader imageUploader = new ImageUploader();
                imageUploader.destroyImage(imageBook.getPublic_id());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
