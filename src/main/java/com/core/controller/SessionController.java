package com.core.controller;

import com.core.service.WebDriverSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for WebDriver session management
 */
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private WebDriverSessionService sessionService;

    /**
     * Initialize a new Chrome WebDriver with a visible window
     *
     * @return Session ID for the created WebDriver
     */
    @GetMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeDriver() {
        String sessionId = sessionService.initializeVisibleDriver();
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", "WebDriver initialized with visible window");
        return ResponseEntity.ok(response);
    }

    /**
     * Close a specific WebDriver session
     *
     * @param sessionId The session ID to close
     * @return Success or failure message
     */
    @GetMapping("/close/{sessionId}")
    public ResponseEntity<Map<String, Object>> closeDriver(@PathVariable String sessionId) {
        boolean closed = sessionService.closeDriver(sessionId);
        Map<String, Object> response = new HashMap<>();

        if (closed) {
            response.put("success", true);
            response.put("message", "WebDriver session closed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Session not found or already closed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * List all active WebDriver sessions
     *
     * @return Map of session IDs to their current URLs
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, String>> getActiveSessions() {
        Map<String, String> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Set implicit wait timeout for a WebDriver session
     *
     * @param sessionId The session ID
     * @param request Request containing timeout seconds
     * @return Success or failure message
     */
    @PostMapping("/implicitWait/{sessionId}")
    public ResponseEntity<Map<String, Object>> setImplicitWait(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        Integer timeoutSeconds;
        try {
            Object timeoutObj = request.get("timeout");
            if (timeoutObj instanceof Integer) {
                timeoutSeconds = (Integer) timeoutObj;
            } else if (timeoutObj instanceof String) {
                timeoutSeconds = Integer.parseInt((String) timeoutObj);
            } else if (timeoutObj instanceof Number) {
                timeoutSeconds = ((Number) timeoutObj).intValue();
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Timeout is required and must be a number");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid timeout value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean success = sessionService.setImplicitWait(sessionId, timeoutSeconds);
        Map<String, Object> response = new HashMap<>();

        if (success) {
            response.put("success", true);
            response.put("message", "Implicit wait set to " + timeoutSeconds + " seconds");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Session not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get the current implicit wait setting
     *
     * @param sessionId The session ID
     * @return Current implicit wait timeout
     */
    @GetMapping("/implicitWait/{sessionId}")
    public ResponseEntity<Map<String, Object>> getImplicitWait(@PathVariable String sessionId) {
        Integer timeout = sessionService.getImplicitWait(sessionId);
        Map<String, Object> response = new HashMap<>();

        if (timeout != null) {
            response.put("success", true);
            response.put("timeout", timeout);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Session not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}