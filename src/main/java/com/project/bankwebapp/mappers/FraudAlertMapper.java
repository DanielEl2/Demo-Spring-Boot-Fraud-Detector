package com.project.bankwebapp.mappers;

import com.project.bankwebapp.DTO.FraudAlertDto;
import com.project.bankwebapp.Entities.FraudAlertEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
public class FraudAlertMapper implements Mapper<FraudAlertEntity, FraudAlertDto>{

    private ModelMapper modelMapper;

    public FraudAlertMapper(ModelMapper mapper) {
        this.modelMapper = mapper;
    }

    public FraudAlertDto mapTo(FraudAlertEntity entity){
        return modelMapper.map(entity,FraudAlertDto.class);
    }

    @Override
    public FraudAlertEntity mapFrom(FraudAlertDto fraudAlertDto) {
        return modelMapper.map(fraudAlertDto,FraudAlertEntity.class);
    }

}
