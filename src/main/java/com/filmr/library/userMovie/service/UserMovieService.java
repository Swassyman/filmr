package com.filmr.library.userMovie.service;

import com.filmr.library.userMovie.model.UserMovie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMovieService {
    private final List<UserMovie> userMovies = new ArrayList<>();

    public List<UserMovie> getMoviesByUserId(Long userId) {
        List<UserMovie> movies = new ArrayList<>();
        for(UserMovie userMovie : userMovies){
            if(userMovie.getUserId().equals(userId)){
                movies.add(userMovie);
            }
        }

        return movies;
    }

    public ResponseEntity<UserMovie> addMovieEntry(UserMovie userMovie) {
        userMovies.add(userMovie);
        return ResponseEntity.ok(userMovie);
    }

    public ResponseEntity<UserMovie> updateMovieEntry(UserMovie userMovie) {
        Long movieId = userMovie.getId();
        for(UserMovie movie:  userMovies){
            if(movie.getId().equals(movieId)){
                userMovies.remove(movie);
                userMovies.add(userMovie);
            }
        }

        return ResponseEntity.ok(userMovie);
    }

    public ResponseEntity<List<UserMovie>> deleteEntry(Long movieId) {
        userMovies.removeIf(movie -> movie.getId().equals(movieId));

        return ResponseEntity.ok(userMovies);
    }
}
