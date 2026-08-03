package com.filmr.libraryentry.service;

import com.filmr.libraryentry.model.LibraryEntry;
import com.filmr.libraryentry.repository.LibraryEntryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LibraryEntryService {
  private LibraryEntryRepository libraryEntryRepository;

  public LibraryEntry addLibraryEntry(LibraryEntry libraryEntry) {
    if (libraryEntryRepository.existsById(libraryEntry.getId())) {
      throw new RuntimeException("Entry with this ID already exists");
    }

    return libraryEntryRepository.save(libraryEntry);
  }

  public List<LibraryEntry> getLibraryEntries(Long userId) {
    return libraryEntryRepository.findAllByUserId(userId);
  }

  public LibraryEntry getLibraryEntryById(Long id) {
    return libraryEntryRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Entry doesn't exist"));
  }

  public void updateLibraryEntry(Long id, LibraryEntry updatedLibraryEntry) {
    LibraryEntry entry = getLibraryEntryById(id);

    if (updatedLibraryEntry.getMediaType() != null)
      entry.setMediaType(updatedLibraryEntry.getMediaType());
    if (updatedLibraryEntry.getStatus() != null) entry.setStatus(updatedLibraryEntry.getStatus());
    if (updatedLibraryEntry.getRating() != null) entry.setRating(updatedLibraryEntry.getRating());
    entry.setFavorite(updatedLibraryEntry.isFavorite());
    if (updatedLibraryEntry.getReview() != null) entry.setReview(updatedLibraryEntry.getReview());

    libraryEntryRepository.save(entry);
  }

  public void deleteLibraryEntry(Long id) {
    LibraryEntry entry = getLibraryEntryById(id);

    libraryEntryRepository.delete(entry);
  }
}
