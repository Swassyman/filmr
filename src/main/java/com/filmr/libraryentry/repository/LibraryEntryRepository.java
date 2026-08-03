package com.filmr.libraryentry.repository;

import com.filmr.libraryentry.model.LibraryEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {
  List<LibraryEntry> findAllByUserId(Long userId);
}
