package br.com.felipeccsacramento.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.felipeccsacramento.model.User;
import br.com.felipeccsacramento.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepository;

	@Autowired
	UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public List<User> listUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable Long userId) {
		return userRepository.findOne(userId);
	}

	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody User user) {
		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}
}
