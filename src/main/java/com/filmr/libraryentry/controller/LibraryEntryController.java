package com.filmr.libraryentry.controller;

import com.filmr.libraryentry.dto.request.CreateLibraryEntryRequest;
import com.filmr.libraryentry.dto.response.LibraryEntryResponse;
import com.filmr.libraryentry.model.LibraryEntry;
import com.filmr.libraryentry.service.LibraryEntryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library")
public class LibraryEntryController {

  private final LibraryEntryService libraryEntryService;

  LibraryEntryController(LibraryEntryService libraryEntryService) {
    this.libraryEntryService = libraryEntryService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<LibraryEntryResponse>> getLibraryEntries(@PathVariable Long id) {
    return ResponseEntity.ok(libraryEntryService.getLibraryEntries(id));
  }

  @GetMapping("/entry/{id}")
  public ResponseEntity<LibraryEntry> getLibraryEntryById(@PathVariable Long id) {
    return ResponseEntity.ok(libraryEntryService.getLibraryEntryById(id));
  }

  @PostMapping("/entry")
  public ResponseEntity<LibraryEntryResponse> addLibraryEntry(
      @RequestBody CreateLibraryEntryRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(libraryEntryService.addLibraryEntry(request));
  }

  @PatchMapping("/entry/{id}")
  public ResponseEntity<LibraryEntry> updateLibraryEntry(
      @PathVariable Long id, @RequestBody LibraryEntry entry) {
    return ResponseEntity.ok(libraryEntryService.updateLibraryEntry(id, entry));
  }

  @DeleteMapping("/entry/{id}")
  public ResponseEntity<Void> deleteLibraryEntry(@PathVariable Long id) {
    libraryEntryService.deleteLibraryEntry(id);
    return ResponseEntity.noContent().build();
  }
}
