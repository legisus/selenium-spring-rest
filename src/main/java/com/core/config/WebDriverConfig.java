package com.core.config;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {

    /**
     * Initialize WebDriverManager for Chrome.
     * This will download and set up the appropriate ChromeDriver version.
     */
    @Bean(initMethod = "setup")
    public WebDriverManager webDriverManager() {
        return WebDriverManager.chromedriver();
    }

    /**
     * Configure ChromeOptions.
     * This will ensure Chrome starts in headless mode (no visible window) by default.
     */
    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }
}
