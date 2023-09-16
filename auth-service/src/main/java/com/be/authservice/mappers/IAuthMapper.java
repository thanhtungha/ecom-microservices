package com.be.authservice.mappers;

import com.be.authservice.dto.RqRegisterArgs;
import com.be.authservice.dto.UserDTO;
import com.be.authservice.dto.UserInfoDTO;
import com.be.authservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IAuthMapper {

    @Mapping(source = "userName", target = "userName")
    User RegisterArgsToUserInfo(RqRegisterArgs registerArgs);

    @Mapping(source = "id", target = "id")
    UserInfoDTO UserToUserInfoDTO(User user);

    @Mapping(source = "id", target = "id")
    UserDTO UserToDTO(User user);
}
