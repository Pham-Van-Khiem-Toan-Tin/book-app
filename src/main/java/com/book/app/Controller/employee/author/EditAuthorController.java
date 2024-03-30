package com.book.app.Controller.employee.author;

import com.book.app.Config.ImageUploader;
import com.book.app.DTO.CloudinaryForm;
import com.book.app.Dao.impl.AuthorDaoImpl;
import com.book.app.Entity.AuthorEntity;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditAuthorController implements Initializable {
    @FXML
    Button btnImage, submitButton, cancel;
    @FXML
    Text nameFile;
    @FXML
    ImageView imagePreview;
    @FXML
    TextField authorName, description;

    private String oldSearch;
    private String oldSort;
    private Dialog<String> dialog;
    private Stage stage;
    private File selectedFile;
    private CloudinaryForm imageAuthor = new CloudinaryForm();
    private TableView<AuthorEntity> tableView;
    private AuthorEntity oldData;
    private AuthorDaoImpl dao = new AuthorDaoImpl();

    public CloudinaryForm getImageAuthor() {
        return imageAuthor;
    }

    public void setImageAuthor(CloudinaryForm imageAuthor) {
        this.imageAuthor = imageAuthor;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getOldSort() {
        return oldSort;
    }

    public void setOldSort(String oldSort) {
        this.oldSort = oldSort;
    }

    public String getOldSearch() {
        return oldSearch;
    }

    public void setOldSearch(String oldSearch) {
        this.oldSearch = oldSearch;
    }

    public Dialog<String> getDialog() {
        return dialog;
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public TableView<AuthorEntity> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<AuthorEntity> tableView) {
        this.tableView = tableView;
    }

    public AuthorEntity getOldData() {
        return oldData;
    }

    public void setOldData(AuthorEntity oldData) {
        this.oldData = oldData;
        this.imageAuthor.setUrl(this.oldData.getImage_url());
        this.imageAuthor.setPublic_id(this.oldData.getImage_public_id());
        authorName.setText(this.oldData.getName());
        description.setText(this.oldData.getDescription());
        nameFile.setText("Old Image");
        imagePreview.setImage(new Image(this.oldData.getImage_url()));
    }

    private boolean validateFields() {
        return authorName.getText() != null && !authorName.getText().trim().isEmpty() &&
                description.getText() != null && !description.getText().trim().isEmpty() &&
                (nameFile.getText().equals("Old Image") || selectedFile != null);
    }
    private boolean checkOldData() {
        if (!Objects.isNull(oldData)) {
            String newAuthorName = Objects.requireNonNullElse(authorName.getText(), "");
            String newDescription = Objects.requireNonNullElse(description.getText(), "");
            String nameImage = nameFile.getText();
            boolean authorNameSameAsOldData = newAuthorName.trim().equals(oldData.getName());
            boolean descriptionSameAsOldData = newDescription.trim().equals(oldData.getDescription());
            boolean oldImage = nameImage.equals("Old Image");
            return !(authorNameSameAsOldData && descriptionSameAsOldData && oldImage);
        } else {
            return true;
        }
    };
    private void checkFields() {
        submitButton.setDisable(!checkOldData() || !validateFields());
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
        authorName.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
        description.textProperty().addListener((observable, oldValue, newValue) -> checkFields());
    }
    public void submit(ActionEvent event) throws IOException {
        String name = authorName.getText().trim();
        String descriptionText = description.getText().trim();
        AuthorEntity author = new AuthorEntity();
        author.setId(this.oldData.getId());
        author.setName(name);
        author.setDescription(descriptionText);
        if(selectedFile != null) {
            try {
                ImageUploader imageUploader = new ImageUploader();
                String resultDestroy = imageUploader.destroyImage(this.oldData.getImage_public_id());
                if (resultDestroy.equals("deleted!")) {
                    CloudinaryForm uploadResult = imageUploader.uploadImage(getSelectedFile(), "author");
                    setImageAuthor(uploadResult);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        author.setImage_url(imageAuthor.getUrl());
        author.setImage_public_id(imageAuthor.getPublic_id());
        author.setUpdated_at(LocalDateTime.now());
        boolean result = dao.editAuthor(author);
        if (result) {
            this.dialog.setResult("successfully");
            this.dialog.close();
            tableView.setItems(FXCollections.observableArrayList(dao.getAllAuthor(oldSearch, oldSort)));
        }
    }
}
