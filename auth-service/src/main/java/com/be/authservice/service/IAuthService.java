package com.be.authservice.service;

import com.be.authservice.dto.*;

import java.util.List;

public interface IAuthService {
    UserDTO register(RqRegisterArgs registerArgs);

    UserDTO login(RqLoginArgs loginArgs);

    boolean logout(String authorizationHeader);

    boolean changePassword(String authorizationHeader,
                           RqChangePasswordArgs changePasswordArgs);

    UserDTO update(String authorizationHeader, RqUpdateArgs updateArgs);

    UserDTO getUserInformation(String authorizationHeader);

    ListUsers getListUser(String authorizationHeader, List<String> ids);

    boolean deleteUser(String authorizationHeader);
}
