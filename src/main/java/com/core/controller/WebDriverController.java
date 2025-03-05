package com.core.controller;

import com.core.service.WebDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webdriver")
public class WebDriverController {

    @Autowired
    private WebDriverService webDriverService;

    /**
     * Initialize a new Chrome WebDriver with a visible window
     *
     * @return Session ID for the created WebDriver
     */
    @GetMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeDriver() {
        String sessionId = webDriverService.initializeVisibleDriver();
        return ResponseEntity.ok(Map.of("sessionId", sessionId, "message", "WebDriver initialized with visible window"));
    }

    /**
     * Close a specific WebDriver session
     *
     * @param sessionId The session ID to close
     * @return Success or failure message
     */
    @GetMapping("/close/{sessionId}")
    public ResponseEntity<Map<String, String>> closeDriver(@PathVariable String sessionId) {
        boolean closed = webDriverService.closeDriver(sessionId);
        if (closed) {
            return ResponseEntity.ok(Map.of("message", "WebDriver session closed successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Session not found or already closed"));
        }
    }

    /**
     * Navigate to a specific URL
     *
     * @param sessionId The session ID to use
     * @param request Request body containing the URL
     * @return Success or failure message
     */
    @PostMapping("/navigate/{sessionId}")
    public ResponseEntity<Map<String, String>> navigateToUrl(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String url = request.get("url");
        if (url == null || url.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "URL is required"));
        }

        boolean navigated = webDriverService.navigateToUrl(sessionId, url);
        if (navigated) {
            return ResponseEntity.ok(Map.of(
                    "message", "Navigated to URL successfully",
                    "url", url
            ));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Session not found"));
        }
    }

    /**
     * Get current URL of a session
     *
     * @param sessionId The session ID to check
     * @return The current URL
     */
    @GetMapping("/url/{sessionId}")
    public ResponseEntity<Map<String, String>> getCurrentUrl(@PathVariable String sessionId) {
        String currentUrl = webDriverService.getCurrentUrl(sessionId);
        if (currentUrl != null) {
            return ResponseEntity.ok(Map.of("url", currentUrl));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Session not found"));
        }
    }

    /**
     * List all active WebDriver sessions
     *
     * @return Map of session IDs to their current URLs
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, String>> getActiveSessions() {
        Map<String, String> sessions = webDriverService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }
}
