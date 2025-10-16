package com.pines.flutter.capacitacion.api.config;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonDefaultFiltersCustomizer() {
        return builder -> {
            SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                    .setFailOnUnknownId(false)
                    // Default no-op filter: include all fields when no explicit filter is supplied
                    .addFilter("PokemonDTOFilter", SimpleBeanPropertyFilter.serializeAll());
            builder.filters(filterProvider);
        };
    }
}


