package com.core.controller;

import com.core.service.WebDriverFrameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for frame and alert handling
 */
@RestController
@RequestMapping("/api/frame")
public class FrameController {

    @Autowired
    private WebDriverFrameService frameService;

    /**
     * Switch to an iframe
     *
     * @param sessionId The session ID
     * @param request Request containing frame locator information
     * @return Success or failure message
     */
    @PostMapping("/switchTo/{sessionId}")
    public ResponseEntity<Map<String, Object>> switchToFrame(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String frameLocator = request.get("frameLocator");
        String frameValue = request.get("frameValue");

        if (frameLocator == null || frameValue == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Frame locator and value are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = frameService.switchToFrame(sessionId, frameLocator, frameValue);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Switch back to default content (out of all frames)
     *
     * @param sessionId The session ID
     * @return Success or failure message
     */
    @GetMapping("/switchToDefault/{sessionId}")
    public ResponseEntity<Map<String, Object>> switchToDefaultContent(@PathVariable String sessionId) {
        Map<String, Object> result = frameService.switchToDefaultContent(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Switch to parent frame
     *
     * @param sessionId The session ID
     * @return Success or failure message
     */
    @GetMapping("/switchToParent/{sessionId}")
    public ResponseEntity<Map<String, Object>> switchToParentFrame(@PathVariable String sessionId) {
        Map<String, Object> result = frameService.switchToParentFrame(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Accept or dismiss an alert
     *
     * @param sessionId The session ID
     * @param request Request containing action (accept/dismiss)
     * @return Alert text and success or failure message
     */
    @PostMapping("/alert/handle/{sessionId}")
    public ResponseEntity<Map<String, Object>> handleAlert(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        Boolean accept = request.containsKey("accept") ? (Boolean) request.get("accept") : true;

        Map<String, Object> result = frameService.handleAlert(sessionId, accept);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            HttpStatus status = result.containsKey("error") &&
                    result.get("error").toString().contains("No alert present") ?
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(result);
        }
    }

    /**
     * Send text to an alert prompt
     *
     * @param sessionId The session ID
     * @param request Request containing text and action
     * @return Alert text and success or failure message
     */
    @PostMapping("/alert/sendText/{sessionId}")
    public ResponseEntity<Map<String, Object>> sendTextToAlert(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        String text = (String) request.get("text");
        Boolean accept = request.containsKey("accept") ? (Boolean) request.get("accept") : true;

        if (text == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Text is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = frameService.sendTextToAlert(sessionId, text, accept);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            HttpStatus status = result.containsKey("error") &&
                    result.get("error").toString().contains("No alert present") ?
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(result);
        }
    }

    /**
     * Get alert text without accepting or dismissing
     *
     * @param sessionId The session ID
     * @return Alert text or error information
     */
    @GetMapping("/alert/getText/{sessionId}")
    public ResponseEntity<Map<String, Object>> getAlertText(@PathVariable String sessionId) {
        Map<String, Object> result = frameService.getAlertText(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            HttpStatus status = result.containsKey("error") &&
                    result.get("error").toString().contains("No alert present") ?
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(result);
        }
    }
}
