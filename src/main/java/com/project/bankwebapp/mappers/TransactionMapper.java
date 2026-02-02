package com.project.bankwebapp.mappers;

import com.project.bankwebapp.DTO.TransactionDto;
import com.project.bankwebapp.Entities.TransactionEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements Mapper<TransactionEntity, TransactionDto> {

    private final ModelMapper modelMapper;

    public TransactionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TransactionDto mapTo(TransactionEntity entity) {

        TransactionDto dto = modelMapper.map(entity, TransactionDto.class);

        //we only mark as fraud if not accepted
        if (entity.getFraudAlert() != null) {


            String alertStatus = entity.getFraudAlert().getStatus().toString();

            if ("ACCEPTED".equals(alertStatus)) {
                dto.setStatus("clean");
            } else {
                dto.setStatus("fraud");
            }

        } else {
            dto.setStatus("clean");
        }

        return dto;
    }

    @Override
    public TransactionEntity mapFrom(TransactionDto dto) {

        return modelMapper.map(dto, TransactionEntity.class);
    }
}
