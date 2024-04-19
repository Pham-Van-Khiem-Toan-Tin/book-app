package com.book.app.Entity;

public class CategoryEntity extends BaseEntity{
    private String id;
    private boolean containsBook;

    public boolean isContainsBook() {
        return containsBook;
    }

    public void setContainsBook(boolean containsBook) {
        this.containsBook = containsBook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
