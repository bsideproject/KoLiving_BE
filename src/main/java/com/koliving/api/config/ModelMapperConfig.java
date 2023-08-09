package com.koliving.api.config;

import com.koliving.api.user.Gender;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
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

        Converter<Integer, Gender> intToGender = new AbstractConverter<Integer, Gender>() {
            @Override
            protected Gender convert(Integer source) {
            return switch (source) {
                case 0 -> Gender.MALE;
                case 1 -> Gender.FEMALE;
                case 2 -> Gender.OTHER;
                default -> throw new IllegalStateException("Unexpected value: " + source);
            };
            }
        };

        modelMapper.addConverter(intToGender);

        return modelMapper;
    }
}
