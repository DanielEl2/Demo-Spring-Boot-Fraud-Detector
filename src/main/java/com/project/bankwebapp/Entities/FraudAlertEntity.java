package com.project.bankwebapp.Entities;

import com.project.bankwebapp.model.AlertStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.engine.internal.Cascade;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "FraudAlerts")
public class FraudAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;
    private Instant timeStamp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", unique = true)
    private TransactionEntity transaction;
}
