package com.core.service;

import com.core.service.serviceUtils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for WebDriver element operations
 */
@Service
public class WebDriverElementService {

    @Autowired
    private WebDriverSessionService sessionService;

    @Autowired
    private ElementReferenceManager elementReferenceManager;

    /**
     * Find an element using various locator strategies
     *
     * @param sessionId The session ID of the WebDriver
     * @param locatorType Type of locator (xpath, id, css, etc.)
     * @param locatorValue Value of the locator
     * @return Map with element details or error information
     */
    public Map<String, Object> findElement(String sessionId, String locatorType, String locatorValue) {
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
            WebElement element = driver.findElement(locator);
            result.put("success", true);
            result.put("found", true);

            try {
                result.put("tagName", element.getTagName());
            } catch (Exception e) {
                result.put("tagName", "unknown");
            }

            try {
                result.put("displayed", element.isDisplayed());
            } catch (Exception e) {
                result.put("displayed", false);
            }

            try {
                result.put("enabled", element.isEnabled());
            } catch (Exception e) {
                result.put("enabled", false);
            }

            try {
                result.put("selected", element.isSelected());
            } catch (Exception e) {
                result.put("selected", false);
            }

            try {
                result.put("text", element.getText());
            } catch (Exception e) {
                result.put("text", "");
            }

            try {
                result.put("value", element.getAttribute("value"));
            } catch (Exception e) {
                result.put("value", null);
            }

            // Store element reference and add ID to result
            String elementId = elementReferenceManager.storeElement(sessionId, element);
            result.put("elementId", elementId);

        } catch (NoSuchElementException e) {
            result.put("success", false);
            result.put("found", false);
            result.put("error", "Element not found: " + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error finding element: " + e.getMessage());
        }

        return result;
    }

    /**
     * Find multiple elements using various locator strategies
     *
     * @param sessionId The session ID of the WebDriver
     * @param locatorType Type of locator (xpath, id, css, etc.)
     * @param locatorValue Value of the locator
     * @return Map with elements details or error information
     */
    public Map<String, Object> findElements(String sessionId, String locatorType, String locatorValue) {
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
            List<WebElement> elements = driver.findElements(locator);
            List<Map<String, Object>> elementDetails = new ArrayList<>();

            for (WebElement element : elements) {
                Map<String, Object> details = new HashMap<>();

                try {
                    details.put("tagName", element.getTagName());
                } catch (Exception e) {
                    details.put("tagName", "unknown");
                }

                try {
                    details.put("displayed", element.isDisplayed());
                } catch (Exception e) {
                    details.put("displayed", false);
                }

                try {
                    details.put("enabled", element.isEnabled());
                } catch (Exception e) {
                    details.put("enabled", false);
                }

                try {
                    details.put("selected", element.isSelected());
                } catch (Exception e) {
                    details.put("selected", false);
                }

                try {
                    details.put("text", element.getText());
                } catch (Exception e) {
                    details.put("text", "");
                }

                try {
                    details.put("value", element.getAttribute("value"));
                } catch (Exception e) {
                    details.put("value", null);
                }

                // Store element reference and add ID to result
                String elementId = elementReferenceManager.storeElement(sessionId, element);
                details.put("elementId", elementId);

                elementDetails.add(details);
            }

            result.put("success", true);
            result.put("count", elements.size());
            result.put("elements", elementDetails);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error finding elements: " + e.getMessage());
        }

        return result;
    }

    /**
     * Click on an element
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID to click
     * @return Map with success or error information
     */
    public Map<String, Object> clickElement(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            element.click();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error clicking element: " + e.getMessage());
        }

        return result;
    }

    /**
     * Send keys to an element
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID to send keys to
     * @param text The text to send
     * @param clearFirst Whether to clear the field first
     * @return Map with success or error information
     */
    public Map<String, Object> sendKeys(String sessionId, String elementId, String text, boolean clearFirst) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            if (clearFirst) {
                element.clear();
            }
            element.sendKeys(text);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error sending keys: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get element attribute
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID
     * @param attributeName The attribute name
     * @return Map with attribute value or error information
     */
    public Map<String, Object> getElementAttribute(String sessionId, String elementId, String attributeName) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            String attributeValue = element.getAttribute(attributeName);
            result.put("success", true);
            result.put("attributeName", attributeName);
            result.put("attributeValue", attributeValue);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting attribute: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get element text
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID
     * @return Map with element text or error information
     */
    public Map<String, Object> getElementText(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            String text = element.getText();
            result.put("success", true);
            result.put("text", text);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting text: " + e.getMessage());
        }

        return result;
    }

    /**
     * Check if element is displayed
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID
     * @return Map with visibility status or error information
     */
    public Map<String, Object> isElementDisplayed(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            boolean displayed = element.isDisplayed();
            result.put("success", true);
            result.put("displayed", displayed);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error checking if element is displayed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Check if element is enabled
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID
     * @return Map with enabled status or error information
     */
    public Map<String, Object> isElementEnabled(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            boolean enabled = element.isEnabled();
            result.put("success", true);
            result.put("enabled", enabled);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error checking if element is enabled: " + e.getMessage());
        }

        return result;
    }
}

