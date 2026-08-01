package com.filmr.library.userShow.model;

import com.filmr.library.enums.STATUS;

import java.util.Date;

public class UserShow {
    private Long id;
    private Long userId;
    private Long movieId;
    private int rating;
    private STATUS status;
    private String review;
    private Date watchedDate;
    private boolean favourite;


    public UserShow() {
    }

    public UserShow(Long id, Long userId, Long movieId) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
    }
}
