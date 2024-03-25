package com.book.app.Controller.employee.category;

import com.book.app.Entity.EmployeeEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    private Text textWelcome, textUsername;
    @FXML
    private TableView<EmployeeEntity> tableview;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
