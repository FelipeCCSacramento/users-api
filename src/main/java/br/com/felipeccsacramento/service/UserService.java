package br.com.felipeccsacramento.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.felipeccsacramento.dto.UserDTO;
import br.com.felipeccsacramento.model.User;
import br.com.felipeccsacramento.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public List<UserDTO> listUsers() {
		List<UserDTO> usersDtoList = userRepository.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
		return usersDtoList;
	}

	public UserDTO getUser(Long id) {
		User user = userRepository.findOne(id);
		
		if(user == null) return null;
		
		return new UserDTO(user);
	}
	
	public UserDTO getUserByCpf(String cpf) {
		User user = userRepository.findByCpf(cpf);
		
		if(user == null) return null;
		
		return new UserDTO(user);
	}

	public UserDTO addUser(UserDTO userDto) {
		User user = userRepository.save(new User(userDto));
		
		if(user == null) return null;
		
		return new UserDTO(user);
	}
	
	public List<UserDTO> addBulkUsers(List<UserDTO> usersDto) {
		List<User> users = usersDto.stream().map(User::new).collect(Collectors.toList());
		users = userRepository.save(users);
		return users.stream().map(UserDTO::new).collect(Collectors.toList());
	}
	
	public UserDTO deleteUser(Long id) {
		User user = userRepository.findOne(id);
		
		if(user == null) return null;
		
		userRepository.delete(id);
		return new UserDTO(user);
	}
	
	public UserDTO updateUser(Long id, UserDTO newUserDto) {
		User user = userRepository.findOne(id);
		
		if (user == null) return null;
		
		user.update(new User(newUserDto));
		
		return new UserDTO(userRepository.save(user));
	}

	public UserDTO patchUser(Long id, Map<String, Object> patchMap) {
		User user = userRepository.findOne(id);
		
		if (user == null) return null;
				
		try {
			user.patch(patchMap);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			log.error(e.getMessage());
		}
		
		return new UserDTO(userRepository.save(user));
	}
	
}
