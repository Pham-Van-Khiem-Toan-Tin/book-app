package com.book.app.Utils;

import java.util.UUID;

public class UUIDUtils {
    public static String generateUniqueId(String name) {
        // Sử dụng hàm băm UUID để tạo id duy nhất từ tên tác giả
        String combinedString = name + System.currentTimeMillis();
        String uniqueId = UUID.nameUUIDFromBytes(combinedString.getBytes()).toString();
        return uniqueId;
    }
}
