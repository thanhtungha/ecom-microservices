package com.be.authservice.service;

import com.be.authservice.dto.*;

public interface IAuthService {
    UserDTO register(RqRegisterArgs registerArgs);

    UserDTO login(RqLoginArgs loginArgs);

    boolean logout(String authorizationHeader);

    boolean changePassword(String authorizationHeader,
                           RqChangePasswordArgs changePasswordArgs);

    UserDTO update(String authorizationHeader, RqUpdateArgs updateArgs);

    UserDTO getUserInformation(String authorizationHeader);

    boolean deleteUser(String authorizationHeader);
}
