package com.project.bankwebapp.DTO;


import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.model.AlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FraudAlertDto {


    private Long id;
    private Long transaction_id;
    private String description;
    @Enumerated(EnumType.STRING)
    private AlertStatus status;
    private Instant timeStamp;

}
