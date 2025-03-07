package com.core.service;

import com.core.config.SeleniumProperties;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service for WebDriver navigation operations
 */
@Service
public class WebDriverNavigationService {

    @Autowired
    private WebDriverSessionService sessionService;

    @Autowired
    private SeleniumProperties seleniumProperties;

    /**
     * Navigate to a specific URL and wait for page to load completely
     *
     * @param sessionId The session ID of the WebDriver
     * @param url The URL to navigate to
     * @param timeoutSeconds Maximum time to wait for page load in seconds
     * @return Map with navigation result
     */
    public Map<String, Object> navigateToUrl(String sessionId, String url, int timeoutSeconds) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            // Set page load timeout
            driver.manage().timeouts().pageLoadTimeout(timeoutSeconds, TimeUnit.SECONDS);

            // Navigate to URL - this will block until page load complete or timeout
            driver.get(url);

            // Additional wait for any JavaScript to finish loading
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            // Wait for document.readyState to be 'complete'
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));

            result.put("success", true);
            result.put("url", url);
            result.put("currentUrl", driver.getCurrentUrl());

        } catch (Exception e) {
            // Log exception but don't fail - page might be partially loaded
            result.put("success", true); // Still consider it a success
            result.put("warning", "Page may not be fully loaded: " + e.getMessage());
            result.put("url", url);
            try {
                result.put("currentUrl", driver.getCurrentUrl());
            } catch (Exception ex) {
                result.put("currentUrl", "unknown");
            }
        }

        return result;
    }

    /**
     * Navigate to a specific URL with default timeout
     *
     * @param sessionId The session ID of the WebDriver
     * @param url The URL to navigate to
     * @return Map with navigation result
     */
    public Map<String, Object> navigateToUrl(String sessionId, String url) {
        return navigateToUrl(sessionId, url, seleniumProperties.getDefaultPageLoadTimeout());
    }

    /**
     * Get the current URL of a WebDriver session
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with current URL information
     */
    public Map<String, Object> getCurrentUrl(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String currentUrl = driver.getCurrentUrl();
            result.put("success", true);
            result.put("url", currentUrl);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting current URL: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get page title
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with page title information
     */
    public Map<String, Object> getTitle(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String title = driver.getTitle();
            result.put("success", true);
            result.put("title", title);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting title: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get page source
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with page source information
     */
    public Map<String, Object> getPageSource(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String pageSource = driver.getPageSource();
            result.put("success", true);
            result.put("pageSource", pageSource);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting page source: " + e.getMessage());
        }

        return result;
    }

    /**
     * Refresh the current page
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with refresh result
     */
    public Map<String, Object> refreshPage(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.navigate().refresh();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error refreshing page: " + e.getMessage());
        }

        return result;
    }

    /**
     * Navigate back in browser history
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with navigation result
     */
    public Map<String, Object> navigateBack(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.navigate().back();
            result.put("success", true);
            result.put("currentUrl", driver.getCurrentUrl());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error navigating back: " + e.getMessage());
        }

        return result;
    }

    /**
     * Navigate forward in browser history
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with navigation result
     */
    public Map<String, Object> navigateForward(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        WebDriver driver = sessionService.getDriver(sessionId);

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.navigate().forward();
            result.put("success", true);
            result.put("currentUrl", driver.getCurrentUrl());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error navigating forward: " + e.getMessage());
        }

        return result;
    }
}
