package com.book.app.Entity;

public class AuthorEntity extends BaseEntity{
    private String id;
    private String image_url;
    private String image_public_id;
    private boolean containsBook;

    public boolean isContainsBook() {
        return containsBook;
    }

    public void setContainsBook(boolean containsBook) {
        this.containsBook = containsBook;
    }

    public String getImage_public_id() {
        return image_public_id;
    }

    public void setImage_public_id(String image_public_id) {
        this.image_public_id = image_public_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
