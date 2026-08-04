package com.filmr.user.service;

import com.filmr.user.dto.request.CreateUserRequest;
import com.filmr.user.dto.request.UpdateUserRequest;
import com.filmr.user.dto.response.UserResponse;
import com.filmr.user.model.User;
import com.filmr.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<Long> findAll() {
    return userRepository.findAll().stream().map(User::getId).toList();
  }

  public UserResponse createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new RuntimeException("User with email already exists");
    }

    User user = new User();
    user.setEmail(request.email());
    user.setName(request.name());
    // todo: encoding password
    user.setPassHash(request.password());
    User saved = userRepository.save(user);

    return UserResponse.from(saved);
  }

  public UserResponse findUserByEmail(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User doesn't exist"));

    return UserResponse.from(user);
  }

  public UserResponse findUserById(Long id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new RuntimeException("User doesn't exist"));

    return UserResponse.from(user);
  }

  public UserResponse updateUser(Long id, UpdateUserRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User with the id doesn't exist"));

    if (request.name() != null) user.setName(request.name());
    if (request.password() != null) user.setPassHash(request.password());

    return UserResponse.from(user);
  }

  public void deleteUser(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User with the id doesn't exist"));

    userRepository.delete(user);
  }
}
