package com.filmr.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.filmr.user.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
@DisplayName("UserRepository")
class UserRepositoryTest {

  @Autowired private TestEntityManager em;
  @Autowired private UserRepository userRepository;

  // ─── helpers ────────────────────────────────────────────────────────────────

  private User persistUser(String name, String email, String passHash) {
    User u = new User();
    u.setName(name);
    u.setEmail(email);
    u.setPassHash(passHash);
    return em.persistAndFlush(u);
  }

  @BeforeEach
  void cleanUp() {
    userRepository.deleteAll();
    em.flush();
    em.clear();
  }

  // ─── findByEmail ─────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("findByEmail()")
  class FindByEmail {

    @Test
    @DisplayName("returns user when email matches exactly")
    void returnsUser_whenEmailExists() {
      persistUser("Alice", "alice@test.com", "hash");

      Optional<User> result = userRepository.findByEmail("alice@test.com");

      assertThat(result).isPresent();
      assertThat(result.get().getName()).isEqualTo("Alice");
      assertThat(result.get().getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    @DisplayName("returns empty when email doesn't match")
    void returnsEmpty_whenEmailNotFound() {
      Optional<User> result = userRepository.findByEmail("nobody@test.com");

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("is case-sensitive — uppercase email does not match stored lowercase email")
    void isCaseSensitive() {
      persistUser("Alice", "alice@test.com", "hash");

      Optional<User> result = userRepository.findByEmail("ALICE@TEST.COM");

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("returns empty when the DB is empty")
    void returnsEmpty_whenTableIsEmpty() {
      Optional<User> result = userRepository.findByEmail("any@test.com");

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("returns correct user when multiple users exist")
    void returnsCorrectUser_whenMultipleUsersExist() {
      persistUser("Bob", "alice@test.com", "h1");
      persistUser("Bob", "bob@test.com", "h2");

      Optional<User> result = userRepository.findByEmail("bob@test.com");

      assertThat(result).isPresent();
      assertThat(result.get().getName()).isEqualTo("Bob");
    }
  }

  // ─── existsByEmail ────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("existsByEmail()")
  class ExistsByEmail {

    @Test
    @DisplayName("returns true when email exists")
    void returnsTrue_whenEmailExists() {
      persistUser("Carol", "carol@test.com", "hash");

      assertThat(userRepository.existsByEmail("carol@test.com")).isTrue();
    }

    @Test
    @DisplayName("returns false when email doesn't exist")
    void returnsFalse_whenEmailNotFound() {
      assertThat(userRepository.existsByEmail("ghost@test.com")).isFalse();
    }

    @Test
    @DisplayName("returns false when table is empty")
    void returnsFalse_whenTableIsEmpty() {
      assertThat(userRepository.existsByEmail("any@test.com")).isFalse();
    }

    @Test
    @DisplayName("returns true for one of multiple persisted emails")
    void returnsTrue_whenOneOfManyEmails() {
      persistUser("Dave", "dave@test.com", "h1");
      persistUser("Eve", "eve@test.com", "h2");

      assertThat(userRepository.existsByEmail("eve@test.com")).isTrue();
      assertThat(userRepository.existsByEmail("dave@test.com")).isTrue();
      assertThat(userRepository.existsByEmail("nobody@test.com")).isFalse();
    }
  }

  // ─── inherited JpaRepository methods ─────────────────────────────────────────

  @Nested
  @DisplayName("save()")
  class Save {

    @Test
    @DisplayName("persists user and assigns a generated id")
    void persistsUser_andAssignsId() {
      User u = new User();
      u.setName("Frank");
      u.setEmail("frank@test.com");
      u.setPassHash("hash");

      User saved = userRepository.save(u);

      assertThat(saved.getId()).isNotNull().isPositive();
    }

    @Test
    @DisplayName("persisted user can be retrieved by id")
    void savedUser_canBeRetrievedById() {
      User u = new User();
      u.setName("Gina");
      u.setEmail("gina@test.com");
      u.setPassHash("hash");
      User saved = userRepository.save(u);
      em.clear();

      Optional<User> found = userRepository.findById(saved.getId());

      assertThat(found).isPresent();
      assertThat(found.get().getEmail()).isEqualTo("gina@test.com");
    }

    @Test
    @DisplayName("auditable timestamps are populated on save")
    void auditTimestamps_arePopulated() {
      User u = new User();
      u.setName("Hank");
      u.setEmail("hank@test.com");
      u.setPassHash("hash");
      User saved = userRepository.save(u);
      em.flush();

      assertThat(saved.getCreatedAt()).isNotNull();
      assertThat(saved.getUpdatedAt()).isNotNull();
    }
  }

  @Nested
  @DisplayName("findAll()")
  class FindAll {

    @Test
    @DisplayName("returns empty list when table is empty")
    void returnsEmpty_whenTableIsEmpty() {
      assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("returns all persisted users")
    void returnsAllUsers() {
      persistUser("Ivy", "ivy@test.com", "h1");
      persistUser("Jake", "jake@test.com", "h2");

      List<User> all = userRepository.findAll();

      assertThat(all).hasSize(2);
    }
  }

  @Nested
  @DisplayName("delete()")
  class Delete {

    @Test
    @DisplayName("user is no longer findable after deletion")
    void userNotFound_afterDeletion() {
      User u = persistUser("Leo", "leo@test.com", "hash");
      Long id = u.getId();

      userRepository.delete(u);
      em.flush();
      em.clear();

      assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("only the targeted user is deleted, others remain")
    void onlyTargetedUserDeleted() {
      User u1 = persistUser("Mia", "mia@test.com", "h1");
      persistUser("Ned", "ned@test.com", "h2");

      userRepository.delete(u1);
      em.flush();
      em.clear();

      assertThat(userRepository.findAll()).hasSize(1);
      assertThat(userRepository.existsByEmail("ned@test.com")).isTrue();
      assertThat(userRepository.existsByEmail("mia@test.com")).isFalse();
    }
  }
}
