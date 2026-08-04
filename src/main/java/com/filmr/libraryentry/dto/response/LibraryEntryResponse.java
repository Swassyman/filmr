package com.filmr.libraryentry.dto.response;

import com.filmr.library.enums.WatchStatus;
import com.filmr.libraryentry.model.LibraryEntry;

public record LibraryEntryResponse(
    Long id,
    Long userId,
    Long mediaId,
    boolean isFavorite,
    WatchStatus watchStatus,
    Integer rating,
    String review) {
  public static LibraryEntryResponse from(LibraryEntry e) {
    return new LibraryEntryResponse(
        e.getId(),
        e.getUser().getId(),
        e.getMediaId(),
        e.isFavorite(),
        e.getWatchStatus(),
        e.getRating(),
        e.getReview());
  }
}
