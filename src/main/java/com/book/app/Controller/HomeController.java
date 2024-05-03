package com.book.app.Controller;

import com.book.app.MainApplication;
import com.book.app.Utils.AppUtils;
import com.book.app.Utils.TokenUtil;
import com.book.app.Utils.UIUtils;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private String rootDirectory = "/com/book/app/";
    @FXML
    private Text username;
    @FXML
    private ScrollPane lastSlider;
    @FXML
    private ScrollPane scrollHotList;
    @FXML
    private Button nextButton1;
    @FXML
    private Button preButton1;
    @FXML
    private Button nextButton2;
    @FXML
    private Button preButton2;
    @FXML
    private Circle author1, author2, author3;
    @FXML
    private Button logout, btnCategory, btnAuthor, btnPublisher;
    private int currentIndex = 0;
    private int currentIndexHot = 0;

    private HBox lastList;
    private HBox hotList;
    private static final String[] IMAGE_URLS = {
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\phianambiengioiphiataymattroi.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\phiasaunghican.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\sucuuroicuathanhnu.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\tazakitsukurukhongmauvanhungnam.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\traitimcuabrutus.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\vuanmangoluquankairotei.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\1q84.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\conchuanho.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\dieukydieucuatiemtaphoanamiya.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\harrypotter.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\laudaibaycuaphapsuhowl.png",
            "C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\nhungchuyenlaotokyo.png"
    };
    private static final String[] nameBooks = {
        "phia nam bien gioi phia tay mat troi",
            "phia sau nghi can",
            "su cuu roi cua thanh nu",
            "tazakit sukuru khong mau van hung nam",
            "trai tim cua brutus",
            "vu an mang o lu quan kairotei",
            "1q84",
            "con chua nho",
            "dieu ky dieu cua tiem tap hoa namiya",
            "harrypotter",
            "lau dai bay cua phap su howl",
            "nhung chuyen la o tokyo"
    };
    private static final String[] authorBooks = {
            "HARUKI MURAKAMI",
            "HIGASHINO KEIGO",
            "HIGASHINO KEIGO",
            "HARUKI MURAKAMI",
            "HIGASHINO KEIGO",
            "HIGASHINO KEIGO",
            "HARUKI MURAKAMI",
            "FRANCES HODGSON BURNETT",
            "HIGASHINO KEIGO",
            "J.K.ROWLING",
            "DIANA WYNNE JONES",
            "HARUKI MURAKAMI"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(AppUtils.getUsername());
        lastList = new HBox();
//        lastList.setAlignment(Pos.CENTER);
//        lastList.setSpacing(20);
//        for (int i = 0; i < 6; i++) {
//            String imageUrl = IMAGE_URLS[i];
//            String nameBook = nameBooks[i];
//            String authorBook = authorBooks[i];
//            Image image = null;
//            try {
//                image = new Image(new FileInputStream(imageUrl));
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            ImageView imageView1 = new ImageView(image);
//            imageView1.setFitWidth(317);
//            imageView1.setFitHeight(134);
//            BoxBlur blur = new BoxBlur();
//            blur.setWidth(10);
//            blur.setHeight(10);
//            blur.setIterations(3);
//            imageView1.setEffect(blur);
//            Rectangle clip1 = new Rectangle(imageView1.getFitWidth(), imageView1.getFitHeight());
//            clip1.setArcWidth(20);
//            clip1.setArcHeight(20);
//            imageView1.setClip(clip1);
//            Text name = new Text(nameBook.toUpperCase());
//            name.setStyle("-fx-fill: #fff; -fx-font-weight: 700; -fx-font-size: 16px; -fx-font-family: 'Segoe UI';");
//            TextFlow nameFlow = new TextFlow(name);
//            nameFlow.setPrefWidth(152);
//            nameFlow.setPrefHeight(46);
//
//            nameFlow.setLayoutX(159);
//            nameFlow.setLayoutY(14);
//            Text author = new Text(authorBook.toUpperCase());
//            author.setStyle("-fx-fill: #fff; -fx-font-weight: 700;-fx-font-family: 'Segoe UI';");
//            TextFlow authorFlow = new TextFlow(author);
//            authorFlow.setPrefWidth(129);
//            authorFlow.setPrefHeight(21);
//            authorFlow.setLayoutX(159);
//            authorFlow.setLayoutY(77);
//            Pane paneText = new Pane(nameFlow, authorFlow);
//            StackPane stackPane1 = new StackPane(imageView1, paneText);
//            stackPane1.setPrefSize(317, 134);
//            stackPane1.setLayoutY(62);
//            stackPane1.setStyle("-fx-background-radius: 20px");
//            stackPane1.setCursor(Cursor.HAND);
//            ImageView imageView2 = null;
//            try {
//                imageView2 = new ImageView(new Image(new FileInputStream(imageUrl)));
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            imageView2.setFitWidth(116);
//            imageView2.setFitHeight(181);
//            StackPane stackPane2 = new StackPane(imageView2);
//            Rectangle clip2 = new Rectangle(imageView2.getFitWidth(), imageView2.getFitHeight());
//            clip2.setArcWidth(20);
//            clip2.setArcHeight(20);
//            imageView2.setClip(clip2);
//            stackPane2.setPrefSize(116, 181);
//            stackPane2.setLayoutX(26);
//            stackPane2.setCursor(Cursor.HAND);
//            Pane pane = new Pane(stackPane1, stackPane2);
//            lastList.getChildren().add(pane);
//        }
//        lastSlider.setContent(lastList);
//        lastSlider.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        nextButton1.setOnAction(event -> nextImage());
//        preButton1.setOnAction(actionEvent -> preImage());
//
//        hotList = new HBox();
//        hotList.setAlignment(Pos.CENTER);
//        hotList.setSpacing(20);
//        for (int i = 6; i < IMAGE_URLS.length; i++) {
//            String urlImage = IMAGE_URLS[i];
//            Image image = null;
//            try {
//                image = new Image(new FileInputStream(urlImage));
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(180);
//            imageView.setFitHeight(250);
//            Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
//            clip.setArcHeight(20);
//            clip.setArcWidth(20);
//            imageView.setClip(clip);
//            Pane paneImage = new Pane(imageView);
//            paneImage.setPrefSize(180,250);
//            paneImage.setLayoutX(10);
//            paneImage.setLayoutX(10);
//            Pane paneContainer = new Pane();
//            paneContainer.setCursor(Cursor.HAND);
//            paneContainer.getChildren().add(paneImage);
//            hotList.getChildren().add(paneContainer);
//        }
//        scrollHotList.setContent(hotList);
//        scrollHotList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        nextButton2.setOnAction(event -> nextImageHot());
//        preButton2.setOnAction(actionEvent -> preImageHot());
//        Image imgAuthor1 = null;
//        try {
//            imgAuthor1 = new Image(new FileInputStream("C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\jkrowling.jpg"));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        Image imgAuthor2 = null;
//        try {
//            imgAuthor2 = new Image(new FileInputStream("C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\stevenking.jpg"));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        Image imgAuthor3 = null;
//        try {
//            imgAuthor3 = new Image(new FileInputStream("C:\\Users\\KhiemJP\\Desktop\\Code\\book-app\\book-app\\src\\main\\resources\\com\\book\\app\\static\\images\\danielSteel.jpg"));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        author1.setFill(new ImagePattern(imgAuthor1));
//        author2.setFill(new ImagePattern(imgAuthor2));
//        author3.setFill(new ImagePattern(imgAuthor3));
//        logout.setOnAction(event -> {
//            try {
//                AppUtils.clearData();
//                TokenUtil.deleteToken();
//                handleSwitchOtherScene(event, "login/authen.fxml", null );
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
        btnCategory.setOnAction(event -> {
            try {
                handleSwitchOtherScene(event, "category/category.fxml", "static/css/category/category.css");
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
        btnAuthor.setOnAction(event -> {
            try {
                UIUtils.handleSwitchOtherScene(event, "author/authors.fxml", "static/css/author/authors.css");
            } catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException();
            }
        });
        btnPublisher.setOnAction(event -> {
            try {
                UIUtils.handleSwitchOtherScene(event, "publisher/publisher.fxml", "static/css/publisher/publisher.css");
            } catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException();
            }
        });
    }

    private void preImageHot() {
        if (currentIndexHot >  0) {
            currentIndexHot--;
            double targetX = -currentIndexHot * 200; // 160 = kích thước hình ảnh (150) + khoảng cách (10)
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), hotList);
            transition.setToX(targetX);
            transition.play();
        }
    }
    private void nextImageHot() {
        if (currentIndexHot < hotList.getChildren().size() - 4) {
            currentIndexHot++;
            double targetX = - currentIndexHot * 200; // 160 = kích thước hình ảnh (150) + khoảng cách (10)
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), hotList);
            transition.setToX(targetX);
            transition.play();
        }
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
    private void handleSwitchOtherScene(ActionEvent event, String directoryClass, String directoryCss) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rootDirectory  + directoryClass));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            if (directoryCss != null) {
                scene.getStylesheets().add(getClass().getResource(rootDirectory + directoryCss).toExternalForm());
            }
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
