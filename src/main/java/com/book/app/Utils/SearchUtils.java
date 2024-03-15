package com.book.app.Utils;

import com.book.app.Dao.impl.EmployeeDaoImpl;
import com.book.app.Entity.EmployeeEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchUtils {
    private static EmployeeDaoImpl dao = new EmployeeDaoImpl();
    public static ObservableList<EmployeeEntity> getAllUserOldSearch(String oldSearch) {
        ObservableList<EmployeeEntity> usersList = FXCollections.observableArrayList();
        List<EmployeeEntity> users = dao.getAllEmployee();
        if (!oldSearch.isEmpty()) {
            Pattern pattern = Pattern.compile(oldSearch, Pattern.CASE_INSENSITIVE);
            usersList = users
                    .stream()
                    .filter(user -> {
                                String admin = user.getAdmin() ? "admin" : "user";
                                return pattern.matcher(user.getUsername()).find() ||
                                        pattern.matcher(user.getPhone()).find() ||
                                        pattern.matcher(String.valueOf(user.getId())).find() ||
                                        pattern.matcher(user.getEmail()).find() ||
                                        pattern.matcher(admin).find() ||
                                        pattern.matcher(DateUtils
                                                .convertLocalDateToStringPattern(user.getCreatedAt(), "dd/MM/yyyy")).find();
                            }
                    )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        } else {
            usersList = FXCollections.observableArrayList(users);
        }
        return usersList;
    }
}
