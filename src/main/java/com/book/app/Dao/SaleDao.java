package com.book.app.Dao;

import com.book.app.Entity.SaleEntity;

import java.time.LocalDate;
import java.util.List;

public interface SaleDao {
    List<SaleEntity> getAllRevenueOfBook(String type, LocalDate startDate, LocalDate endDate);
    List<SaleEntity> getAllRevenueOfAuthor(String type, LocalDate startDate, LocalDate endDate);
    List<SaleEntity> getAllRevenueOfCategory(String type, LocalDate startDate, LocalDate endDate);
    List<SaleEntity> getAllRevenueOfPublisher(String type, LocalDate startDate, LocalDate endDate);
    List<SaleEntity> getAllRevenueOfEmployee(String type, LocalDate startDate, LocalDate endDate);

}
