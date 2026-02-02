package com.project.bankwebapp.Services;

import com.project.bankwebapp.Entities.FraudAlertEntity;
import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Repositories.FraudAlertRepository;
import com.project.bankwebapp.Repositories.TransactionRepository;
import com.project.bankwebapp.model.AlertStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FraudAlertService {

    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;

    public FraudAlertService(TransactionRepository transactionRepository, FraudAlertRepository fraudAlertRepository){
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
    }

    public FraudAlertEntity findAlert(Long id){
        FraudAlertEntity fraudAlert = fraudAlertRepository.findById(id).orElseThrow(()->new RuntimeException("Alert not found"));
        return fraudAlert;
    }


    public FraudAlertEntity createFraudAlert(FraudAlertEntity fraudAlertEntity, Long transactionId) {

        // we find transaction, since it returns an optional we need to throw an exception if it is not found
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // check if it has alert
        if (transactionEntity.getFraudAlert() != null) {
            throw new RuntimeException("This transaction is already flagged");
        }


        fraudAlertEntity.setTransaction(transactionEntity);
        fraudAlertEntity.setStatus(AlertStatus.OPEN);

        return fraudAlertRepository.save(fraudAlertEntity);
    }

    public FraudAlertEntity resolveAlert(Long alertId, String status){
        FraudAlertEntity fraudAlert =  findAlert(alertId);
        fraudAlert.setStatus(AlertStatus.valueOf(status));
        return fraudAlertRepository.save(fraudAlert);

    }

    public List<FraudAlertEntity> findAll(){
        return fraudAlertRepository.findAll();
    }

    public List<FraudAlertEntity> findUserAlerts(UUID userId){
        return fraudAlertRepository.findAllByUserId(userId);
    }
}
