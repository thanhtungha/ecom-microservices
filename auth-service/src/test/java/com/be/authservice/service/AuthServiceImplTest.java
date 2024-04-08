package com.be.authservice.service;

import com.be.authservice.dto.*;
import com.be.authservice.exception.BaseException;
import com.be.authservice.exception.RestExceptions;
import com.be.authservice.mappers.IAuthMapper;
import com.be.authservice.model.User;
import com.be.authservice.repository.IAuthRepository;
import com.be.authservice.security.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @InjectMocks
    public AuthServiceImpl service;
    @Mock
    public IAuthRepository repository;

    @Mock
    public AuthenticationProvider authenticationProvider;

    @Spy
    private IAuthMapper mapper = Mappers.getMapper(IAuthMapper.class);

    private User user;
    private String accessToken;

    @BeforeEach
    public void beforeEach() {
        accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJpc3MiOiJ4eXoxMjQzMiIsImV4cCI6MTcxMjUyMzE1OCwiaWF0IjoxNzEyNDM2NzU4fQ.ZyiwRJZQGtFQtmrsiwX7f1dfc_0hMiN0PEn9aHSql8g";

        user = User.builder()
                .id(UUID.randomUUID())
                .createDate(new Date())
                .updateDate(new Date())
                .userName("userName")
                .userPassword("userPassword")
                .phoneNumber("0123456789")
                .address("010302 world street")
                .accessToken(accessToken)
                .build();
    }

    @Test
    void register_success() {
        RqRegisterArgs registerArgs = RqRegisterArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .phoneNumber("0123456789")
                .build();

        when(repository.save(any())).thenReturn(user);
        when(authenticationProvider.createAccessToken(anyString())).thenReturn(
                accessToken);

        UserInfoDTO expected = mapper.UserToUserInfoDTO(user);
        UserInfoDTO actual = service.register(registerArgs);

        assertNotNull(actual);
        assertEquals(expected.getUserName(), actual.getUserName());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    @Test
    void register_fail_userExisted() {
        RqRegisterArgs registerArgs = RqRegisterArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .phoneNumber("0123456789")
                .build();

        when(repository.findByUserName(anyString())).thenReturn(
                Optional.of(user));

        BaseException baseException = assertThrows(
                RestExceptions.Conflict.class,
                () -> service.register(registerArgs));
        assertEquals(baseException.getMessage(), "User existed");
    }

    @Test
    void login_success() {
        RqLoginArgs loginArgs = RqLoginArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .build();

        when(repository.findByUserNameAndUserPassword(loginArgs.getUserName(),
                loginArgs.getUserPassword())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(user);
        when(authenticationProvider.createAccessToken(anyString())).thenReturn(
                accessToken);

        UserInfoDTO expected = mapper.UserToUserInfoDTO(user);
        UserInfoDTO actual = service.login(loginArgs);

        assertNotNull(actual);
        assertEquals(expected.getUserName(), actual.getUserName());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(expected.getAccessToken(), actual.getAccessToken());
    }

    @Test
    void login_fail_wrongUserName() {
        RqLoginArgs loginArgs = RqLoginArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .build();

        when(repository.findByUserNameAndUserPassword(anyString(),
                anyString())).thenReturn(Optional.empty());

        BaseException baseException = assertThrows(
                RestExceptions.NotFound.class, () -> service.login(loginArgs));
        assertEquals(baseException.getMessage(),
                "User not found or wrong password");
    }

    @Test
    void logout_success() {
        when(repository.findByAccessToken(accessToken)).thenReturn(
                Optional.of(user));
        boolean actual = service.logout("Bearer " + accessToken);
        assertTrue(actual);
    }

    @Test
    void changePassword_success() {
        RqChangePasswordArgs changePasswordArgs = RqChangePasswordArgs.builder()
                .newPassword("newPassword")
                .build();

        when(repository.findByAccessToken(accessToken)).thenReturn(
                Optional.of(user));

        boolean actual = service.changePassword("Bearer " + accessToken,
                changePasswordArgs);

        assertTrue(actual);
    }

    @Test
    void changePassword_fail_samePassword() {
        RqChangePasswordArgs changePasswordArgs = RqChangePasswordArgs.builder()
                .newPassword("userPassword")
                .build();

        when(repository.findByAccessToken(accessToken)).thenReturn(
                Optional.of(user));

        BaseException baseException = assertThrows(
                RestExceptions.BadRequest.class,
                () -> service.changePassword("Bearer " + accessToken,
                        changePasswordArgs));
        assertEquals(baseException.getMessage(),
                "New password cannot be the same as the old password.");
    }

    @Test
    void forgotPassword() {
        //fail("Not implemented!");
    }

    @Test
    void update() {
        RqUpdateArgs updateArgs = RqUpdateArgs.builder()
                .phoneNumber("0123456789")
                .address("010302 Sweet Home Str")
                .build();

        when(repository.findByAccessToken(accessToken)).thenReturn(
                Optional.of(user));

        UserInfoDTO actual = service.update("Bearer " + accessToken,
                updateArgs);

        assertEquals("0123456789", actual.getPhoneNumber());
        assertEquals("010302 Sweet Home Str", actual.getAddress());
    }

    @Test
    void getUserInformation_success() {
        when(repository.findByAccessToken(accessToken)).thenReturn(
                Optional.of(user));

        UserInfoDTO expected = mapper.UserToUserInfoDTO(user);
        UserInfoDTO actual = service.getUserInformation(
                "Bearer " + accessToken);

        assertEquals(expected.getUserName(), actual.getUserName());
        assertEquals(expected.getAccessToken(), actual.getAccessToken());
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    void getUserInformation_fail_invalidAccessToken() {
        BaseException baseException = assertThrows(
                RestExceptions.Forbidden.class,
                () -> service.getUserInformation("Bearer " + accessToken));
        assertEquals(baseException.getMessage(), "Invalid accessToken!");
    }

    @Test
    void delete() {
        when(repository.findByAccessToken(accessToken)).thenReturn(
                Optional.of(user));

        boolean actual = service.deleteUser("Bearer " + accessToken);

        assertTrue(actual);
    }
}