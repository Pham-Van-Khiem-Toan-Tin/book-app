package com.book.app.Utils;

import com.book.app.Dao.impl.UserImpl;
import com.book.app.Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchUtils {
    private static UserImpl dao = new UserImpl();
    public static ObservableList<User> getAllUserOldSearch(String oldSearch) {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        List<User> users = dao.getAllUser();
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
