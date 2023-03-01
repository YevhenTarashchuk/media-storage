package com.media_storage.auth_core.config;

import com.media_storage.auth_data.model.response.UserDetailsResponse;
import com.media_storage.auth_core.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(false)
                .setFieldAccessLevel(PRIVATE);

        userDetailsMapping(mapper);
        return mapper;
    }

    private void userDetailsMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(UserEntity.class, UserDetailsResponse.class)
                .addMappings(mapper -> mapper.map(UserEntity::getId, UserDetailsResponse::setUserId));
    }
}