package com.filmr.user.service;

import com.filmr.user.model.User;
import com.filmr.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
		return userRepository.save(user);
	}
}
