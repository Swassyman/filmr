package com.filmr.library.userMovie.model;

import com.filmr.library.enums.STATUS;

import java.util.Date;

public class UserMovie {
    private Long id;
    private Long userId;
    private Long movieId;
    private int rating;
    private STATUS status;
    private String review;
    private Date watchedDate;
    private boolean favourite;


    public UserMovie() {}

    public UserMovie(Long id, Long userId, Long movieId) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(Date watchedDate) {
        this.watchedDate = watchedDate;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
