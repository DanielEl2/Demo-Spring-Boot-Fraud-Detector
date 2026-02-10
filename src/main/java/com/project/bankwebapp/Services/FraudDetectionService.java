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
    private final LocationService locationService;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("5000");
    private static final int MAX_TRANSACTIONS_WINDOW = 30;
    private static final int TIME_WINDOW_MINUTES = 10;
    private static final int TRAVEL_SPEED = 800;


    public FraudDetectionService(FraudAlertService fraudAlertService, FraudAlertRepository fraudAlertRepository,
                                 TransactionRepository transactionRepository, LocationService locationService) {
        this.fraudAlertService = fraudAlertService;
        this.fraudAlertRepository = fraudAlertRepository;
        this.transactionRepository = transactionRepository;
        this.locationService = locationService;
    }

    public List<String> scanTransaction(TransactionEntity transaction) {

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
        if (recentCount >= MAX_TRANSACTIONS_WINDOW) {
            System.out.println("FRAUD: Velocity Check Failed. " + recentCount + " transactions in " + TIME_WINDOW_MINUTES + " mins");

            fraudReasons.add("High Velocity: Multiple transactions in short period of time");
        }
        TransactionEntity mostRecent = transactionRepository.findLastTransactionForUser(transaction.getUser().getUser_id());
        if(mostRecent!=null){
            long seconddiff = ChronoUnit.SECONDS.between(mostRecent.getTimestamp(), transaction.getTimestamp());
            double hourdiff = Math.abs(seconddiff/3600.0);
            double realdistance = locationService.getDistance(mostRecent.getLocation(),transaction.getLocation());
            if(realdistance>TRAVEL_SPEED*hourdiff){
                fraudReasons.add("Impossible Travel: Transaction has occured too far away from previous one");
            }
        }


        return fraudReasons;
    }

    public void createAlert(TransactionEntity transaction, String description) {
        // we create a new alert and set its properties then link it to the transaction
        FraudAlertEntity fraudAlertEntity = new FraudAlertEntity();
        fraudAlertEntity.setStatus(AlertStatus.OPEN);
        fraudAlertEntity.setDescription(description);
        fraudAlertEntity.setTimeStamp(Instant.now());

        fraudAlertService.createFraudAlert(fraudAlertEntity, transaction.getTransaction_id());
    }

}
