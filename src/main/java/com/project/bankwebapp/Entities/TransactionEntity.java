package com.project.bankwebapp.Entities;


import jakarta.persistence.*;
import lombok.Data;
import org.h2.engine.User;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "Transactions")

public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transaction_id;
    private String location;
    private BigDecimal amount;
    private String currency;
    private Instant timestamp;
    private String status;
    @ManyToOne()
    @JoinColumn(name="user_id" , nullable = false)
    private UserEntity user;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    private FraudAlertEntity fraudAlert;

}
