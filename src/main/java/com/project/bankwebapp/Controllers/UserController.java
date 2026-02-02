package com.project.bankwebapp.Controllers;


import com.project.bankwebapp.DTO.LoginRequestDto;
import com.project.bankwebapp.DTO.LoginReturnDto;
import com.project.bankwebapp.DTO.UpdateUserDto;
import com.project.bankwebapp.DTO.UserDto;
import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Services.JwtService;
import com.project.bankwebapp.Services.UserService;
import com.project.bankwebapp.mappers.Mapper;
import com.project.bankwebapp.mappers.UserUpdateMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController

public class UserController {

    private Mapper<UserEntity, UserDto> userMapper;
    private UserService userService;
    private UserUpdateMapper userUpdateMapper;
    private JwtService jwtService;
    public UserController(Mapper<UserEntity,UserDto> userMapper, UserService userService, UserUpdateMapper userUpdateMapper, JwtService jwtService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.userUpdateMapper = userUpdateMapper;
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/SignUp")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto){
        UserEntity userentity = userMapper.mapFrom(userDto);
        UserEntity userEntity = userService.createUser(userentity);
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
        return loginUser(loginRequest);
    }

    @GetMapping(path = "/users")
    public List<UserDto> getAllUsers(){
        List<UserEntity> users = userService.findAllUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for(UserEntity user : users){
            userDtos.add(userMapper.mapTo(user));

        }
        return userDtos;
    }

    @GetMapping(path = "/users/{user_id}")
    public UserDto getUser(@PathVariable("user_id") UUID user_id){
        UserEntity user =  userService.findUserById(user_id);
        return  userMapper.mapTo(user);

    }

    @DeleteMapping(path = "/users/{user_id}")
    public void deleteUser(@PathVariable("user_id") UUID user_id, @RequestAttribute("authenticatedUserId") UUID user_id1){
        UserEntity user =  userService.findUserById(user_id);
        if(user_id.toString().equals(user_id1.toString()) || user.getRole().toString().equals("ADMIN")){
            userService.deleteUser(user_id);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own profile");
        }

    }


    @PatchMapping(path="/users/{user_id}")
    public UserDto updateUser(@PathVariable("user_id") UUID user_id, @RequestBody UpdateUserDto updateUserDto, @RequestAttribute("authenticatedUserId") UUID user_id1){
        if(!user_id.equals(user_id1)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile");
        }
        UserEntity updates = userUpdateMapper.mapFrom(updateUserDto);
        UserEntity updatedUser = userService.updateUser(user_id,updates);
        return userMapper.mapTo(updatedUser);
    }
    @PostMapping(path = "/Login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {

        // find user, and check password
        // generate the token and return a json including all necessary data
        UserEntity user = userService.findUserByUsername(loginRequestDto.getUsername());


        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }


        boolean correct = userService.checkUserPassword(user.getUser_id(), loginRequestDto.getPassword());

        if (correct) {

            String token = jwtService.generateToken(user.getUsername(), user.getRole(), user.getUser_id());


            LoginReturnDto returnJson = LoginReturnDto.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .userId(user.getUser_id())
                    .build();

            return ResponseEntity.ok(returnJson);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }





}
