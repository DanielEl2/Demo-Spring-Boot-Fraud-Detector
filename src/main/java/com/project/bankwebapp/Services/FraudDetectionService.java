package com.project.bankwebapp.Services;

import com.project.bankwebapp.Entities.FraudAlertEntity;
import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Repositories.FraudAlertRepository;
import com.project.bankwebapp.Repositories.TransactionRepository;
import com.project.bankwebapp.model.AlertStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {

    private final FraudAlertService fraudAlertService;
    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionRepository transactionRepository;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("5000");
    private static final int MAX_TRANSACTIONS_WINDOW = 5;
    private static final int TIME_WINDOW_MINUTES = 10;


    public FraudDetectionService(FraudAlertService fraudAlertService, FraudAlertRepository fraudAlertRepository, TransactionRepository transactionRepository) {
        this.fraudAlertService = fraudAlertService;
        this.fraudAlertRepository = fraudAlertRepository;
        this.transactionRepository = transactionRepository;
    }

    public void scanTransaction(TransactionEntity transaction) {

        List<String> fraudReasons = new ArrayList<>();

        if (transaction.getAmount().compareTo(MAX_AMOUNT) > 0) {
            System.out.println("FRAUD: Amount " + transaction.getAmount() + " > " + MAX_AMOUNT);

            fraudReasons.add("High Value Transaction detected");
        }

        // we check the current time minus the window we set above to see if are multiple transactions occuring
        // this way not only is the transaction at the limit set as a fraud but also the previous ones
        Instant timeWindowStart = Instant.now().minus(TIME_WINDOW_MINUTES, ChronoUnit.MINUTES);

        long recentCount = transactionRepository.countByUserAndTimestampAfter(transaction.getUser(), timeWindowStart);

        // recentCount includes the current one if it was already saved
        if (recentCount > MAX_TRANSACTIONS_WINDOW) {
            System.out.println("FRAUD: Velocity Check Failed. " + recentCount + " transactions in " + TIME_WINDOW_MINUTES + " mins");

            fraudReasons.add("High Velocity: Multiple transactions in short period of time");
        }
        // here we only save if there is a fraud alert
        if (!fraudReasons.isEmpty()) {

            String combinedDescription = String.join(", ", fraudReasons);


            createAlert(transaction, combinedDescription);
        }
    }

    private void createAlert(TransactionEntity transaction, String description) {
        // we create a new alert and set its properties then link it to the transaction
        FraudAlertEntity fraudAlertEntity = new FraudAlertEntity();
        fraudAlertEntity.setStatus(AlertStatus.OPEN);
        fraudAlertEntity.setDescription(description);
        fraudAlertEntity.setTimeStamp(Instant.now());

        fraudAlertService.createFraudAlert(fraudAlertEntity, transaction.getTransaction_id());
    }

}
