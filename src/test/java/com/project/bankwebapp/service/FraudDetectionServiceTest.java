package com.project.bankwebapp.service;

import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Repositories.FraudAlertRepository;
import com.project.bankwebapp.Repositories.TransactionRepository;
import com.project.bankwebapp.Services.FraudAlertService;
import com.project.bankwebapp.Services.FraudDetectionService;
import com.project.bankwebapp.Services.LocationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private FraudAlertService fraudAlertService;
    @Mock
    private LocationService locationService;

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @Test
    void AmountExceeds5000() {

        UserEntity user = new UserEntity();
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransaction_id(100L);
        transaction.setAmount(new BigDecimal("6000"));
        transaction.setUser(user);


        when(transactionRepository.countByUserAndTimestampAfter(eq(user), any(Instant.class)))
                .thenReturn(0L);


        List<String> reasons = fraudDetectionService.scanTransaction(transaction);


        assertEquals(1, reasons.size());
        assertTrue(reasons.getFirst().contains("High Value Transaction detected"));
    }

    @Test
    void VelocityIsHigh() {

        UserEntity user = new UserEntity();
        TransactionEntity transac = new TransactionEntity();
        transac.setTransaction_id(200L);
        transac.setAmount(new BigDecimal("100"));
        transac.setUser(user);

        // we are returning that 35 transactions have occured so it flags
        when(transactionRepository.countByUserAndTimestampAfter(eq(user), any(Instant.class)))
                .thenReturn(35L);


        List<String> reasons = fraudDetectionService.scanTransaction(transac);


        assertEquals(1, reasons.size());
        assertTrue(reasons.getFirst().contains("High Velocity: Multiple transactions in short period of time"));
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

    @Test
    void ImpossibleDistance() {
        // we create user
        UUID userId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setUser_id(userId);

        // create transaction for one hour ago
        TransactionEntity prevTrans = new TransactionEntity();
        prevTrans.setTransaction_id(100L);
        prevTrans.setUser(user);
        prevTrans.setLocation("New York, NY");
        prevTrans.setTimestamp(Instant.now().minus(1, ChronoUnit.HOURS));

        // create current transaction
        TransactionEntity currentTrans = new TransactionEntity();
        currentTrans.setTransaction_id(200L);
        currentTrans.setUser(user);
        currentTrans.setLocation("London, UK");
        currentTrans.setTimestamp(Instant.now());
        currentTrans.setAmount(new BigDecimal("50"));



        // we use when here which allows us to mimic out repository calls since we dont actually have a db connection

        // we use any because we dont care what value is passed as long as its actually an instant value
        when(transactionRepository.countByUserAndTimestampAfter(eq(user), any(Instant.class))).thenReturn(0L);

        // when they ask for the previous transaction we provide the transaction we created before
        when(transactionRepository.findLastTransactionForUser(userId)).thenReturn(prevTrans);

        // we then create a fake distance so that we can test if its working
        when(locationService.getDistance("New York, NY", "London, UK")).thenReturn(5000.0);


        List<String> reasons = fraudDetectionService.scanTransaction(currentTrans);


        assertEquals(1, reasons.size());
        assertTrue(reasons.getFirst().contains("Impossible Travel: Transaction has occured too far away from previous one"));
    }
}