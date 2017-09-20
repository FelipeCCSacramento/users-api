package br.com.felipeccsacramento.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.felipeccsacramento.model.User;
import lombok.Getter;
import lombok.Setter;

public class UserDTO {

	@Getter @Setter private Long id;
	@Getter @Setter private String cpf;
	@Getter @Setter private String firstName;
	@Getter @Setter private String lastName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Getter @Setter private LocalDate birthDate;
	@Getter @Setter private String address;
	@Getter @Setter private String phone;
	
	public UserDTO(){}
	
	public UserDTO(User user) {
		this.id = user.getId();
		this.cpf = user.getCpf();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.birthDate = user.getBirthDate();
		this.address = user.getAddress();
		this.phone = user.getPhone();
	}
	
	
	// Omitted usage of getters/setters
	// TODO: Use Spring Validation interface
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		
		// CPF
		if(this.cpf == null) {
			errors.add("CPF não informado");
		} else {
			cpf = cpf.replaceAll("\\D", "");
			if(cpf.length() != 11) errors.add("CPF inválido");
		}
		
		// FirstName
		if(this.firstName == null) {
			errors.add("Nome não informado");
		} else {
			if(firstName.length() < 3) errors.add("Nome deve conter no mínimo 3 caracteres");
		}
		
		// LastName
		if(this.lastName == null) {
			errors.add("Sobrenome não informado");
		} else {
			if(lastName.length() < 3) errors.add("Sobrenome deve conter no mínimo 3 caracteres");
		}
		
		// BirthDate
		if(this.birthDate != null && birthDate.isAfter(LocalDate.now())) errors.add("Data de nascimento inválida");
		
		// Address
		if(this.address != null && address.length() < 3) errors.add("Endereço deve conter no mínimo 3 caracteres");
		
		// Phone 
		if(this.phone != null) {
			phone = phone.replaceAll("\\D", "");
			if(phone.length() < 10 || phone.length() > 11) errors.add("Telefone deve conter 10 ou 11 dígitos (DDD + número)");
		}
		
		return errors;
	}
}
