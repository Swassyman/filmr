package com.filmr.library.userShow.controller;

import com.filmr.library.userShow.model.UserShow;
import com.filmr.library.userShow.service.UserShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library/shows")
public class UserShowController {
	private final UserShowService userShowService;

	public UserShowController(UserShowService userShowService) {
		this.userShowService = userShowService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<UserShow>> findById(@PathVariable Long id) {
		List<UserShow> shows = userShowService.getUserShowsById(id);

		return ResponseEntity.ok(shows);
	}

	@PostMapping("/{id}")
	public ResponseEntity<UserShow> addShowEntry(@PathVariable Long userId, UserShow userShow) {
		userShowService.addShowEntry(userShow);
		return ResponseEntity.ok(userShow);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserShow> updateShowEntry(@PathVariable Long userId, UserShow userShow) {
		userShowService.updateShowEntry(userShow);
		return ResponseEntity.ok(userShow);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<UserShow> deleteShowEntry(@PathVariable Long userId, UserShow userShow) {
		userShowService.deleteShowEntry(userId);
		return ResponseEntity.ok(userShow);
	}
}
