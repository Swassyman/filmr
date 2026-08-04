package com.filmr.user.controller;

import com.filmr.user.dto.request.CreateUserRequest;
import com.filmr.user.dto.request.UpdateUserRequest;
import com.filmr.user.dto.response.UserResponse;
import com.filmr.user.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
  }

  @GetMapping
  public ResponseEntity<List<Long>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findUserById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable Long id, @RequestBody UpdateUserRequest request) {
    return ResponseEntity.accepted().body(userService.updateUser(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
