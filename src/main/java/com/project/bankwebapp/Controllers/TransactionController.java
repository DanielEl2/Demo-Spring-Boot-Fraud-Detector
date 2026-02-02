package com.project.bankwebapp.Controllers;

import com.project.bankwebapp.DTO.TransactionDto;
import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Services.TransactionService;
import com.project.bankwebapp.Services.UserService;
import com.project.bankwebapp.mappers.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class TransactionController {

    private TransactionService transactionService;
    private Mapper<TransactionEntity, TransactionDto> transactionMapper;
    private UserService userService;

    public TransactionController(TransactionService transactionService, Mapper<TransactionEntity, TransactionDto> transactionMapper, UserService userService) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.userService = userService;
    }
    @PostMapping(path = "/transactions")
    public TransactionDto createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionEntity transactionEntity = transactionMapper.mapFrom(transactionDto);
        UUID userid = transactionDto.getUser();
        TransactionEntity transactionEntity1 = transactionService.createTransaction(transactionEntity, userid);
        return transactionMapper.mapTo(transactionEntity1);
    }

    @GetMapping(path = "/transactions")
    public List<TransactionDto> getAllTransactions(@RequestAttribute("authenticatedUserId") UUID user_id) {
        UserEntity user = userService.findUserById(user_id);
        if(user.getRole().toString().equals("USER")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access Denied");
        }
        List<TransactionEntity> transactionEntities = transactionService.getAllTransactions();
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntities) {
            transactionDtos.add(transactionMapper.mapTo(transactionEntity));
        }
        return transactionDtos;
    }

    @GetMapping(path = "/transactions/{user_id}")
    public List<TransactionDto> getTransactionsByUser(@PathVariable("user_id") UUID userId, @RequestAttribute("authenticatedUserId") UUID user_id) {
        if(!userId.toString().equals(user_id.toString())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access Denied");
        }

        List<TransactionEntity> transactionEntities = transactionService.getAllTransactionsbyUser(userId);
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntities) {
            transactionDtos.add(transactionMapper.mapTo(transactionEntity));
        }
        return transactionDtos;
    }

    @GetMapping(path = "/transactions/{user_id}/{t_id}")
    public TransactionDto getTransactionById(@PathVariable("user_id") UUID userId, @PathVariable("t_id") Long transactionId) {
        TransactionEntity transactionEntity = transactionService.getTransaction(transactionId, userId);
        return transactionMapper.mapTo(transactionEntity);
    }

}


