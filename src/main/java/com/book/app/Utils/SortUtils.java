package com.book.app.Utils;

import com.book.app.Entity.EmployeeEntity;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Comparator;

public class SortUtils {
    public static SortedList<EmployeeEntity> getSortList(String sortType, ObservableList<EmployeeEntity> tableList) {
        SortedList<EmployeeEntity> sortedData = new SortedList<>(tableList);
        sortedData.comparatorProperty().unbind();
        if (!sortType.isEmpty()) {
            switch (sortType) {
                case "id: Ascending":
                    sortedData.setComparator(Comparator.comparingInt(EmployeeEntity::getId));
                    break;
                case "username: A-Z":
                    sortedData.setComparator(Comparator.comparing(EmployeeEntity::getUsername));
                    break;
                case "created_at: Ascending":
                    sortedData.setComparator(Comparator.comparing(EmployeeEntity::getCreatedAt));
                    break;
                default:
                    sortedData.setComparator(null);
                    break;

            }
        }
        return sortedData;
    }
}
