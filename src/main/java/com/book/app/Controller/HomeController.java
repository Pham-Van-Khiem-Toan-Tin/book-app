package com.book.app.Controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ScrollPane lastSlider;
    @FXML
    private Button nextButton1;
    @FXML
    private Button preButton1;
    private int currentIndex = 0;
    private HBox lastList;
    private static final String[] IMAGE_URLS = {
            "https://via.placeholder.com/150",
            "https://via.placeholder.com/150",
            "https://via.placeholder.com/150",
            "https://via.placeholder.com/150",
            "https://via.placeholder.com/150",
            "https://via.placeholder.com/150"
    };
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lastList = new HBox();
        lastList.setAlignment(Pos.CENTER);
        lastList.setSpacing(20);
        for (String imageUrl : IMAGE_URLS) {
            Image image = new Image("https://via.placeholder.com/200x400");
            ImageView imageView1 = new ImageView(image);
            imageView1.setFitWidth(317);
            imageView1.setFitHeight(134);
            Rectangle clip1 = new Rectangle(imageView1.getFitWidth(), imageView1.getFitHeight());
            clip1.setArcWidth(20);
            clip1.setArcHeight(20);
            imageView1.setClip(clip1);
            StackPane stackPane1 = new StackPane(imageView1);
            stackPane1.setPrefSize(317, 134);
            stackPane1.setLayoutY(62);
            stackPane1.setStyle("-fx-background-radius: 20px");
            ImageView imageView2 = new ImageView(new Image("https://via.placeholder.com/400x200"));
            imageView2.setFitWidth(116);
            imageView2.setFitHeight(181);
            StackPane stackPane2 = new StackPane(imageView2);
            Rectangle clip2 = new Rectangle(imageView2.getFitWidth(), imageView2.getFitHeight());
            clip2.setArcWidth(20);
            clip2.setArcHeight(20);
            imageView2.setClip(clip2);
            stackPane2.setPrefSize(116, 181);
            stackPane2.setLayoutX(26);
            Pane pane = new Pane(stackPane1, stackPane2);
            lastList.getChildren().add(pane);
        }

        lastSlider.setContent(lastList);
        lastSlider.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        nextButton1.setOnAction(event -> nextImage());
        preButton1.setOnAction(actionEvent -> preImage());
    }


    private void preImage() {
        if (currentIndex >  0) {
            currentIndex--;
            double targetX = -currentIndex * 327; // 160 = kích thước hình ảnh (150) + khoảng cách (10)
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), lastList);
            transition.setToX(targetX);
            transition.play();
        }
    }
    private void nextImage() {
        if (currentIndex < lastList.getChildren().size() - 3) {
            currentIndex++;
            double targetX = - currentIndex * 327; // 160 = kích thước hình ảnh (150) + khoảng cách (10)
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), lastList);
            transition.setToX(targetX);
            transition.play();
        }
    }
}
