package com.core.service;

import com.core.config.SeleniumProperties;
import com.core.service.serviceUtils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for WebDriver wait operations
 */
@Service
public class WebDriverWaitService {

    @Autowired
    private WebDriverSessionService sessionService;

    @Autowired
    private ElementReferenceManager elementReferenceManager;

    @Autowired
    private SeleniumProperties seleniumProperties;

    /**
     * Wait for an element using explicit wait
     *
     * @param sessionId The session ID of the WebDriver
     * @param locatorType Type of locator (xpath, id, css, etc.)
     * @param locatorValue Value of the locator
     * @param waitCondition Condition to wait for (visible, clickable, present, invisible)
     * @param timeoutSeconds Maximum wait time in seconds
     * @return Map with result of wait operation
     */
    public Map<String, Object> explicitWait(String sessionId, String locatorType, String locatorValue,
                                            String waitCondition, int timeoutSeconds) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        By locator;
        try {
            locator = LocatorUtils.createLocator(locatorType, locatorValue);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("error", "Invalid locator: " + e.getMessage());
            return result;
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            WebElement element = null;
            boolean success = false;

            switch (waitCondition.toLowerCase()) {
                case "visible":
                    element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    success = true;
                    break;
                case "clickable":
                    element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                    success = true;
                    break;
                case "present":
                    element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                    success = true;
                    break;
                case "invisible":
                    success = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
                    break;
                case "texttobe":
                    if (locatorValue.contains("|")) {
                        String[] parts = locatorValue.split("\\|", 2);
                        locator = LocatorUtils.createLocator(locatorType, parts[0]);
                        success = wait.until(ExpectedConditions.textToBe(locator, parts[1]));
                    } else {
                        result.put("success", false);
                        result.put("error", "For textToBe condition, locatorValue must be in format 'locator|expectedText'");
                        return result;
                    }
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid wait condition: " + waitCondition);
                    return result;
            }

            result.put("success", success);

            if (element != null) {
                String elementId = elementReferenceManager.storeElement(sessionId, element);
                result.put("elementId", elementId);

                try {
                    result.put("text", element.getText());
                } catch (Exception e) {
                    result.put("text", "");
                }

                try {
                    result.put("tagName", element.getTagName());
                } catch (Exception e) {
                    result.put("tagName", "unknown");
                }
            }

        } catch (TimeoutException e) {
            result.put("success", false);
            result.put("error", "Timeout waiting for condition: " + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error during wait: " + e.getMessage());
        }

        return result;
    }

    /**
     * Wait for an element using explicit wait with default timeout
     *
     * @param sessionId The session ID of the WebDriver
     * @param locatorType Type of locator (xpath, id, css, etc.)
     * @param locatorValue Value of the locator
     * @param waitCondition Condition to wait for (visible, clickable, present, invisible)
     * @return Map with result of wait operation
     */
    public Map<String, Object> explicitWait(String sessionId, String locatorType, String locatorValue,
                                            String waitCondition) {
        return explicitWait(sessionId, locatorType, locatorValue, waitCondition,
                seleniumProperties.getDefaultExplicitWaitTimeout());
    }

    /**
     * Wait for a specific amount of time (static wait)
     *
     * @param sessionId The session ID of the WebDriver
     * @param timeoutSeconds Time to wait in seconds
     * @return Map with wait result
     */
    public Map<String, Object> staticWait(String sessionId, int timeoutSeconds) {
        Map<String, Object> result = new HashMap<>();

        if (!sessionService.sessionExists(sessionId)) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Thread.sleep(timeoutSeconds * 1000L);
            result.put("success", true);
            result.put("waitTime", timeoutSeconds);
        } catch (InterruptedException e) {
            result.put("success", false);
            result.put("error", "Wait interrupted: " + e.getMessage());
        }

        return result;
    }

    /**
     * Wait for a JavaScript condition to be true
     *
     * @param sessionId The session ID of the WebDriver
     * @param script JavaScript code that returns a boolean
     * @param timeoutSeconds Maximum wait time in seconds
     * @return Map with wait result
     */
    public Map<String, Object> waitForJavaScriptCondition(String sessionId, String script, int timeoutSeconds) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean success = wait.until(webDriver -> {
                try {
                    Object jsResult = ((org.openqa.selenium.JavascriptExecutor) webDriver).executeScript(script);
                    if (jsResult instanceof Boolean) {
                        return (Boolean) jsResult;
                    } else if (jsResult != null) {
                        return Boolean.parseBoolean(jsResult.toString());
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }
            });

            result.put("success", success);

        } catch (TimeoutException e) {
            result.put("success", false);
            result.put("error", "Timeout waiting for JavaScript condition: " + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error during JavaScript wait: " + e.getMessage());
        }

        return result;
    }
}
