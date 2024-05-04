package com.book.app.Controller.admin.sale;

import com.book.app.Dao.impl.SaleDaoImpl;
import com.book.app.Entity.SaleEntity;
import com.book.app.Utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SaleCategoryController implements Initializable {
    @FXML
    BarChart<String, Number> chartRevenue;
    @FXML
    ComboBox<String> statisticsFollow;
    @FXML
    DatePicker startDate, endDate;
    @FXML
    private Text textWelcome, textUsername;
    @FXML
    private ComboBox<String> choiceBoxLogout;
    @FXML
    private Button  btnUser, btnSaleBook, btnSaleCategory, btnSalePublisher, btnSaleCustomer, btnSaleAuthor, btnSaleEmployee;
    private SaleDaoImpl dao = new SaleDaoImpl();
    private XYChart.Series<String, Number> series;
    private CategoryAxis xAxis = new CategoryAxis();
    private NumberAxis yAxis = new NumberAxis();

    private void changeDataChart() {

        List<SaleEntity> saleEntityList = new ArrayList<>();
        List<SaleEntity> revenueList = dao.getAllRevenueOfCategory(statisticsFollow.getValue(), null, null);
        chartRevenue.getData().clear();
        chartRevenue.setAnimated(true);
        series = new XYChart.Series();
        if (startDate.getValue() != null && endDate.getValue() != null) {
            LocalDate startDateVal = startDate.getValue();
            LocalDate endDateVal = endDate.getValue();
            revenueList = dao.getAllRevenueOfBook(statisticsFollow.getValue(), startDateVal, endDateVal);
        }
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(saleEntityList.stream().map(SaleEntity::getLabel).collect(Collectors.toList())));
        for (SaleEntity revenue : revenueList) {
            series.getData().add(new XYChart.Data<>(revenue.getLabel(), revenue.getRevenue()));
        }
        series.setName("Revenue");
        chartRevenue.getData().addAll(series);
        chartRevenue.setAnimated(false);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UIUtils.setupUIElements(textWelcome, textUsername, choiceBoxLogout);
        UIUtils.setupMenuEmployee(btnUser, btnSaleBook, btnSaleCategory, btnSalePublisher, btnSaleAuthor, btnSaleCustomer, btnSaleEmployee);

        statisticsFollow.getItems().addAll("Day", "Week", "Month", "Year");
        statisticsFollow.setValue("Day");
        statisticsFollow.setOnAction(event -> {
            startDate.setValue(null);
            endDate.setValue(null);
            changeDataChart();
        });


        chartRevenue.setTitle("Revenue according to books");
        chartRevenue.getXAxis().setAutoRanging(true);
        chartRevenue.getYAxis().setAutoRanging(true);

        changeDataChart();
        startDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Nếu ngày mới được chọn (newValue) nhỏ hơn ngày kết thúc hiện tại, không làm gì cả
            if (newValue != null && endDate.getValue() != null && newValue.isBefore(endDate.getValue())) {
                return;
            }
            if (newValue != null && endDate.getValue() != null && newValue.isAfter(endDate.getValue())) {
                endDate.setValue(newValue.plusDays(1));
            }
            // Nếu ngày mới được chọn (newValue) lớn hơn ngày kết thúc hiện tại hoặc endDate chưa được chọn, cập nhật endDate
            if (newValue != null) {
                endDate.setValue(newValue.plusDays(1)); // Đặt ngày kết thúc là ngày tiếp theo sau ngày bắt đầu
            }
        });
        endDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Nếu ngày mới được chọn (newValue) nhỏ hơn ngày kết thúc hiện tại, không làm gì cả
            if (newValue != null && startDate.getValue() != null && newValue.isAfter(startDate.getValue())) {
                return;
            }
            if (newValue != null && startDate.getValue() != null && newValue.isBefore(startDate.getValue())) {
                startDate.setValue(newValue.minusDays(1));
            }
            // Nếu ngày mới được chọn (newValue) lớn hơn ngày kết thúc hiện tại hoặc endDate chưa được chọn, cập nhật endDate
            if (newValue != null) {
                startDate.setValue(newValue.minusDays(1)); // Đặt ngày kết thúc là ngày tiếp theo sau ngày bắt đầu
            }
        });
        startDate.setOnAction(event -> {
            changeDataChart();
        });
        endDate.setOnAction(event -> {
            changeDataChart();
        });

    }
}
