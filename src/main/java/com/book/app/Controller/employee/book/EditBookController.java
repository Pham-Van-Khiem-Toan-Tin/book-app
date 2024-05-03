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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EditBookController implements Initializable {
    @FXML
    Button btnImage, submitButton, cancel;
    @FXML
    Text nameFile;
    @FXML
    ImageView imagePreview;
    @FXML
    ComboBox<PublisherEntity> choicePublisher;
    @FXML
    TextField listAuthorName, listCategoryName, authorName, categoryName, bookTitle, description;
    @FXML
    ListView<AuthorEntity> listAuthor;
    @FXML
    ListView<CategoryEntity> listCategory;
    @FXML
    Pane boxSearchAuthor, boxSearchCategory;
    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private File selectedFile;
    private CloudinaryForm imageBook;
    private TableView<BookEntity> tableView;
    private BookEntity oldData;
    private BookDaoImpl dao = new BookDaoImpl();
    private PublisherDaoImpl pls = new PublisherDaoImpl();
    private AuthorDaoImpl authorDao = new AuthorDaoImpl();
    private CategoryDaoImpl categoryDao = new CategoryDaoImpl();
    private List<AuthorEntity> authors = new ArrayList<AuthorEntity>();
    private List<CategoryEntity> categories = new ArrayList<CategoryEntity>();
    private List<PublisherEntity> publisherEntityList;
    private List<AuthorEntity> authorEntityObservableList;
    private List<CategoryEntity> categoryEntityObservableList;
    public CloudinaryForm getImageBook() {
        return imageBook;
    }

    public void setImageBook(CloudinaryForm imageBook) {
        this.imageBook = imageBook;
    }


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

    public TableView<BookEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<BookEntity> tableView) {
        this.tableView = tableView;
    }

    public BookEntity getOldData() {
        return oldData;

    }

    public void setOldData(BookEntity oldData) {
        this.oldData = oldData;
        bookTitle.setText(oldData.getName());
        description.setText(oldData.getDescription());
        choicePublisher.setValue(oldData.getPublisher());
        categoryEntityObservableList = categoryEntityObservableList.stream()
                .map(element -> {
                    if (oldData.getCategories().stream().anyMatch(item -> item.getId().equals(element.getId()))) {
                        element.setContainsBook(true);
                    }
                    return element;
                })
                .collect(Collectors.toList());
        authorEntityObservableList = authorEntityObservableList.stream()
                .map(element -> {
                    if (oldData.getAuthors().stream().anyMatch(item -> item.getId().equals(element.getId()))) {
                        element.setContainsBook(true);
                    }
                    return element;
                })
                .collect(Collectors.toList());
        imagePreview.setImage(new Image(oldData.getImage_url()));
        nameFile.setText("Book Image");
        authors = oldData.getAuthors();
        categories = oldData.getCategories();
        changeContentTextField(listAuthorName, authors.stream().map(element -> element.getName()).collect(Collectors.joining(", ")));
        changeContentTextField(listCategoryName, categories.stream().map(element -> element.getName()).collect(Collectors.joining(", ")));
    }

    private boolean validateFields() {
        return bookTitle.getText() != null && !bookTitle.getText().trim().isEmpty() &&
                description.getText() != null && !description.getText().trim().isEmpty() &&
                !authors.isEmpty() &&
                choicePublisher.getValue() != null && nameFile.getText() != null;
    }

    private boolean checkOldValue() {
        if (!Objects.isNull(oldData)) {
            String newBookName = Objects.requireNonNullElse(bookTitle.getText(), "");
            String newBookDescription = Objects.requireNonNullElse(description.getText(), "");
            boolean nameCheck = newBookName.trim().equals(oldData.getName());
            boolean descriptionCheck = newBookDescription.trim().equals(oldData.getDescription());
            boolean categoryCheck = oldData.getCategories().stream().map(CategoryEntity::getId).sorted().collect(Collectors.toCollection(ArrayList::new)).equals(categories.stream().map(CategoryEntity::getId).sorted().collect(Collectors.toCollection(ArrayList::new)));
            boolean authorCheck = oldData.getAuthors().stream().map(AuthorEntity::getId).sorted().collect(Collectors.toCollection(ArrayList::new)).equals(authors.stream().map(AuthorEntity::getId).sorted().collect(Collectors.toCollection(ArrayList::new)));
            boolean publisherCheck = choicePublisher.getValue() != null && choicePublisher.getValue().getId().equals(oldData.getPublisher().getId());
            boolean fileCheck = nameFile.getText() != null && nameFile.getText().equals("Book image");
            return !(nameCheck && descriptionCheck && categoryCheck && authorCheck && publisherCheck && fileCheck);
        }
        return true;
    }

    private void checkFields() {
        submitButton.setDisable(!checkOldValue() || !validateFields());
    }
    private void changeContentTextField(TextField listname, String text) {
        listname.setText(text);
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        publisherEntityList = pls.getAllPublisherActive();
        choicePublisher.setItems(FXCollections.observableArrayList(publisherEntityList));
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
        authorEntityObservableList = authorDao.getAllAuthorName();
        categoryEntityObservableList = categoryDao.getAllCategoryName();
        bookTitle.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        description.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        choicePublisher.setOnAction(event -> {
            choicePublisher.hide();
            checkFields();
        });
        listAuthor.setItems(FXCollections.observableArrayList(authorEntityObservableList));
        listAuthor.setCellFactory(param -> new ListCell<AuthorEntity>() {
            @Override
            protected void updateItem(AuthorEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getName());
                    if (Optional.ofNullable(item.isContainsBook()).orElse(false)) {
                        checkBox.setSelected(true);
                    }
                    checkBox.setOnAction(event -> {
                        AuthorEntity selectedAuthor = getItem();
                        if (checkBox.isSelected() && selectedAuthor != null) {
                            selectedAuthor.setContainsBook(true);
                            authors.add(selectedAuthor);
                        } else {
                            selectedAuthor.setContainsBook(false);
                            authors = (ArrayList<AuthorEntity>) authors.stream().filter(author -> !author.getId().equals(selectedAuthor.getId())).collect(Collectors.toList());
                        }
                        changeContentTextField(listAuthorName, authors.stream().map(element -> element.getName()).collect(Collectors.joining(", ")));
                        checkFields();
                    });
                    setGraphic(checkBox);
                }
            }
        });
        listAuthorName.setOnMouseClicked(event -> {
            boxSearchAuthor.setVisible(!boxSearchAuthor.isVisible());
            boxSearchCategory.setVisible(false);
        });
        authorName.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = Optional.ofNullable(newValue).orElse("");
            List<AuthorEntity> resultSearch = new ArrayList<>();
            if (searchText.isEmpty()) {
                resultSearch = authorEntityObservableList;
            } else {
                Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                resultSearch = authorEntityObservableList.stream().filter(author -> pattern.matcher(author.getName()).find()).collect(Collectors.toList());
            }
            listAuthor.setItems(FXCollections.observableArrayList(resultSearch));
        });
        listCategory.setItems(FXCollections.observableArrayList(categoryEntityObservableList));
        listCategory.setCellFactory(param -> new ListCell<CategoryEntity>() {
            @Override
            protected void updateItem(CategoryEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(item.getName());
                    if (Optional.ofNullable(item.isContainsBook()).orElse(false)) {
                        checkBox.setSelected(true);
                    }
                    checkBox.setOnAction(event -> {
                        CategoryEntity selectedCate = getItem();
                        if (checkBox.isSelected() && selectedCate != null) {
                            categories.add(selectedCate);
                        } else {
                            categories = (ArrayList<CategoryEntity>) categories.stream().filter(category -> !category.getId().equals(selectedCate.getId())).collect(Collectors.toList());
                        }
                        changeContentTextField(listCategoryName, categories.stream().map(element -> element.getName()).collect(Collectors.joining(", ")));
                        checkFields();
                    });
                    setGraphic(checkBox);
                }
            }
        });
        categoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = Optional.ofNullable(newValue).orElse("");
            List<CategoryEntity> resultSearch = new ArrayList<>();
            if (searchText.isEmpty()) {
                resultSearch = categoryEntityObservableList;
            } else {
                Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                resultSearch = categoryEntityObservableList.stream().filter(category -> pattern.matcher(category.getName()).find()).collect(Collectors.toList());
            }
            listCategory.setItems(FXCollections.observableArrayList(resultSearch));
        });
        listCategoryName.setOnMouseClicked(mouseEvent -> {
            boxSearchCategory.setVisible(!boxSearchCategory.isVisible());
            boxSearchAuthor.setVisible(false);
        });
        checkFields();
    }

    public void submit(ActionEvent event) throws Exception {
        String title = bookTitle.getText().trim();
        String descriptionText = description.getText().trim();
        BookEntity book = new BookEntity();
        book.setId(oldData.getId());
        book.setName(title);
        book.setDescription(descriptionText);
        book.setCategories(categories);
        book.setPublisher(choicePublisher.getValue());
        book.setAuthors(authors);
        if (selectedFile != null) {
            try {
                ImageUploader imageUploader = new ImageUploader();
                CloudinaryForm uploadResult = imageUploader.uploadImage(getSelectedFile(), "book");
                setImageBook(uploadResult);
                book.setImage_url(imageBook.getUrl());
                book.setImage_public_id(imageBook.getPublic_id());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        boolean result = dao.editBook(book);
        if (result) {
            ImageUploader imageUploader = new ImageUploader();
            imageUploader.destroyImage(oldData.getImage_public_id());
            this.dialog.setResult("successfully");
            this.dialog.close();
            tableView.setItems(FXCollections.observableArrayList(dao.getAllBook(oldSearch, oldSort)));
        } else {
            if (selectedFile != null) {
                try {
                    ImageUploader imageUploader = new ImageUploader();
                    imageUploader.destroyImage(imageBook.getPublic_id());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
