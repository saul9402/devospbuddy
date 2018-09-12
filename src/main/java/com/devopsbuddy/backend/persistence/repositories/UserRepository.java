package com.devopsbuddy.backend.persistence.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.devopsbuddy.backend.persistence.domain.backend.User;

//Con esta anotacion spring puede traducir las excepciones a excepciones de acceso de datos, :')
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	public User findByUsername(String username);
}
