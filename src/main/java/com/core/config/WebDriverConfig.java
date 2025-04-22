package com.core.config;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {

    @Autowired
    private SeleniumProperties seleniumProperties;

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

        // Add default arguments based on configuration
        if (seleniumProperties.isHeadlessByDefault()) {
            options.addArguments("--headless=new");
        }

        // Add standard arguments for stability
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");

        // Add any custom arguments from properties
        if (seleniumProperties.getChromeOptions() != null && !seleniumProperties.getChromeOptions().isEmpty()) {
            options.addArguments(seleniumProperties.getChromeOptions());
        }

        return options;
    }
}
