package com.moneymanger.moneytracker.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileConfiguration {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
