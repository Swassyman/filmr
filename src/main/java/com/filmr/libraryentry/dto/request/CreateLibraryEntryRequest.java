package com.filmr.libraryentry.dto.request;

import com.filmr.library.enums.WatchStatus;

public record CreateLibraryEntryRequest(
    Long userId,
    Long mediaId,
    boolean isFavorite,
    WatchStatus watchStatus,
    Integer rating,
    String review) {}
