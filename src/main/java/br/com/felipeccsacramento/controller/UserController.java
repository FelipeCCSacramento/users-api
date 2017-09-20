package br.com.felipeccsacramento.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.felipeccsacramento.dto.UserDTO;
import br.com.felipeccsacramento.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<?> listUsers() {
		return ResponseEntity.ok(userService.listUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		UserDTO userDto = userService.getUser(id);
		return Optional.ofNullable(userDto).map(u -> ResponseEntity.ok(u))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/cpf/{cpf}")
	public ResponseEntity<?> getUserByCpf(@PathVariable String cpf) {
		UserDTO userDto = userService.getUserByCpf(cpf);
		return Optional.ofNullable(userDto).map(u -> ResponseEntity.ok(u))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody UserDTO userDto) {
		List<String> errors = userDto.validate();
		if(errors.size() > 0) {
			return ResponseEntity.badRequest().body(errors);
		}
		
		UserDTO newUserDto = userService.addUser(userDto);
		return Optional.ofNullable(newUserDto).map(u -> {
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(u.getId()).toUri();
			return ResponseEntity.created(location).build();
		}).orElseGet(() -> ResponseEntity.unprocessableEntity().build());
	}

	@PostMapping("/bulk")
	public ResponseEntity<?> addBulkUsers(@RequestBody List<UserDTO> usersDto) {
		List<List<String>> errors = new ArrayList<>();
		 usersDto.stream().forEach((userDto) -> {
			 List<String> error = userDto.validate();
			 
			 if(error.size() > 0) errors.add(error);
		 });
		if(errors.size() > 0) return ResponseEntity.badRequest().body(errors);

		List<UserDTO> newUsersDto = userService.addBulkUsers(usersDto);
		return Optional.ofNullable(newUsersDto).map(u -> ResponseEntity.ok().build())
				.orElseGet(() -> ResponseEntity.unprocessableEntity().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		UserDTO userDto = userService.deleteUser(id);
		return Optional.ofNullable(userDto).map(u -> ResponseEntity.ok().build())
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> patchMap) {
		UserDTO userDto = userService.patchUser(id, patchMap);
		return Optional.ofNullable(userDto).map(u -> ResponseEntity.ok(u))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDto) {
		List<String> errors = userDto.validate();
		if(errors.size() > 0) {
			return ResponseEntity.badRequest().body(errors);
		}
		
		UserDTO userUpdatedDto = userService.updateUser(id, userDto);
		return Optional.ofNullable(userUpdatedDto).map(u -> ResponseEntity.ok(u))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
