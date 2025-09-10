package com.c04.productmodule.repositories;

import com.c04.productmodule.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAuthRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

