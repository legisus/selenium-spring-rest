package com.core.controller;

import com.core.service.WebDriverElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for element operations
 */
@RestController
@RequestMapping("/api/element")
public class ElementController {

    @Autowired
    private WebDriverElementService elementService;

    /**
     * Find an element using various locator strategies
     *
     * @param sessionId The session ID
     * @param request Request containing locator information
     * @return Information about the found element
     */
    @PostMapping("/find/{sessionId}")
    public ResponseEntity<Map<String, Object>> findElement(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String locatorType = request.get("locatorType");
        String locatorValue = request.get("locatorValue");

        if (locatorType == null || locatorValue == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Locator type and value are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = elementService.findElement(sessionId, locatorType, locatorValue);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Find multiple elements using various locator strategies
     *
     * @param sessionId The session ID
     * @param request Request containing locator information
     * @return Information about the found elements
     */
    @PostMapping("/findAll/{sessionId}")
    public ResponseEntity<Map<String, Object>> findElements(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String locatorType = request.get("locatorType");
        String locatorValue = request.get("locatorValue");

        if (locatorType == null || locatorValue == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Locator type and value are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = elementService.findElements(sessionId, locatorType, locatorValue);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Click on an element
     *
     * @param sessionId The session ID
     * @param elementId The element ID to click
     * @return Success or failure message
     */
    @GetMapping("/click/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> clickElement(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = elementService.clickElement(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Send keys to an element
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @param request Request containing text to send
     * @return Success or failure message
     */
    @PostMapping("/sendKeys/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> sendKeys(
            @PathVariable String sessionId,
            @PathVariable String elementId,
            @RequestBody Map<String, Object> request) {

        String text = (String) request.get("text");
        boolean clearFirst = request.containsKey("clearFirst") ?
                (boolean) request.get("clearFirst") : false;

        if (text == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Text is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = elementService.sendKeys(sessionId, elementId, text, clearFirst);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get element attribute
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @param attributeName The attribute name
     * @return The attribute value
     */
    @GetMapping("/attribute/{sessionId}/{elementId}/{attributeName}")
    public ResponseEntity<Map<String, Object>> getElementAttribute(
            @PathVariable String sessionId,
            @PathVariable String elementId,
            @PathVariable String attributeName) {

        Map<String, Object> result = elementService.getElementAttribute(sessionId, elementId, attributeName);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get element text
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return The element text
     */
    @GetMapping("/text/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> getElementText(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = elementService.getElementText(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Check if element is displayed
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return Whether the element is displayed
     */
    @GetMapping("/isDisplayed/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> isElementDisplayed(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = elementService.isElementDisplayed(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Check if element is enabled
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return Whether the element is enabled
     */
    @GetMapping("/isEnabled/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> isElementEnabled(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = elementService.isElementEnabled(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Check if element is selected
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return Whether the element is selected
     */
    @GetMapping("/isSelected/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> isElementSelected(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = elementService.isElementSelected(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
