package com.project.bankwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class BankWebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankWebAppApplication.class, args);
    }

    /*
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BcryptPasswordEncoder();
    }
    */


}
