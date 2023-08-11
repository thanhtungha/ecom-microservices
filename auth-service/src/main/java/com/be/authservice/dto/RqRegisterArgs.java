package com.be.authservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RqRegisterArgs {
    @NotNull
    private String userName;
    @NotNull
    private String userPassword;
    @NotNull
    private String phoneNumber;
}
