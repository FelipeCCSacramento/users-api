package br.com.felipeccsacramento.model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.felipeccsacramento.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Table(name="USER")
@Entity
public class User {

	@Id
	@GeneratedValue
	@Getter @Setter private Long id;
	
	@Getter @Setter private String cpf;
	@Getter @Setter private String firstName;
	@Getter @Setter private String lastName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Getter @Setter private LocalDate birthDate;
	
	@Getter @Setter private String address;
	@Getter @Setter private String phone;
	
	public User() {}
	
	public User(UserBuilder userBuilder) {
		this.id = userBuilder.id;
		this.cpf = userBuilder.cpf;
		this.firstName = userBuilder.firstName;
		this.lastName = userBuilder.lastName;
		this.birthDate = userBuilder.birthDate;
		this.address = userBuilder.address;
		this.phone = userBuilder.phone;
	}
	
	public User(UserDTO userDto) {
		this.id = userDto.getId();
		this.cpf = userDto.getCpf();
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.birthDate = userDto.getBirthDate();
		this.address = userDto.getAddress();
		this.phone = userDto.getPhone();
	}
	
	// TODO: refatorar
	public void update(User user) {
		this.setCpf(user.getCpf());
		this.setFirstName(user.getFirstName());
		this.setLastName(user.getLastName());
		this.setBirthDate(user.getBirthDate());
		this.setAddress(user.getAddress());
		this.setPhone(user.getPhone());
	}
	
	public void patch(Map<String, Object> patchMap) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		for(Entry<String, Object> entry: patchMap.entrySet()) {
			Field field = User.class.getDeclaredField(entry.getKey());
			
			if(!field.isAnnotationPresent(Id.class)) {
				field.setAccessible(true);					
				field.set(this, entry.getValue());
			}

		}

	}
	
	public static class UserBuilder {

		private Long id;
		private String cpf;
		private String firstName;
		private String lastName;
		private LocalDate birthDate;
		private String address;
		private String phone;

		public UserBuilder(String cpf, String firstName, String lastName) {
			this.cpf = cpf;
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public UserBuilder id(Long id) {
			this.id = id;
			return this;
		}
		
		public UserBuilder birthDate(LocalDate birthDate) {
			this.birthDate = birthDate;
			return this;
		}

		public UserBuilder address(String address) {
			this.address = address;
			return this;
		}

		public UserBuilder phone(String phone) {
			this.phone = phone;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}
}
