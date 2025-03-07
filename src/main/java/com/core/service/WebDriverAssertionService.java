package com.core.service;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for assertions on elements and pages
 */
@Service
public class WebDriverAssertionService {

    @Autowired
    private ElementReferenceManager elementReferenceManager;

    @Autowired
    private WebDriverSessionService sessionService;

    /**
     * Perform assertions on element attributes, text or conditions
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID to assert on
     * @param assertType Type of assertion (equals, contains, etc.)
     * @param property Property to check (text, value, attribute, etc.)
     * @param expectedValue Expected value to compare against
     * @param attributeName Attribute name if property is 'attribute'
     * @return Map with assertion result
     */
    public Map<String, Object> assertElement(String sessionId, String elementId,
                                             String assertType, String property,
                                             String expectedValue, String attributeName) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            String actualValue;

            // Get the actual value based on the property
            switch (property.toLowerCase()) {
                case "text":
                    actualValue = element.getText();
                    break;
                case "value":
                    actualValue = element.getAttribute("value");
                    break;
                case "attribute":
                    if (attributeName == null || attributeName.isEmpty()) {
                        result.put("success", false);
                        result.put("error", "Attribute name is required for 'attribute' property");
                        return result;
                    }
                    actualValue = element.getAttribute(attributeName);
                    break;
                case "visible":
                case "displayed":
                    actualValue = String.valueOf(element.isDisplayed());
                    break;
                case "enabled":
                    actualValue = String.valueOf(element.isEnabled());
                    break;
                case "selected":
                    actualValue = String.valueOf(element.isSelected());
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid property: " + property);
                    return result;
            }

            // Perform the assertion
            boolean assertionResult = false;
            switch (assertType.toLowerCase()) {
                case "equals":
                    assertionResult = expectedValue.equals(actualValue);
                    break;
                case "notequals":
                    assertionResult = !expectedValue.equals(actualValue);
                    break;
                case "contains":
                    assertionResult = actualValue != null && actualValue.contains(expectedValue);
                    break;
                case "notcontains":
                    assertionResult = actualValue == null || !actualValue.contains(expectedValue);
                    break;
                case "startswith":
                    assertionResult = actualValue != null && actualValue.startsWith(expectedValue);
                    break;
                case "endswith":
                    assertionResult = actualValue != null && actualValue.endsWith(expectedValue);
                    break;
                case "matches":
                    assertionResult = actualValue != null && actualValue.matches(expectedValue);
                    break;
                case "empty":
                    assertionResult = actualValue == null || actualValue.isEmpty();
                    expectedValue = ""; // Set for consistency in response
                    break;
                case "notempty":
                    assertionResult = actualValue != null && !actualValue.isEmpty();
                    expectedValue = "not empty"; // Set for consistency in response
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid assertion type: " + assertType);
                    return result;
            }

            result.put("success", true);
            result.put("assertion", assertionResult);
            result.put("actualValue", actualValue);
            result.put("expectedValue", expectedValue);
            result.put("property", property);

            if ("attribute".equals(property.toLowerCase())) {
                result.put("attributeName", attributeName);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error performing assertion: " + e.getMessage());
        }

        return result;
    }

    /**
     * Assert that current URL matches a pattern
     *
     * @param sessionId The session ID of the WebDriver
     * @param assertType Type of assertion (equals, contains, etc.)
     * @param expectedUrl Expected URL or pattern
     * @return Map with assertion result
     */
    public Map<String, Object> assertUrl(String sessionId, String assertType, String expectedUrl) {
        Map<String, Object> result = new HashMap<>();

        if (!sessionService.sessionExists(sessionId)) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String currentUrl = sessionService.getDriver(sessionId).getCurrentUrl();

            // Perform the assertion
            boolean assertionResult = false;
            switch (assertType.toLowerCase()) {
                case "equals":
                    assertionResult = expectedUrl.equals(currentUrl);
                    break;
                case "notequals":
                    assertionResult = !expectedUrl.equals(currentUrl);
                    break;
                case "contains":
                    assertionResult = currentUrl.contains(expectedUrl);
                    break;
                case "notcontains":
                    assertionResult = !currentUrl.contains(expectedUrl);
                    break;
                case "startswith":
                    assertionResult = currentUrl.startsWith(expectedUrl);
                    break;
                case "endswith":
                    assertionResult = currentUrl.endsWith(expectedUrl);
                    break;
                case "matches":
                    assertionResult = currentUrl.matches(expectedUrl);
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid assertion type: " + assertType);
                    return result;
            }

            result.put("success", true);
            result.put("assertion", assertionResult);
            result.put("currentUrl", currentUrl);
            result.put("expectedUrl", expectedUrl);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error asserting URL: " + e.getMessage());
        }

        return result;
    }

    /**
     * Assert that page title matches a pattern
     *
     * @param sessionId The session ID of the WebDriver
     * @param assertType Type of assertion (equals, contains, etc.)
     * @param expectedTitle Expected title or pattern
     * @return Map with assertion result
     */
    public Map<String, Object> assertTitle(String sessionId, String assertType, String expectedTitle) {
        Map<String, Object> result = new HashMap<>();

        if (!sessionService.sessionExists(sessionId)) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String currentTitle = sessionService.getDriver(sessionId).getTitle();

            // Perform the assertion
            boolean assertionResult = false;
            switch (assertType.toLowerCase()) {
                case "equals":
                    assertionResult = expectedTitle.equals(currentTitle);
                    break;
                case "notequals":
                    assertionResult = !expectedTitle.equals(currentTitle);
                    break;
                case "contains":
                    assertionResult = currentTitle.contains(expectedTitle);
                    break;
                case "notcontains":
                    assertionResult = !currentTitle.contains(expectedTitle);
                    break;
                case "startswith":
                    assertionResult = currentTitle.startsWith(expectedTitle);
                    break;
                case "endswith":
                    assertionResult = currentTitle.endsWith(expectedTitle);
                    break;
                case "matches":
                    assertionResult = currentTitle.matches(expectedTitle);
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid assertion type: " + assertType);
                    return result;
            }

            result.put("success", true);
            result.put("assertion", assertionResult);
            result.put("currentTitle", currentTitle);
            result.put("expectedTitle", expectedTitle);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error asserting title: " + e.getMessage());
        }

        return result;
    }
}
