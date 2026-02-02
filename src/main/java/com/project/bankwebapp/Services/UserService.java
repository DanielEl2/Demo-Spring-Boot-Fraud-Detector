package com.project.bankwebapp.Services;


import com.project.bankwebapp.Entities.UserEntity;
import com.project.bankwebapp.Repositories.UserRepository;
import com.project.bankwebapp.model.Role;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,  BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity createUser(UserEntity user) {
        boolean usernamecheck = checkUsernameExists(user.getUsername());
        boolean emailcheck = checkEmailExists(user.getEmail());
        if(usernamecheck){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Username already exists");
        }
        else if(emailcheck){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
        }
        if(user.getRole() == null){
            user.setRole(Role.USER);
        }
        String rawpass =  user.getPassword();
        String encodedpass = passwordEncoder.encode(rawpass);
        user.setPassword(encodedpass);
        return userRepository.save(user);
    }



    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity findUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
    }
    public UserEntity findUserByUsername(String username){
            return userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));

    }

    public UserEntity findUserByEmail(String Email){
        return userRepository.findByEmail(Email).orElseThrow(()->new RuntimeException("User not found"));
    }

    public boolean checkUserPassword(UUID userId, String password) {
        UserEntity user = findUserById(userId);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public void deleteUser(UUID userId) {
        UserEntity user = findUserById(userId);
        userRepository.delete(user);
    }

    public boolean checkUsernameExists(String username){
        try{
            findUserByUsername(username);
             return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public boolean checkEmailExists(String Email){
        try{
            findUserByEmail(Email);
            return true;
        }
        catch (Exception e){
            return false;
        }

    }

    public UserEntity updateUser(UUID userId, UserEntity updates) {
        UserEntity existingUser = findUserById(userId);
        if (updates.getFirstName() != null) {

            existingUser.setFirstName(updates.getFirstName());


        }
        if (updates.getLastName() != null) {
            existingUser.setLastName(updates.getLastName());
        }

        if (updates.getUsername() != null) {
            Optional<UserEntity> existing = userRepository.findByUsername(updates.getUsername());


            if (existing.isPresent() && !existing.get().getUser_id().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
            }


            existingUser.setUsername(updates.getUsername());
        }


        if (updates.getEmail() != null) {
            Optional<UserEntity> existing = userRepository.findByEmail(updates.getEmail());

            if (existing.isPresent() && !existing.get().getUser_id().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }

            existingUser.setEmail(updates.getEmail());
        }

        if (updates.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updates.getPassword()));
        }


        return userRepository.save(existingUser);
    }


}
