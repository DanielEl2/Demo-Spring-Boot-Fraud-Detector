package com.project.bankwebapp.DTO;

import com.project.bankwebapp.Entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {


    private Long transaction_id;
    private String location;
    private BigDecimal amount;
    private String currency;
    private Instant timestamp;


    private UUID user;

    private String status;
}
