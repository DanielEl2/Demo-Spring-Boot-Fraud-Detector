package com.project.bankwebapp.DTO;

import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Data Transfer Object
public class UserDto {


    private UUID user_id;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Role role;

    private List<TransactionDto> transactions;
}
