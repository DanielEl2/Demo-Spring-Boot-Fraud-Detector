package com.project.bankwebapp.mappers;

import com.project.bankwebapp.DTO.UserDto;
import com.project.bankwebapp.Entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<UserEntity, UserDto>{

    private ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        userDto.setPassword(null);
        return userDto;
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto,UserEntity.class);
    }
}
