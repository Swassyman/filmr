package com.filmr.library.userShow.service;

import com.filmr.library.userShow.model.UserShow;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserShowService {
	private final List<UserShow> userShows = new ArrayList<>();

	public List<UserShow> getUserShowsById(Long userId) {
		List<UserShow> shows = new ArrayList<>();
		for (UserShow userShow : userShows) {
			if (userShow.getId().equals(userId)) {
				shows.add(userShow);
			}
		}

		return shows;
	}

	public ResponseEntity<UserShow> addShowEntry(UserShow userShow) {
		userShows.add(userShow);
		return ResponseEntity.ok(userShow);
	}

	public ResponseEntity<UserShow> updateShowEntry(UserShow userShow) {
		Long showId = userShow.getId();
		for (UserShow show : userShows) {
			if (show.getId().equals(showId)) {
				userShows.remove(show);
				userShows.add(userShow);
			}
		}

		return ResponseEntity.ok(userShow);
	}

	public ResponseEntity<List<UserShow>> deleteShowEntry(Long showId) {
		userShows.removeIf(show -> show.getId().equals(showId));

		return ResponseEntity.ok(userShows);
	}
}
