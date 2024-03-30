package com.book.app;

import com.book.app.Utils.AppUtils;
import com.book.app.Utils.TokenUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.security.Key;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        String rootDirectory = "/com/book/app/";
        Scene scene;
        try {
            String token = TokenUtil.readToken();
            if (token != null) {
                try {
                    Key key = TokenUtil.generateKey();
                    String result = TokenUtil.decrypt(token, key);
                    String[] user = result.split("\\.");
                    AppUtils.setUsername(user[0]);
                    AppUtils.setRole(user[2]);
                    String fxmFile, cssFile;
                    fxmFile = rootDirectory + (user[2].equals("admin") ? "admin/user-management.fxml": "home/home.fxml");
                    cssFile = user[2].equals("admin") ? "static/css/user-management.css": "static/css/home.css";
                    FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmFile));
                    Parent root = loader.load();
                    scene = new Scene(root, 1280, 800);
                    scene.getStylesheets().add(MainApplication.class.getResource(rootDirectory + cssFile).toExternalForm());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login/authen.fxml"));
                scene = new Scene(fxmlLoader.load(), 1280, 800);
            }
//            scene.getStylesheets().add(getClass().getResource("static/css/user-management.css").toExternalForm());
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Book store");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}