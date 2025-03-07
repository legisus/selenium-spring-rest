package com.core.config;

import com.core.service.WebDriverServiceExpanded;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfig {

    /**
     * Register the expanded WebDriver service as the primary implementation
     */
    @Bean
    @Primary
    public WebDriverServiceExpanded webDriverService() {
        return new WebDriverServiceExpanded();
    }
}
