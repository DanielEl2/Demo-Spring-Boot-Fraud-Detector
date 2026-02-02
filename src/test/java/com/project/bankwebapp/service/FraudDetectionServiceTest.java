package com.project.bankwebapp.service;

import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Repositories.FraudAlertRepository;
import com.project.bankwebapp.Repositories.TransactionRepository;
import com.project.bankwebapp.Services.FraudAlertService;
import com.project.bankwebapp.Services.FraudDetectionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private FraudAlertService fraudAlertService;

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @Test
    void AmountExceeds5000() {

        UserEntity user = new UserEntity();
        TransactionEntity tx = new TransactionEntity();
        tx.setTransaction_id(100L);
        tx.setAmount(new BigDecimal("6000"));
        tx.setUser(user);


        when(transactionRepository.countByUserAndTimestampAfter(eq(user), any(Instant.class)))
                .thenReturn(0L);


        fraudDetectionService.scanTransaction(tx);


        verify(fraudAlertService, times(1))
                .createFraudAlert(any(), eq(100L));
    }

    @Test
    void VelocityIsHigh() {

        UserEntity user = new UserEntity();
        TransactionEntity tx = new TransactionEntity();
        tx.setTransaction_id(200L);
        tx.setAmount(new BigDecimal("100")); // Amount is safe
        tx.setUser(user);


        when(transactionRepository.countByUserAndTimestampAfter(eq(user), any(Instant.class)))
                .thenReturn(6L);


        fraudDetectionService.scanTransaction(tx);


        verify(fraudAlertService, times(1))
                .createFraudAlert(any(), eq(200L));
    }

    @Test
    void TransactionIsClean() {

        UserEntity user = new UserEntity();
        TransactionEntity tx = new TransactionEntity();
        tx.setTransaction_id(300L);
        tx.setAmount(new BigDecimal("200")); // Safe Amount
        tx.setUser(user);


        when(transactionRepository.countByUserAndTimestampAfter(eq(user), any(Instant.class)))
                .thenReturn(1L);


        fraudDetectionService.scanTransaction(tx);

        verify(fraudAlertService, never()).createFraudAlert(any(), anyLong());
    }
}