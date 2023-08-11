package com.be.authservice.service;

import com.be.authservice.dto.*;
import com.be.authservice.exception.RestExceptions;
import com.be.authservice.mappers.IAuthMapper;
import com.be.authservice.model.User;
import com.be.authservice.repository.IAuthRepository;
import com.be.authservice.security.AuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    private final IAuthRepository repository;
    private final AuthenticationProvider authenticationProvider;
    private final IAuthMapper mapper;

    @Override
    public UserDTO register(RqRegisterArgs registerArgs) {
        Optional<User> storedModel =
                repository.findByUserName(registerArgs.getUserName());
        if (storedModel.isPresent()) {
            throw new RestExceptions.Conflict("User existed");
        }

        User user = mapper.RegisterArgsToUserInfo(registerArgs);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setAccessToken(authenticationProvider.createAccessToken(user.getUserName()));
        repository.save(user);
        return mapper.UserToDTO(user);
    }

    @Override
    public UserDTO login(RqLoginArgs loginArgs) {
        Optional<User> storedModel =
                repository.findByUserName(loginArgs.getUserName());
        if (storedModel.isPresent() && storedModel.get().getUserPassword().equals(loginArgs.getUserPassword())) {
            User user = storedModel.get();
            user.setAccessToken(authenticationProvider.createAccessToken(user.getUserName()));
            user.setUpdateDate(new Date());
            repository.save(user);
            return mapper.UserToDTO(user);
        } else {
            throw new RestExceptions.NotFound("User not found or wrong " +
                    "password");
        }
    }

    @Override
    public boolean logout(String authorizationHeader) {
        User user = getUser(authorizationHeader);
        user.setAccessToken("");
        user.setUpdateDate(new Date());
        repository.save(user);
        return true;
    }

    @Override
    public boolean changePassword(String authorizationHeader,
                                  RqChangePasswordArgs changePasswordArgs) {
        User user = getUser(authorizationHeader);
        if (user.getUserPassword().equals(changePasswordArgs.getNewPassword())) {
            throw new RestExceptions.BadRequest("New password cannot be " +
                    "the same as the old password.");
        }
        user.setUserPassword(changePasswordArgs.getNewPassword());
        user.setUpdateDate(new Date());
        repository.save(user);
        return true;
    }

    @Override
    public UserDTO update(String authorizationHeader,
                          RqUpdateArgs updateArgs) {
        User user = getUser(authorizationHeader);
        user.setAddress(updateArgs.getAddress());
        user.setPhoneNumber(updateArgs.getPhoneNumber());
        user.setUpdateDate(new Date());
        repository.save(user);
        return mapper.UserToDTO(user);
    }

    @Override
    public UserDTO getUserInformation(String authorizationHeader) {
        Optional<User> storedModel =
                repository.findByAccessToken(extractAccessToken(authorizationHeader));
        if (storedModel.isPresent()) {
            return mapper.UserToDTO(storedModel.get());
        } else {
            throw new RestExceptions.Forbidden("Invalid accessToken!");
        }
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(
                "Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new RestExceptions.Forbidden("Invalid accessToken!");
    }

    @Override
    public boolean deleteUser(String authorizationHeader) {
        User user = getUser(authorizationHeader);
        repository.delete(user);
        return true;
    }

    private User getUser(String authorizationHeader) {
        Optional<User> storedModel =
                repository.findByAccessToken(extractAccessToken(authorizationHeader));
        if (storedModel.isPresent()) {
            return storedModel.get();
        } else {
            throw new RestExceptions.Forbidden("Invalid accessToken!");
        }
    }
}
