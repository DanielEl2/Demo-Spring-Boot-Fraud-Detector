package com.project.bankwebapp.Services;


import com.project.bankwebapp.DTO.TransactionDto;
import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Repositories.TransactionRepository;
import com.project.bankwebapp.Repositories.UserRepository;
import com.project.bankwebapp.mappers.Mapper;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private UserService userService;
    private FraudDetectionService fraudDetectionService;

    public TransactionService(TransactionRepository transactionRepository,UserRepository userRepository, UserService userService , FraudDetectionService fraudDetectionService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.fraudDetectionService = fraudDetectionService;

    }

    public TransactionEntity createTransaction(TransactionEntity transactionEntity, UUID userid) {
        UserEntity userEntity = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("User not found"));
        transactionEntity.setUser(userEntity);
        List<String> fraudReasons = fraudDetectionService.scanTransaction(transactionEntity);
        TransactionEntity savedTransaction =  transactionRepository.save(transactionEntity);

        if(!fraudReasons.isEmpty()){
            String combinedDescription = String.join(", ", fraudReasons);


            fraudDetectionService.createAlert(savedTransaction, combinedDescription);
        }



        return savedTransaction;

    }
    public List<TransactionEntity> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public TransactionEntity getTransaction(long transaction_id, UUID userId) {
        return transactionRepository.findByUserIdAndTransactionId(userId,transaction_id).orElseThrow(()->new RuntimeException("Transaction not found"));

    }

    public List<TransactionEntity> getAllTransactionsbyUser(UUID userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    public void deleteTransaction(long transaction_id, UUID userId) {
        if (!transactionRepository.existsByUserIdAndId(userId, transaction_id)) {
            throw new RuntimeException("Transaction not found or does not belong to user");
        }
        transactionRepository.deleteByUserId(transaction_id, userId);

    }

    public void updateTransactionStatus(Long transactionId) {

        TransactionEntity transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));


        transaction.setStatus("CLEAN");


        transactionRepository.save(transaction);
    }


}
