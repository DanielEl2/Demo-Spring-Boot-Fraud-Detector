package com.project.bankwebapp.mappers;

import com.project.bankwebapp.DTO.UpdateUserDto;
import com.project.bankwebapp.DTO.UserDto;
import com.project.bankwebapp.Entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateMapper implements Mapper<UserEntity, UpdateUserDto> {

    private ModelMapper mapper;

    public UserUpdateMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public UpdateUserDto mapTo(UserEntity userEntity) {
        return mapper.map(userEntity, UpdateUserDto.class);

    }

    public UserEntity mapFrom(UpdateUserDto updateUserDto) {
        return mapper.map(updateUserDto, UserEntity.class);
    }

}
