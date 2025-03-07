package com.core.service;

import com.core.service.serviceUtils.LocatorUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for frame and alert handling
 */
@Service
public class WebDriverFrameService {

    @Autowired
    private WebDriverSessionService sessionService;

    /**
     * Switch to an iframe
     *
     * @param sessionId The session ID of the WebDriver
     * @param frameLocator Type of locator for the frame
     * @param frameValue Value of the locator
     * @return Map with success or error information
     */
    public Map<String, Object> switchToFrame(String sessionId, String frameLocator, String frameValue) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            if ("index".equalsIgnoreCase(frameLocator)) {
                // Switch by index
                int index = Integer.parseInt(frameValue);
                driver.switchTo().frame(index);
            } else if ("name".equalsIgnoreCase(frameLocator) || "id".equalsIgnoreCase(frameLocator)) {
                // Switch by name or id
                driver.switchTo().frame(frameValue);
            } else {
                // Switch by locator (element)
                By locator = LocatorUtils.createLocator(frameLocator, frameValue);
                WebElement frameElement = driver.findElement(locator);
                driver.switchTo().frame(frameElement);
            }

            result.put("success", true);
            result.put("message", "Switched to frame successfully");
        } catch (NoSuchFrameException e) {
            result.put("success", false);
            result.put("error", "Frame not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("error", "Invalid frame index: " + frameValue);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error switching to frame: " + e.getMessage());
        }

        return result;
    }

    /**
     * Switch back to default content (out of all frames)
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> switchToDefaultContent(String sessionId) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.switchTo().defaultContent();
            result.put("success", true);
            result.put("message", "Switched to default content successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error switching to default content: " + e.getMessage());
        }

        return result;
    }

    /**
     * Switch to parent frame
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> switchToParentFrame(String sessionId) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.switchTo().parentFrame();
            result.put("success", true);
            result.put("message", "Switched to parent frame successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error switching to parent frame: " + e.getMessage());
        }

        return result;
    }

    /**
     * Accept or dismiss an alert
     *
     * @param sessionId The session ID of the WebDriver
     * @param accept Whether to accept (true) or dismiss (false) the alert
     * @return Map with alert text and success or error information
     */
    public Map<String, Object> handleAlert(String sessionId, boolean accept) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            if (accept) {
                alert.accept();
                result.put("action", "accepted");
            } else {
                alert.dismiss();
                result.put("action", "dismissed");
            }

            result.put("success", true);
            result.put("alertText", alertText);
        } catch (NoAlertPresentException e) {
            result.put("success", false);
            result.put("error", "No alert present");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error handling alert: " + e.getMessage());
        }

        return result;
    }

    /**
     * Send text to an alert prompt
     *
     * @param sessionId The session ID of the WebDriver
     * @param text Text to send to the prompt
     * @param accept Whether to accept (true) or dismiss (false) after sending text
     * @return Map with alert text and success or error information
     */
    public Map<String, Object> sendTextToAlert(String sessionId, String text, boolean accept) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            alert.sendKeys(text);

            if (accept) {
                alert.accept();
                result.put("action", "accepted");
            } else {
                alert.dismiss();
                result.put("action", "dismissed");
            }

            result.put("success", true);
            result.put("alertText", alertText);
            result.put("textSent", text);
        } catch (NoAlertPresentException e) {
            result.put("success", false);
            result.put("error", "No alert present");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error sending text to alert: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get alert text without accepting or dismissing
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with alert text or error information
     */
    public Map<String, Object> getAlertText(String sessionId) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            result.put("success", true);
            result.put("alertText", alertText);
        } catch (NoAlertPresentException e) {
            result.put("success", false);
            result.put("error", "No alert present");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting alert text: " + e.getMessage());
        }

        return result;
    }
}
