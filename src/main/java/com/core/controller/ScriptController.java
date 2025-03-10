package com.core.controller;

import com.core.service.WebDriverScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for JavaScript execution and screenshot operations
 */
@RestController
@RequestMapping("/api/script")
public class ScriptController {

    @Autowired
    private WebDriverScriptService scriptService;

    /**
     * Execute JavaScript in the browser
     *
     * @param sessionId The session ID
     * @param request Request containing script and arguments
     * @return Result of JavaScript execution
     */
    @PostMapping("/execute/{sessionId}")
    public ResponseEntity<Map<String, Object>> executeScript(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        String script = (String) request.get("script");
        if (script == null || script.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Script is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @SuppressWarnings("unchecked")
        List<Object> args = request.containsKey("args") ?
                (List<Object>) request.get("args") : null;

        Map<String, Object> result = scriptService.executeScript(sessionId, script, args);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Take a screenshot
     *
     * @param sessionId The session ID
     * @return Screenshot in Base64 format
     */
    @GetMapping("/screenshot/{sessionId}")
    public ResponseEntity<Map<String, Object>> takeScreenshot(@PathVariable String sessionId) {
        Map<String, Object> result = scriptService.takeScreenshot(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Take a screenshot of a specific element
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return Element screenshot in Base64 format
     */
    @GetMapping("/screenshot/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> takeElementScreenshot(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = scriptService.takeElementScreenshot(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}