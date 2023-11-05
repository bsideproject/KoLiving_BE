package com.koliving.api.config;

import com.koliving.api.dto.ProfileDto;
import com.koliving.api.user.domain.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);

        modelMapper.typeMap(ProfileDto.class, User.class).addMappings(mapper -> {
            mapper.map(ProfileDto::toGender, User::setGender);
        });

        return modelMapper;
    }
}
