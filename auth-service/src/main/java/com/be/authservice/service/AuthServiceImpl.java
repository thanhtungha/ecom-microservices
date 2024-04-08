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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    private final IAuthRepository repository;
    private final AuthenticationProvider authenticationProvider;
    private final IAuthMapper mapper;

    @Override
    public UserInfoDTO register(RqRegisterArgs registerArgs) {
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
        return mapper.UserToUserInfoDTO(user);
    }

    @Override
    public UserInfoDTO login(RqLoginArgs loginArgs) {
        Optional<User> storedModel =
                repository.findByUserNameAndUserPassword(loginArgs.getUserName(),
                        loginArgs.getUserPassword());
        if (storedModel.isPresent()) {
            User user = storedModel.get();
            user.setAccessToken(authenticationProvider.createAccessToken(user.getUserName()));
            user.setUpdateDate(new Date());
            repository.save(user);
            return mapper.UserToUserInfoDTO(user);
        } else {
            throw new RestExceptions.NotFound("User not found or wrong " +
                    "password");
        }
    }

    @Override
    public boolean logout(String authorizationHeader) {
        User user = verifyUser(authorizationHeader);
        user.setAccessToken("");
        user.setUpdateDate(new Date());
        repository.save(user);
        return true;
    }

    @Override
    public boolean changePassword(String authorizationHeader,
                                  RqChangePasswordArgs changePasswordArgs) {
        User user = verifyUser(authorizationHeader);
        if (user.getUserPassword()
                .equals(changePasswordArgs.getNewPassword())) {
            throw new RestExceptions.BadRequest("New password cannot be " +
                    "the same as the old password.");
        }
        user.setUserPassword(changePasswordArgs.getNewPassword());
        user.setUpdateDate(new Date());
        repository.save(user);
        return true;
    }

    @Override
    public UserDTO forgotPassword(RqForgotPasswordArgs forgotPasswordArgs) {
        Optional<User> storedModel = repository.findByUserName(forgotPasswordArgs.getUserName());
        if (storedModel.isPresent()) {
            User user = storedModel.get();
            return mapper.UserToDTO(user);
        } else {
            throw new RestExceptions.Forbidden("User not found!");
        }
    }

    @Override
    public UserInfoDTO update(String authorizationHeader, RqUpdateArgs updateArgs) {
        User user = verifyUser(authorizationHeader);
        user.setAddress(updateArgs.getAddress());
        user.setPhoneNumber(updateArgs.getPhoneNumber());
        user.setUpdateDate(new Date());
        repository.save(user);
        return mapper.UserToUserInfoDTO(user);
    }

    @Override
    public UserInfoDTO getUserInformation(String authorizationHeader) {
        Optional<User> storedModel = repository.findByAccessToken(
                extractAccessToken(authorizationHeader));
        if (storedModel.isPresent()) {
            return mapper.UserToUserInfoDTO(storedModel.get());
        } else {
            throw new RestExceptions.Forbidden("Invalid accessToken!");
        }
    }

    @Override
    public ListUsers getListUser(String authorizationHeader, List<String> ids) {
        verifyUser(authorizationHeader);
        List<UUID> uuidList = ids.stream()
                .map(UUID::fromString)
                .toList();
        List<User> storedModel = repository.findAllById(uuidList);
        List<UserInfoDTO> userInfoDTOList = storedModel.stream()
                .map(mapper::UserToUserInfoDTO)
                .toList();
        return new ListUsers(userInfoDTOList);
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
        User user = verifyUser(authorizationHeader);
        repository.delete(user);
        return true;
    }

    private User verifyUser(String authorizationHeader) {
        Optional<User> storedModel = repository.findByAccessToken(
                extractAccessToken(authorizationHeader));
        if (storedModel.isPresent()) {
            return storedModel.get();
        } else {
            throw new RestExceptions.Forbidden("Invalid accessToken!");
        }
    }
}
