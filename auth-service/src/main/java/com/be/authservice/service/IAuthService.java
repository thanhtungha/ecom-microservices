package com.be.authservice.service;

import com.be.authservice.dto.*;

import java.util.List;

public interface IAuthService {
    UserInfoDTO register(RqRegisterArgs registerArgs);

    UserInfoDTO login(RqLoginArgs loginArgs);

    boolean logout(String authorizationHeader);

    boolean changePassword(String authorizationHeader,
                           RqChangePasswordArgs changePasswordArgs);

    UserDTO forgotPassword(RqForgotPasswordArgs forgotPasswordArgs);

    UserInfoDTO update(String authorizationHeader, RqUpdateArgs updateArgs);

    UserInfoDTO getUserInformation(String authorizationHeader);

    ListUsers getListUser(String authorizationHeader, List<String> ids);

    boolean deleteUser(String authorizationHeader);
}
