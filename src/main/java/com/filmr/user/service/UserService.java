package com.filmr.user.service;

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

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User createUser(User user) {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new RuntimeException("User with email already exists");
    }

    return userRepository.save(user);
  }

  public User findUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User doesn't exist"));
  }

  public User findUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("User doesn't exist"));
  }

  public User updateUser(Long id, User updatedUser) {
    User user = findUserById(id);

    if (updatedUser.getName() != null) user.setName(updatedUser.getName());
    if (updatedUser.getPassHash() != null) user.setPassHash(updatedUser.getPassHash());

    return userRepository.save(user);
  }

  public void deleteUser(Long id) {
    User user = findUserById(id);

    userRepository.delete(user);
  }
}
