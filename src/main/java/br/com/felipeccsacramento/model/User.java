package br.com.felipeccsacramento.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Table(name="USER")
@Entity
public class User {

	@Id
	@Getter @Setter private Long id;
	@Getter @Setter private String cpf;
	@Getter @Setter private String firstName;
	@Getter @Setter private String lastName;
	@Getter @Setter private LocalDate birthDate;
	@Getter @Setter private String address;
	@Getter @Setter private String phone;
}
