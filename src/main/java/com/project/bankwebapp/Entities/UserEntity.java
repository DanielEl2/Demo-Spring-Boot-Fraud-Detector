package com.project.bankwebapp.Entities;

import com.project.bankwebapp.model.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "Users")
public class UserEntity {


    @Id
    //This generates a random 128-bit string, like 550e8400-e29b-41d4-a716-446655440000. MORE SECURE
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID user_id;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password; // need to hash This
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TransactionEntity> transactions;

}
