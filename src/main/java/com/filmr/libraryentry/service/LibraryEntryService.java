package com.filmr.libraryentry.service;

import com.filmr.library.enums.WatchStatus;
import com.filmr.libraryentry.dto.request.CreateLibraryEntryRequest;
import com.filmr.libraryentry.dto.response.LibraryEntryResponse;
import com.filmr.libraryentry.model.LibraryEntry;
import com.filmr.libraryentry.repository.LibraryEntryRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LibraryEntryService {
  private final LibraryEntryRepository libraryEntryRepository;

  LibraryEntryService(LibraryEntryRepository libraryEntryRepository) {
    this.libraryEntryRepository = libraryEntryRepository;
  }

  public LibraryEntryResponse addLibraryEntry(CreateLibraryEntryRequest request) {
    if (libraryEntryRepository.existsByMediaId(request.mediaId())) {
      throw new RuntimeException("Entry with this ID already exists");
    }

    LibraryEntry entry = new LibraryEntry();

    if (request.watchStatus() == WatchStatus.WATCHED) {
      entry.setCompletedAt(Instant.now());
    } else if (request.watchStatus() == WatchStatus.WATCHING) {
      entry.setStartedAt(Instant.now());
    }

    libraryEntryRepository.save(entry);
    return LibraryEntryResponse.from(entry);
  }

  public List<LibraryEntryResponse> getLibraryEntries(Long userId) {
    return libraryEntryRepository.findAllByUserId(userId).stream()
        .map(LibraryEntryResponse::from)
        .toList();
  }

  public LibraryEntry getLibraryEntryById(Long id) {
    return libraryEntryRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Entry doesn't exist"));
  }

  public LibraryEntry updateLibraryEntry(Long id, LibraryEntry updatedLibraryEntry) {
    LibraryEntry entry = getLibraryEntryById(id);

    if (updatedLibraryEntry.getMediaType() != null)
      entry.setMediaType(updatedLibraryEntry.getMediaType());
    if (updatedLibraryEntry.getRating() != null) entry.setRating(updatedLibraryEntry.getRating());
    entry.setFavorite(updatedLibraryEntry.isFavorite());
    if (updatedLibraryEntry.getReview() != null) entry.setReview(updatedLibraryEntry.getReview());

    // todo: keeping history of everytime you watch something
    if (entry.getWatchStatus() != updatedLibraryEntry.getWatchStatus()
        && updatedLibraryEntry.getWatchStatus() == WatchStatus.WATCHED) {
      entry.setCompletedAt(Instant.now());
    }

    return libraryEntryRepository.save(entry);
  }

  public void deleteLibraryEntry(Long id) {
    LibraryEntry entry = getLibraryEntryById(id);

    libraryEntryRepository.delete(entry);
  }
}
