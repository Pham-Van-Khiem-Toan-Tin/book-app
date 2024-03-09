package com.book.app.Utils;

import com.book.app.Entity.User;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Comparator;

public class SortUtils {
    public static SortedList<User> getSortList(String sortType, ObservableList<User> tableList) {
        SortedList<User> sortedData = new SortedList<>(tableList);
        sortedData.comparatorProperty().unbind();
        if (!sortType.isEmpty()) {
            switch (sortType) {
                case "id: Ascending":
                    sortedData.setComparator(Comparator.comparingInt(User::getId));
                    break;
                case "username: A-Z":
                    sortedData.setComparator(Comparator.comparing(User::getUsername));
                    break;
                case "created_at: Ascending":
                    sortedData.setComparator(Comparator.comparing(User::getCreatedAt));
                    break;
                default:
                    sortedData.setComparator(null);
                    break;

            }
        }
        return sortedData;
    }
}
