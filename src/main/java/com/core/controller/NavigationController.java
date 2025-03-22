package com.core.controller;

import com.core.service.WebDriverNavigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for browser navigation operations
 */
@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    @Autowired
    private WebDriverNavigationService navigationService;

    /**
     * Navigate to a specific URL
     *
     * @param sessionId The session ID
     * @param request Request body containing URL and optional timeout
     * @return Success or failure message
     */
    @PostMapping("/to/{sessionId}")
    public ResponseEntity<Map<String, Object>> navigateToUrl(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        String url = (String) request.get("url");
        if (url == null || url.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "URL is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Extract timeout if provided, otherwise use default
        Integer timeoutSeconds = null;
        try {
            if (request.containsKey("timeout")) {
                Object timeoutObj = request.get("timeout");
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
            result = navigationService.navigateToUrl(sessionId, url, timeoutSeconds);
        } else {
            result = navigationService.navigateToUrl(sessionId, url);
        }

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get current URL
     *
     * @param sessionId The session ID
     * @return The current URL
     */
    @GetMapping("/url/{sessionId}")
    public ResponseEntity<Map<String, Object>> getCurrentUrl(@PathVariable String sessionId) {
        Map<String, Object> result = navigationService.getCurrentUrl(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get page title
     *
     * @param sessionId The session ID
     * @return The page title
     */
    @GetMapping("/title/{sessionId}")
    public ResponseEntity<Map<String, Object>> getTitle(@PathVariable String sessionId) {
        Map<String, Object> result = navigationService.getTitle(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get page source
     *
     * @param sessionId The session ID
     * @return The page source
     */
    @GetMapping("/source/{sessionId}")
    public ResponseEntity<Map<String, Object>> getPageSource(@PathVariable String sessionId) {
        Map<String, Object> result = navigationService.getPageSource(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Refresh the current page
     *
     * @param sessionId The session ID
     * @return Success or failure message
     */
    @GetMapping("/refresh/{sessionId}")
    public ResponseEntity<Map<String, Object>> refreshPage(@PathVariable String sessionId) {
        Map<String, Object> result = navigationService.refreshPage(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Navigate back in browser history
     *
     * @param sessionId The session ID
     * @return Success or failure message
     */
    @GetMapping("/back/{sessionId}")
    public ResponseEntity<Map<String, Object>> navigateBack(@PathVariable String sessionId) {
        Map<String, Object> result = navigationService.navigateBack(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Navigate forward in browser history
     *
     * @param sessionId The session ID
     * @return Success or failure message
     */
    @GetMapping("/forward/{sessionId}")
    public ResponseEntity<Map<String, Object>> navigateForward(@PathVariable String sessionId) {
        Map<String, Object> result = navigationService.navigateForward(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
