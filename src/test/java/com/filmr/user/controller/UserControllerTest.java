package com.filmr.user.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.filmr.user.dto.request.CreateUserRequest;
import com.filmr.user.dto.request.UpdateUserRequest;
import com.filmr.user.dto.response.UserResponse;
import com.filmr.user.service.UserService;
import jakarta.servlet.ServletException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@DisplayName("UserController")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserService userService;

  @MockitoBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  // ─── POST /user ───────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("POST /user")
  class CreateUser {

    @Test
    @DisplayName("returns 201 Created with body when request is valid")
    void returns201_onSuccess() throws Exception {
      CreateUserRequest req = new CreateUserRequest("Alice", "alice@test.com", "secret");
      UserResponse resp = new UserResponse(1L, "Alice", "alice@test.com");
      when(userService.createUser(any(CreateUserRequest.class))).thenReturn(resp);

      mockMvc
          .perform(
              post("/user")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.name").value("Alice"))
          .andExpect(jsonPath("$.email").value("alice@test.com"));
    }

    @Test
    @DisplayName("service exception propagates when email already exists")
    void serviceExceptionPropagates_whenEmailAlreadyExists() throws Exception {
      CreateUserRequest req = new CreateUserRequest("Alice", "alice@test.com", "secret");
      when(userService.createUser(any(CreateUserRequest.class)))
          .thenThrow(new RuntimeException("User with email already exists"));

      assertThatThrownBy(
              () ->
                  mockMvc.perform(
                      post("/user")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(req))))
          .isInstanceOf(ServletException.class)
          .hasCauseInstanceOf(RuntimeException.class)
          .hasMessageContaining("User with email already exists");
    }

    @Test
    @DisplayName("calls service.createUser() exactly once with the correct payload")
    void delegatesToService() throws Exception {
      CreateUserRequest req = new CreateUserRequest("Bob", "bob@test.com", "pass");
      UserResponse resp = new UserResponse(2L, "Bob", "bob@test.com");
      when(userService.createUser(any(CreateUserRequest.class))).thenReturn(resp);

      mockMvc
          .perform(
              post("/user")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isCreated());

      verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }
  }

  // ─── GET /user ─────────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("GET /user")
  class FindAll {

    @Test
    @DisplayName("returns 200 OK with empty array when no users")
    void returns200_withEmptyArray() throws Exception {
      when(userService.findAll()).thenReturn(List.of());

      mockMvc
          .perform(get("/user"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("returns 200 OK with list of ids")
    void returns200_withIds() throws Exception {
      when(userService.findAll()).thenReturn(List.of(1L, 2L, 3L));

      mockMvc
          .perform(get("/user"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0]").value(1))
          .andExpect(jsonPath("$[1]").value(2))
          .andExpect(jsonPath("$[2]").value(3));
    }

    @Test
    @DisplayName("returns 200 with single-element array")
    void returns200_withSingleId() throws Exception {
      when(userService.findAll()).thenReturn(List.of(42L));

      mockMvc
          .perform(get("/user"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0]").value(42));
    }
  }

  // ─── GET /user/{id} ────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("GET /user/{id}")
  class FindUserById {

    @Test
    @DisplayName("returns 200 OK with user body when id exists")
    void returns200_whenUserExists() throws Exception {
      UserResponse resp = new UserResponse(5L, "Carol", "carol@test.com");
      when(userService.findUserById(5L)).thenReturn(resp);

      mockMvc
          .perform(get("/user/5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(5))
          .andExpect(jsonPath("$.name").value("Carol"))
          .andExpect(jsonPath("$.email").value("carol@test.com"));
    }

    @Test
    @DisplayName("service exception propagates for unknown id")
    void serviceExceptionPropagates_whenUserNotFound() throws Exception {
      when(userService.findUserById(999L)).thenThrow(new RuntimeException("User doesn't exist"));

      assertThatThrownBy(() -> mockMvc.perform(get("/user/999")))
          .isInstanceOf(ServletException.class)
          .hasCauseInstanceOf(RuntimeException.class)
          .hasMessageContaining("User doesn't exist");
    }

    @Test
    @DisplayName("delegates to service with the correct id")
    void delegatesToService_withCorrectId() throws Exception {
      UserResponse resp = new UserResponse(7L, "Dave", "dave@test.com");
      when(userService.findUserById(7L)).thenReturn(resp);

      mockMvc.perform(get("/user/7")).andExpect(status().isOk());

      verify(userService, times(1)).findUserById(7L);
    }
  }

  // ─── PATCH /user/{id} ──────────────────────────────────────────────────────────

  @Nested
  @DisplayName("PATCH /user/{id}")
  class UpdateUser {

    @Test
    @DisplayName("returns 202 Accepted with updated body")
    void returns202_onSuccess() throws Exception {
      UpdateUserRequest req = new UpdateUserRequest("NewName", null);
      UserResponse resp = new UserResponse(1L, "NewName", "alice@test.com");
      when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(resp);

      mockMvc
          .perform(
              patch("/user/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isAccepted())
          .andExpect(jsonPath("$.name").value("NewName"))
          .andExpect(jsonPath("$.email").value("alice@test.com"));
    }

    @Test
    @DisplayName("service exception propagates for unknown id")
    void serviceExceptionPropagates_whenUserNotFound() throws Exception {
      UpdateUserRequest req = new UpdateUserRequest("Name", "pass");
      when(userService.updateUser(eq(404L), any(UpdateUserRequest.class)))
          .thenThrow(new RuntimeException("User with the id doesn't exist"));

      assertThatThrownBy(
              () ->
                  mockMvc.perform(
                      patch("/user/404")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(req))))
          .isInstanceOf(ServletException.class)
          .hasCauseInstanceOf(RuntimeException.class)
          .hasMessageContaining("User with the id doesn't exist");
    }

    @Test
    @DisplayName("handles patch with both fields null (no-op update)")
    void handles_nullFieldPatch() throws Exception {
      UpdateUserRequest req = new UpdateUserRequest(null, null);
      UserResponse resp = new UserResponse(1L, "OldName", "old@test.com");
      when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(resp);

      mockMvc
          .perform(
              patch("/user/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isAccepted())
          .andExpect(jsonPath("$.name").value("OldName"));
    }

    @Test
    @DisplayName("calls service.updateUser() with the correct id and body")
    void delegatesToService_withCorrectArgs() throws Exception {
      UpdateUserRequest req = new UpdateUserRequest("Updated", "newPass");
      UserResponse resp = new UserResponse(3L, "Updated", "u@test.com");
      when(userService.updateUser(eq(3L), any(UpdateUserRequest.class))).thenReturn(resp);

      mockMvc
          .perform(
              patch("/user/3")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isAccepted());

      verify(userService, times(1)).updateUser(eq(3L), any(UpdateUserRequest.class));
    }
  }

  // ─── DELETE /user/{id} ─────────────────────────────────────────────────────────

  @Nested
  @DisplayName("DELETE /user/{id}")
  class DeleteUser {

    @Test
    @DisplayName("returns 204 No Content on successful deletion")
    void returns204_onSuccess() throws Exception {
      doNothing().when(userService).deleteUser(1L);

      mockMvc.perform(delete("/user/1")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("service exception propagates for unknown id")
    void serviceExceptionPropagates_whenUserNotFound() throws Exception {
      doThrow(new RuntimeException("User with the id doesn't exist"))
          .when(userService)
          .deleteUser(999L);

      assertThatThrownBy(() -> mockMvc.perform(delete("/user/999")))
          .isInstanceOf(ServletException.class)
          .hasCauseInstanceOf(RuntimeException.class)
          .hasMessageContaining("User with the id doesn't exist");
    }

    @Test
    @DisplayName("response body is empty on successful deletion")
    void responseBodyIsEmpty_onSuccess() throws Exception {
      doNothing().when(userService).deleteUser(2L);

      mockMvc
          .perform(delete("/user/2"))
          .andExpect(status().isNoContent())
          .andExpect(content().string(""));
    }

    @Test
    @DisplayName("calls service.deleteUser() exactly once with the correct id")
    void delegatesToService_withCorrectId() throws Exception {
      doNothing().when(userService).deleteUser(8L);

      mockMvc.perform(delete("/user/8")).andExpect(status().isNoContent());

      verify(userService, times(1)).deleteUser(8L);
    }
  }
}
