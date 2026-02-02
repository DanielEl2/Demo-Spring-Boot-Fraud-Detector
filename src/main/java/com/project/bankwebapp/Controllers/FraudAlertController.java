package com.project.bankwebapp.Controllers;

import com.project.bankwebapp.DTO.FraudAlertDto;
import com.project.bankwebapp.Entities.FraudAlertEntity;
import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Services.FraudAlertService;
import com.project.bankwebapp.Services.TransactionService;
import com.project.bankwebapp.Services.UserService;
import com.project.bankwebapp.mappers.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
public class FraudAlertController {

    private FraudAlertService fraudAlertService;
    private Mapper<FraudAlertEntity,FraudAlertDto> fraudmapper;
    private TransactionService transactionService;
    private UserService userService;

    public FraudAlertController(FraudAlertService fraudAlertService, Mapper<FraudAlertEntity,FraudAlertDto> fraudmapper, TransactionService transactionService, UserService userService) {
        this.fraudmapper = fraudmapper;
        this.fraudAlertService = fraudAlertService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping(path = "/alerts/{userId}/{fraudId}")
    public FraudAlertDto findSingleAlert(@PathVariable("userId") UUID userId, @PathVariable("fraudId") Long fraudId){
        FraudAlertEntity fraudAlert = fraudAlertService.findAlert(fraudId);
        return fraudmapper.mapTo(fraudAlert);

    }

    @GetMapping(path = "/alerts/{userId}")
    public List<FraudAlertDto> findUserAlerts(@PathVariable("userId") UUID userId, @RequestAttribute("authenticatedUserId") UUID user_id){
        UserEntity user = userService.findUserById(user_id);
        if(!userId.toString().equals(user_id.toString())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access Denied");
        }
        List<FraudAlertEntity> fraudAlertEntities = fraudAlertService.findUserAlerts(userId);
        List<FraudAlertDto> fraudAlertDtos = new ArrayList<>();
        for(FraudAlertEntity fraudAlertEntity : fraudAlertEntities){
            FraudAlertDto fraudAlertDto = fraudmapper.mapTo(fraudAlertEntity);
            fraudAlertDtos.add(fraudAlertDto);
        }
        return fraudAlertDtos;
    }

    @GetMapping(path = "/alerts")
    public List<FraudAlertDto> getAllAlerts(@RequestAttribute("authenticatedUserId") UUID userId){
        UserEntity user = userService.findUserById(userId);
        if(!user.getRole().equals("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access Denied");
        }
        List<FraudAlertEntity> fraudAlertEntities = fraudAlertService.findAll();
        List<FraudAlertDto> fraudAlertDtos = new ArrayList<>();
        for(FraudAlertEntity fraudAlertEntity : fraudAlertEntities){
            FraudAlertDto fraudAlertDto = fraudmapper.mapTo(fraudAlertEntity);
            fraudAlertDto.setTransaction_id(fraudAlertEntity.getTransaction().getTransaction_id());
            fraudAlertDtos.add(fraudAlertDto);
        }

        return fraudAlertDtos;
    }

    @PatchMapping(path = "/alerts/{fraudId}")
    public FraudAlertDto resolveAlert(@PathVariable("fraudId") Long fraudId, @RequestBody Map<String,String> update,@RequestAttribute("authenticatedUserId") UUID user_id){
        UserEntity user = userService.findUserById(user_id);
        if(!user.getUser_id().toString().equals(user_id.toString())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access Denied");
        }
        String status = update.get("status");
        FraudAlertEntity fraudAlert = fraudAlertService.resolveAlert(fraudId, status);


        Long tID = fraudAlert.getTransaction().getTransaction_id();

        if ("ACCEPTED".equals(status)) {

            transactionService.updateTransactionStatus(tID);
        }

        FraudAlertDto updated = fraudmapper.mapTo(fraudAlert);
        updated.setTransaction_id(tID);
        return updated;
    }


}
