package com.filmr.library.userMovie.controller;

import com.filmr.library.userMovie.model.UserMovie;
import com.filmr.library.userMovie.service.UserMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library/movies")
public class UserMovieController {
    private final UserMovieService userMovieService;

    @Autowired
    public UserMovieController(UserMovieService userMovieService) {
        this.userMovieService = userMovieService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<UserMovie>> findById(@PathVariable Long id){
        List<UserMovie> userMovies = userMovieService.getMoviesByUserId(id);

        if(userMovies.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMovies);
    }


    @PostMapping("/{id}")
    public ResponseEntity<UserMovie> addEntry(@PathVariable Long id, @RequestBody UserMovie userMovie) {
        return userMovieService.addMovieEntry(userMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserMovie> updateEntry(@PathVariable Long id, @RequestBody UserMovie userMovie) {
        return userMovieService.updateMovieEntry(userMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<UserMovie>> deleteEntry(@PathVariable Long id, @RequestBody UserMovie userMovie) {
        return userMovieService.deleteEntry(userMovie.getMovieId());
    }

}
