package com.core.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WebDriverService {

    @Autowired
    private ChromeOptions defaultChromeOptions;

    // Store active WebDriver instances
    private final Map<String, WebDriver> activeDrivers = new HashMap<>();

    /**
     * Initialize a new Chrome WebDriver instance with visible UI
     *
     * @return The session ID for the created WebDriver
     */
    public String initializeVisibleDriver() {
        ChromeOptions visibleOptions = new ChromeOptions();
        // Copy default options but remove headless mode
        visibleOptions.addArguments("--disable-gpu");
        visibleOptions.addArguments("--no-sandbox");
        visibleOptions.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(visibleOptions);
        String sessionId = UUID.randomUUID().toString();
        activeDrivers.put(sessionId, driver);
        return sessionId;
    }

    /**
     * Navigate to a specific URL
     *
     * @param sessionId The session ID of the WebDriver
     * @param url The URL to navigate to
     * @return true if navigation was successful, false otherwise
     */
    public boolean navigateToUrl(String sessionId, String url) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            driver.get(url);
            return true;
        }
        return false;
    }

    /**
     * Close a specific WebDriver instance
     *
     * @param sessionId The session ID of the WebDriver to close
     * @return true if the driver was closed successfully, false otherwise
     */
    public boolean closeDriver(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            try {
                driver.quit();
                activeDrivers.remove(sessionId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Get the current URL of a WebDriver session
     *
     * @param sessionId The session ID of the WebDriver
     * @return The current URL or null if the session doesn't exist
     */
    public String getCurrentUrl(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            return driver.getCurrentUrl();
        }
        return null;
    }

    /**
     * Get all active session IDs
     *
     * @return A map of session IDs to their current URLs
     */
    public Map<String, String> getActiveSessions() {
        Map<String, String> sessions = new HashMap<>();
        for (Map.Entry<String, WebDriver> entry : activeDrivers.entrySet()) {
            try {
                sessions.put(entry.getKey(), entry.getValue().getCurrentUrl());
            } catch (Exception e) {
                sessions.put(entry.getKey(), "Error: " + e.getMessage());
            }
        }
        return sessions;
    }
}
