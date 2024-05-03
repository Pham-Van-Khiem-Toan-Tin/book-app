package com.book.app.Utils;

public class AppUtils {
    private static String id;
    private static String username;
    private static String image;
    private static String role;

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        AppUtils.id = id;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        AppUtils.username = username;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        AppUtils.image = image;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        AppUtils.role = role;
    }

    public static void clearData() {
        AppUtils.setUsername("");
        AppUtils.setRole("");
        AppUtils.setImage("");
    }
}
