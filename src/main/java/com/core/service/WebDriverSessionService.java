package com.core.service;

import com.core.config.SeleniumProperties;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing WebDriver sessions
 */
@Service
public class WebDriverSessionService {

    @Autowired
    private SeleniumProperties seleniumProperties;

    @Autowired
    private ElementReferenceManager elementReferenceManager;

    // Store active WebDriver instances
    private final Map<String, WebDriver> activeDrivers = new HashMap<>();

    // Store implicit wait settings for each driver
    private final Map<String, Integer> implicitWaitSettings = new HashMap<>();

    /**
     * Initialize a new Chrome WebDriver instance with visible UI
     *
     * @return The session ID for the created WebDriver
     */
    public String initializeVisibleDriver() {
        // Create new Chrome options for visible browser
        ChromeOptions visibleOptions = new ChromeOptions();

        // Add stability arguments
        visibleOptions.addArguments("--disable-gpu");
        visibleOptions.addArguments("--no-sandbox");
        visibleOptions.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(visibleOptions);
        String sessionId = UUID.randomUUID().toString();
        activeDrivers.put(sessionId, driver);

        // Set default implicit wait
        int defaultImplicitWait = seleniumProperties.getDefaultImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(defaultImplicitWait, TimeUnit.SECONDS);
        implicitWaitSettings.put(sessionId, defaultImplicitWait);

        return sessionId;
    }

    /**
     * Get a WebDriver by session ID
     *
     * @param sessionId The session ID
     * @return The WebDriver or null if not found
     */
    public WebDriver getDriver(String sessionId) {
        return activeDrivers.get(sessionId);
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
                implicitWaitSettings.remove(sessionId);
                elementReferenceManager.clearSession(sessionId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
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

    /**
     * Set implicit wait timeout for a specific driver
     *
     * @param sessionId The session ID of the WebDriver
     * @param timeoutSeconds Timeout in seconds
     * @return True if successful, false if session not found
     */
    public boolean setImplicitWait(String sessionId, int timeoutSeconds) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(timeoutSeconds, TimeUnit.SECONDS);
            implicitWaitSettings.put(sessionId, timeoutSeconds);
            return true;
        }
        return false;
    }

    /**
     * Get the current implicit wait setting for a driver
     *
     * @param sessionId The session ID of the WebDriver
     * @return Current implicit wait in seconds, or null if session not found
     */
    public Integer getImplicitWait(String sessionId) {
        return implicitWaitSettings.get(sessionId);
    }

    /**
     * Check if a session exists
     *
     * @param sessionId The session ID to check
     * @return true if the session exists, false otherwise
     */
    public boolean sessionExists(String sessionId) {
        return activeDrivers.containsKey(sessionId);
    }

    /**
     * Clean up all WebDriver sessions when the service is destroyed
     */
    @PreDestroy
    public void cleanUp() {
        for (WebDriver driver : activeDrivers.values()) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Ignore exceptions during cleanup
            }
        }
        activeDrivers.clear();
        implicitWaitSettings.clear();
    }

    /**
     * Close all active WebDriver sessions
     *
     * @return Number of sessions closed
     */
    public int closeAllDrivers() {
        int closedCount = activeDrivers.size();

        for (Map.Entry<String, WebDriver> entry : new HashMap<>(activeDrivers).entrySet()) {
            try {
                String sessionId = entry.getKey();
                WebDriver driver = entry.getValue();
                driver.quit();
                activeDrivers.remove(sessionId);
                implicitWaitSettings.remove(sessionId);
                elementReferenceManager.clearSession(sessionId);
            } catch (Exception e) {
                // Log error but continue closing other sessions
                System.err.println("Error closing session: " + e.getMessage());
            }
        }

        return closedCount;
    }

    /**
     * Get all active session IDs
     *
     * @return List of active session IDs
     */
    public List<String> getActiveSessionIds() {
        return new ArrayList<>(activeDrivers.keySet());
    }
}
