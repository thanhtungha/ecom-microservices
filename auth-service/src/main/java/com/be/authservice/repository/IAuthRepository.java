package com.be.authservice.repository;

import com.be.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IAuthRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserNameAndUserPassword(String userName, String userPassword);
    Optional<User> findByAccessToken(String accessToken);
}

