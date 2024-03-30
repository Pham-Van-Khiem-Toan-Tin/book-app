package com.book.app.Dao;

import com.book.app.Entity.PublisherEntity;

import java.util.List;

public interface PublisherDao {
    List<PublisherEntity> getAllPublisher(String keyword, String sort);
    boolean addPublisher(PublisherEntity publisher);
    boolean editPublisher(PublisherEntity publisher);
    boolean lockOrUnLockPublisher(String id, Boolean enable);
}
