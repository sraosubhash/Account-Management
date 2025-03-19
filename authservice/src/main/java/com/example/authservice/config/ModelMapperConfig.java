package com.example.authservice.config;

import com.example.authservice.dto.UserDTO;
import com.example.authservice.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configuration
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Custom mappings for User -> UserDTO
        modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.map(User::getId, UserDTO::setId);
                    mapper.map(User::getEmail, UserDTO::setEmail);
                    mapper.map(User::getFirstName, UserDTO::setFirstName);
                    mapper.map(User::getLastName, UserDTO::setLastName);
                    mapper.map(User::getRole, UserDTO::setRole);
                    mapper.map(User::getAlternatePhone, UserDTO::setAlternatePhone);
                    mapper.map(User::getAddress, UserDTO::setAddress);
                });

        return modelMapper;
    }
}