package com.core.controller;

import com.core.service.WebDriverAssertionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for assertions on elements and pages
 */
@RestController
@RequestMapping("/api/assert")
public class AssertionController {

    @Autowired
    private WebDriverAssertionService assertionService;

    /**
     * Perform assertions on element attributes, text or conditions
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @param request Request containing assertion parameters
     * @return Assertion result
     */
    @PostMapping("/element/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> assertElement(
            @PathVariable String sessionId,
            @PathVariable String elementId,
            @RequestBody Map<String, String> request) {

        String assertType = request.get("assertType");
        String property = request.get("property");
        String expectedValue = request.get("expectedValue");
        String attributeName = request.get("attributeName");

        if (assertType == null || property == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Assert type and property are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // expectedValue can be null for some assertion types like "empty"
        if (expectedValue == null &&
                !assertType.equalsIgnoreCase("empty") &&
                !assertType.equalsIgnoreCase("notempty")) {

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Expected value is required for assertion type: " + assertType);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // attributeName is required when property is "attribute"
        if ("attribute".equalsIgnoreCase(property) && (attributeName == null || attributeName.isEmpty())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Attribute name is required when property is 'attribute'");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = assertionService.assertElement(
                sessionId, elementId, assertType, property, expectedValue, attributeName);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Assert that current URL matches a pattern
     *
     * @param sessionId The session ID
     * @param request Request containing assertion parameters
     * @return Assertion result
     */
    @PostMapping("/url/{sessionId}")
    public ResponseEntity<Map<String, Object>> assertUrl(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String assertType = request.get("assertType");
        String expectedUrl = request.get("expectedUrl");

        if (assertType == null || expectedUrl == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Assert type and expected URL are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = assertionService.assertUrl(sessionId, assertType, expectedUrl);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Assert that page title matches a pattern
     *
     * @param sessionId The session ID
     * @param request Request containing assertion parameters
     * @return Assertion result
     */
    @PostMapping("/title/{sessionId}")
    public ResponseEntity<Map<String, Object>> assertTitle(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String assertType = request.get("assertType");
        String expectedTitle = request.get("expectedTitle");

        if (assertType == null || expectedTitle == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Assert type and expected title are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = assertionService.assertTitle(sessionId, assertType, expectedTitle);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
