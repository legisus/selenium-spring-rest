package com.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Selenium WebDriver
 */
@Component
@ConfigurationProperties(prefix = "selenium")
public class SeleniumProperties {

    /**
     * Default page load timeout in seconds
     */
    private int defaultPageLoadTimeout = 30;

    /**
     * Default explicit wait timeout in seconds
     */
    private int defaultExplicitWaitTimeout = 30;

    /**
     * Default implicit wait timeout in seconds
     */
    private int defaultImplicitWaitTimeout = 0;

    /**
     * Whether to run Chrome in headless mode by default
     */
    private boolean headlessByDefault = true;

    /**
     * Additional Chrome options
     */
    private List<String> chromeOptions = new ArrayList<>();

    public int getDefaultPageLoadTimeout() {
        return defaultPageLoadTimeout;
    }

    public void setDefaultPageLoadTimeout(int defaultPageLoadTimeout) {
        this.defaultPageLoadTimeout = defaultPageLoadTimeout;
    }

    public int getDefaultExplicitWaitTimeout() {
        return defaultExplicitWaitTimeout;
    }

    public void setDefaultExplicitWaitTimeout(int defaultExplicitWaitTimeout) {
        this.defaultExplicitWaitTimeout = defaultExplicitWaitTimeout;
    }

    public int getDefaultImplicitWaitTimeout() {
        return defaultImplicitWaitTimeout;
    }

    public void setDefaultImplicitWaitTimeout(int defaultImplicitWaitTimeout) {
        this.defaultImplicitWaitTimeout = defaultImplicitWaitTimeout;
    }

    public boolean isHeadlessByDefault() {
        return headlessByDefault;
    }

    public void setHeadlessByDefault(boolean headlessByDefault) {
        this.headlessByDefault = headlessByDefault;
    }

    public List<String> getChromeOptions() {
        return chromeOptions;
    }

    public void setChromeOptions(List<String> chromeOptions) {
        this.chromeOptions = chromeOptions;
    }
}
