package com.filmr.libraryentry.controller;

import com.filmr.libraryentry.model.LibraryEntry;
import com.filmr.libraryentry.service.LibraryEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
public class LibraryEntryController {

	private final LibraryEntryService libraryEntryService;

	LibraryEntryController(LibraryEntryService libraryEntryService) {
		this.libraryEntryService = libraryEntryService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<List<LibraryEntry>> getLibraryEntries(@PathVariable Long userId) {
		return ResponseEntity.ok(libraryEntryService.getLibraryEntries(userId));
	}

	@GetMapping("/entry/{id}")
	public ResponseEntity<LibraryEntry> getLibraryEntryById(@PathVariable Long id) {
		return ResponseEntity.ok(libraryEntryService.getLibraryEntryById(id));
	}

	@PostMapping("/entry")
	public ResponseEntity<LibraryEntry> addLibraryEntry(@RequestBody LibraryEntry entry) {
		return ResponseEntity.ok(libraryEntryService.addLibraryEntry(entry));
	}

	@PatchMapping("/entry/{id}")
	public ResponseEntity<LibraryEntry> updateLibraryEntry(
			@PathVariable Long id, @RequestBody LibraryEntry entry) {
		return ResponseEntity.accepted().body(libraryEntryService.updateLibraryEntry(id, entry));
	}

	@DeleteMapping("/entry/{id}")
	public ResponseEntity<Void> deleteLibraryEntry(@PathVariable Long id) {
		libraryEntryService.deleteLibraryEntry(id);
		return ResponseEntity.noContent().build();
	}
}
