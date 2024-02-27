package com.book.app.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthenticationController implements Initializable {
    @FXML
    Label userNameLogin, passwordLogin;
    @FXML
    TextField inputNameLogin, inputPasswordLogin;
    private String root = "/com/book/app/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameLogin.setOnMouseClicked(mouseEvent -> {
            inputNameLogin.requestFocus();
        });
        passwordLogin.setOnMouseClicked(mouseEvent -> {
            inputPasswordLogin.requestFocus();
        });
    }
}
