package br.com.felipeccsacramento.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.felipeccsacramento.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByCpf(String cpf);

}
