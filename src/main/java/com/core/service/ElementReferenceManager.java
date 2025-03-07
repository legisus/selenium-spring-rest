package com.core.service;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to manage WebElement references across sessions.
 * This class allows storing WebElements by session and retrieving them later
 * using element IDs, which helps with stateless REST API operations.
 */
@Component
public class ElementReferenceManager {

    // Stores elements for each session: sessionId -> (elementId -> WebElement)
    private final Map<String, Map<String, WebElement>> sessionElements = new HashMap<>();

    /**
     * Store a WebElement reference and generate a unique element ID
     *
     * @param sessionId The WebDriver session ID
     * @param element The WebElement to store
     * @return A unique element ID
     */
    public String storeElement(String sessionId, WebElement element) {
        String elementId = UUID.randomUUID().toString();
        storeElement(sessionId, elementId, element);
        return elementId;
    }

    /**
     * Store a WebElement reference with a given element ID
     *
     * @param sessionId The WebDriver session ID
     * @param elementId The element ID to use
     * @param element The WebElement to store
     */
    public void storeElement(String sessionId, String elementId, WebElement element) {
        sessionElements.computeIfAbsent(sessionId, k -> new HashMap<>()).put(elementId, element);
    }

    /**
     * Retrieve a stored WebElement
     *
     * @param sessionId The WebDriver session ID
     * @param elementId The element ID
     * @return The stored WebElement, or null if not found
     */
    public WebElement getElement(String sessionId, String elementId) {
        Map<String, WebElement> elements = sessionElements.get(sessionId);
        if (elements == null) {
            return null;
        }
        return elements.get(elementId);
    }

    /**
     * Clear all stored elements for a session
     *
     * @param sessionId The WebDriver session ID
     */
    public void clearSession(String sessionId) {
        sessionElements.remove(sessionId);
    }

    /**
     * Check if an element exists
     *
     * @param sessionId The WebDriver session ID
     * @param elementId The element ID
     * @return true if the element exists, false otherwise
     */
    public boolean elementExists(String sessionId, String elementId) {
        Map<String, WebElement> elements = sessionElements.get(sessionId);
        if (elements == null) {
            return false;
        }
        return elements.containsKey(elementId);
    }
}
