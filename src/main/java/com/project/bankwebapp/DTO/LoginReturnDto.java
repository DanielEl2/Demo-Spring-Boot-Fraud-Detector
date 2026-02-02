package com.project.bankwebapp.DTO;

import com.project.bankwebapp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginReturnDto {

    private String token;
    private String username;
    private Role role;
    private UUID userId;
}
