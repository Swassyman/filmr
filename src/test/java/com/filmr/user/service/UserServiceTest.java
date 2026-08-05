package com.filmr.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.filmr.user.dto.request.CreateUserRequest;
import com.filmr.user.dto.request.UpdateUserRequest;
import com.filmr.user.dto.response.UserResponse;
import com.filmr.user.model.User;
import com.filmr.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  // ─── helpers ────────────────────────────────────────────────────────────────

  private User buildUser(Long id, String name, String email, String passHash) {
    User u = new User();
    u.setName(name);
    u.setEmail(email);
    u.setPassHash(passHash);
    // Reflectively set the generated id so the mock "saved" entity has an id.
    try {
      var field = User.class.getDeclaredField("id");
      field.setAccessible(true);
      field.set(u, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return u;
  }

  // ─── findAll ─────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("findAll()")
  class FindAll {

    @Test
    @DisplayName("returns empty list when no users exist")
    void returnsEmptyList_whenNoUsers() {
      when(userRepository.findAll()).thenReturn(List.of());

      List<Long> result = userService.findAll();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("returns list of ids for existing users")
    void returnsIds_whenUsersExist() {
      User u1 = buildUser(1L, "Alice", "alice@test.com", "hash1");
      User u2 = buildUser(2L, "Bob", "bob@test.com", "hash2");
      when(userRepository.findAll()).thenReturn(List.of(u1, u2));

      List<Long> result = userService.findAll();

      assertThat(result).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("returns single id for one user")
    void returnsSingleId_whenOneUserExists() {
      User u = buildUser(42L, "Carol", "carol@test.com", "hash3");
      when(userRepository.findAll()).thenReturn(List.of(u));

      List<Long> result = userService.findAll();

      assertThat(result).containsExactly(42L);
    }
  }

  // ─── createUser ──────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("createUser()")
  class CreateUser {

    @Test
    @DisplayName("creates and returns user when email is new")
    void createsUser_whenEmailIsNew() {
      CreateUserRequest req = new CreateUserRequest("Alice", "alice@test.com", "secret");
      User saved = buildUser(1L, "Alice", "alice@test.com", "secret");

      when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
      when(userRepository.save(any(User.class))).thenReturn(saved);

      UserResponse response = userService.createUser(req);

      assertThat(response.id()).isEqualTo(1L);
      assertThat(response.name()).isEqualTo("Alice");
      assertThat(response.email()).isEqualTo("alice@test.com");
    }

    @Test
    @DisplayName("persists correct field values on the saved entity")
    void persistsCorrectFields() {
      CreateUserRequest req = new CreateUserRequest("Dave", "dave@test.com", "mypassword");
      User saved = buildUser(10L, "Dave", "dave@test.com", "mypassword");

      when(userRepository.existsByEmail("dave@test.com")).thenReturn(false);
      when(userRepository.save(any(User.class))).thenReturn(saved);

      ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
      userService.createUser(req);
      verify(userRepository).save(captor.capture());

      User captured = captor.getValue();
      assertThat(captured.getName()).isEqualTo("Dave");
      assertThat(captured.getEmail()).isEqualTo("dave@test.com");
      assertThat(captured.getPassHash()).isEqualTo("mypassword");
    }

    @Test
    @DisplayName("throws RuntimeException when email already exists")
    void throwsException_whenEmailAlreadyExists() {
      CreateUserRequest req = new CreateUserRequest("Alice", "alice@test.com", "secret");
      when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

      assertThatThrownBy(() -> userService.createUser(req))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User with email already exists");

      verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("repository.save() is called exactly once on success")
    void callsSaveExactlyOnce_onSuccess() {
      CreateUserRequest req = new CreateUserRequest("Eve", "eve@test.com", "pass");
      User saved = buildUser(5L, "Eve", "eve@test.com", "pass");

      when(userRepository.existsByEmail("eve@test.com")).thenReturn(false);
      when(userRepository.save(any(User.class))).thenReturn(saved);

      userService.createUser(req);

      verify(userRepository, times(1)).save(any(User.class));
    }
  }

  // ─── findUserByEmail ──────────────────────────────────────────────────────────

  @Nested
  @DisplayName("findUserByEmail()")
  class FindUserByEmail {

    @Test
    @DisplayName("returns UserResponse for existing email")
    void returnsUser_whenEmailExists() {
      User u = buildUser(3L, "Frank", "frank@test.com", "hash");
      when(userRepository.findByEmail("frank@test.com")).thenReturn(Optional.of(u));

      UserResponse response = userService.findUserByEmail("frank@test.com");

      assertThat(response.id()).isEqualTo(3L);
      assertThat(response.name()).isEqualTo("Frank");
      assertThat(response.email()).isEqualTo("frank@test.com");
    }

    @Test
    @DisplayName("throws RuntimeException when email doesn't exist")
    void throwsException_whenEmailNotFound() {
      when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.findUserByEmail("ghost@test.com"))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User doesn't exist");
    }

    @Test
    @DisplayName("throws when email is an empty string (not in DB)")
    void throwsException_whenEmailIsEmpty() {
      when(userRepository.findByEmail("")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.findUserByEmail(""))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User doesn't exist");
    }
  }

  // ─── findUserById ─────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("findUserById()")
  class FindUserById {

    @Test
    @DisplayName("returns UserResponse for existing id")
    void returnsUser_whenIdExists() {
      User u = buildUser(7L, "Grace", "grace@test.com", "hash");
      when(userRepository.findById(7L)).thenReturn(Optional.of(u));

      UserResponse response = userService.findUserById(7L);

      assertThat(response.id()).isEqualTo(7L);
      assertThat(response.name()).isEqualTo("Grace");
      assertThat(response.email()).isEqualTo("grace@test.com");
    }

    @Test
    @DisplayName("throws RuntimeException for non-existent id")
    void throwsException_whenIdNotFound() {
      when(userRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.findUserById(999L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User doesn't exist");
    }

    @Test
    @DisplayName("throws when id is 0 (boundary — not in DB)")
    void throwsException_whenIdIsZero() {
      when(userRepository.findById(0L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.findUserById(0L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User doesn't exist");
    }

    @Test
    @DisplayName("throws when id is negative")
    void throwsException_whenIdIsNegative() {
      when(userRepository.findById(-1L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.findUserById(-1L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User doesn't exist");
    }
  }

  // ─── updateUser ───────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("updateUser()")
  class UpdateUser {

    private User existingUser;

    @BeforeEach
    void setUp() {
      existingUser = buildUser(1L, "OldName", "user@test.com", "oldHash");
    }

    @Test
    @DisplayName("updates name only when only name is provided")
    void updatesNameOnly_whenOnlyNameProvided() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
      UpdateUserRequest req = new UpdateUserRequest("NewName", null);

      UserResponse response = userService.updateUser(1L, req);

      assertThat(response.name()).isEqualTo("NewName");
      assertThat(existingUser.getPassHash()).isEqualTo("oldHash"); // password unchanged
    }

    @Test
    @DisplayName("updates password only when only password is provided")
    void updatesPasswordOnly_whenOnlyPasswordProvided() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
      UpdateUserRequest req = new UpdateUserRequest(null, "newHash");

      userService.updateUser(1L, req);

      assertThat(existingUser.getName()).isEqualTo("OldName"); // name unchanged
      assertThat(existingUser.getPassHash()).isEqualTo("newHash");
    }

    @Test
    @DisplayName("updates both name and password when both provided")
    void updatesBothFields_whenBothProvided() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
      UpdateUserRequest req = new UpdateUserRequest("NewName", "newHash");

      UserResponse response = userService.updateUser(1L, req);

      assertThat(response.name()).isEqualTo("NewName");
      assertThat(existingUser.getPassHash()).isEqualTo("newHash");
    }

    @Test
    @DisplayName("changes nothing when both fields are null")
    void changesNothing_whenBothFieldsNull() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
      UpdateUserRequest req = new UpdateUserRequest(null, null);

      UserResponse response = userService.updateUser(1L, req);

      assertThat(response.name()).isEqualTo("OldName");
      assertThat(existingUser.getPassHash()).isEqualTo("oldHash");
    }

    @Test
    @DisplayName("throws RuntimeException when user id doesn't exist")
    void throwsException_whenIdNotFound() {
      when(userRepository.findById(404L)).thenReturn(Optional.empty());
      UpdateUserRequest req = new UpdateUserRequest("Name", "pass");

      assertThatThrownBy(() -> userService.updateUser(404L, req))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User with the id doesn't exist");
    }

    @Test
    @DisplayName("returns response with unchanged email after update")
    void emailIsUnchangedAfterUpdate() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
      UpdateUserRequest req = new UpdateUserRequest("NewName", "newHash");

      UserResponse response = userService.updateUser(1L, req);

      assertThat(response.email()).isEqualTo("user@test.com");
    }
  }

  // ─── deleteUser ───────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("deleteUser()")
  class DeleteUser {

    @Test
    @DisplayName("calls repository.delete() with correct user when id exists")
    void deletesUser_whenIdExists() {
      User u = buildUser(1L, "Hank", "hank@test.com", "hash");
      when(userRepository.findById(1L)).thenReturn(Optional.of(u));

      userService.deleteUser(1L);

      verify(userRepository, times(1)).delete(u);
    }

    @Test
    @DisplayName("throws RuntimeException when user id doesn't exist")
    void throwsException_whenIdNotFound() {
      when(userRepository.findById(55L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.deleteUser(55L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User with the id doesn't exist");

      verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("does not call delete when user not found")
    void doesNotCallDelete_whenUserNotFound() {
      when(userRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.deleteUser(99L)).isInstanceOf(RuntimeException.class);

      verify(userRepository, never()).delete(any(User.class));
    }
  }
}
