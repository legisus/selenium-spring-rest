package com.core.controller;

import com.core.service.WebDriverWaitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for wait operations
 */
@RestController
@RequestMapping("/api/wait")
public class WaitController {

    @Autowired
    private WebDriverWaitService waitService;

    /**
     * Wait for an element using explicit wait
     *
     * @param sessionId The session ID
     * @param request Request containing wait parameters
     * @return Success or failure message
     */
    @PostMapping("/explicit/{sessionId}")
    public ResponseEntity<Map<String, Object>> explicitWait(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        String locatorType = (String) request.get("locatorType");
        String locatorValue = (String) request.get("locatorValue");
        String waitCondition = (String) request.get("waitCondition");

        if (locatorType == null || locatorValue == null || waitCondition == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Locator type, value and wait condition are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Integer timeoutSeconds = null;
        try {
            Object timeoutObj = request.get("timeout");
            if (timeoutObj != null) {
                if (timeoutObj instanceof Integer) {
                    timeoutSeconds = (Integer) timeoutObj;
                } else if (timeoutObj instanceof String) {
                    timeoutSeconds = Integer.parseInt((String) timeoutObj);
                } else if (timeoutObj instanceof Number) {
                    timeoutSeconds = ((Number) timeoutObj).intValue();
                }
            }
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid timeout value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result;
        if (timeoutSeconds != null) {
            result = waitService.explicitWait(sessionId, locatorType, locatorValue, waitCondition, timeoutSeconds);
        } else {
            result = waitService.explicitWait(sessionId, locatorType, locatorValue, waitCondition);
        }

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            HttpStatus status = result.containsKey("error") &&
                    result.get("error").toString().contains("Timeout") ?
                    HttpStatus.REQUEST_TIMEOUT : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(result);
        }
    }

    /**
     * Wait for a specific amount of time (static wait)
     *
     * @param sessionId The session ID
     * @param seconds Time to wait in seconds
     * @return Success or failure message
     */
    @GetMapping("/static/{sessionId}/{seconds}")
    public ResponseEntity<Map<String, Object>> staticWait(
            @PathVariable String sessionId,
            @PathVariable int seconds) {

        if (seconds <= 0 || seconds > 120) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Wait time must be between 1 and 120 seconds");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = waitService.staticWait(sessionId, seconds);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Wait for a JavaScript condition to be true
     *
     * @param sessionId The session ID
     * @param request Request containing JavaScript code and timeout
     * @return Success or failure message
     */
    @PostMapping("/javascript/{sessionId}")
    public ResponseEntity<Map<String, Object>> waitForJavaScriptCondition(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        String script = (String) request.get("script");

        if (script == null || script.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "JavaScript code is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Integer timeoutSeconds = 30; // Default timeout
        try {
            Object timeoutObj = request.get("timeout");
            if (timeoutObj != null) {
                if (timeoutObj instanceof Integer) {
                    timeoutSeconds = (Integer) timeoutObj;
                } else if (timeoutObj instanceof String) {
                    timeoutSeconds = Integer.parseInt((String) timeoutObj);
                } else if (timeoutObj instanceof Number) {
                    timeoutSeconds = ((Number) timeoutObj).intValue();
                }
            }
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid timeout value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = waitService.waitForJavaScriptCondition(sessionId, script, timeoutSeconds);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            HttpStatus status = result.containsKey("error") &&
                    result.get("error").toString().contains("Timeout") ?
                    HttpStatus.REQUEST_TIMEOUT : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(result);
        }
    }
}
