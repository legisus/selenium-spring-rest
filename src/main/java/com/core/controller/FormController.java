package com.core.controller;

import com.core.service.WebDriverFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for form operations
 */
@RestController
@RequestMapping("/api/form")
public class FormController {

    @Autowired
    private WebDriverFormService formService;

    /**
     * Select an option from a dropdown by visible text
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @param request Request containing text to select
     * @return Success or failure message
     */
    @PostMapping("/select/text/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> selectByVisibleText(
            @PathVariable String sessionId,
            @PathVariable String elementId,
            @RequestBody Map<String, String> request) {

        String text = request.get("text");

        if (text == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Text is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = formService.selectByVisibleText(sessionId, elementId, text);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Select an option from a dropdown by value
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @param request Request containing value to select
     * @return Success or failure message
     */
    @PostMapping("/select/value/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> selectByValue(
            @PathVariable String sessionId,
            @PathVariable String elementId,
            @RequestBody Map<String, String> request) {

        String value = request.get("value");

        if (value == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Value is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = formService.selectByValue(sessionId, elementId, value);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Select an option from a dropdown by index
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @param request Request containing index to select
     * @return Success or failure message
     */
    @PostMapping("/select/index/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> selectByIndex(
            @PathVariable String sessionId,
            @PathVariable String elementId,
            @RequestBody Map<String, Object> request) {

        Integer index;
        try {
            Object indexObj = request.get("index");
            if (indexObj instanceof Integer) {
                index = (Integer) indexObj;
            } else if (indexObj instanceof String) {
                index = Integer.parseInt((String) indexObj);
            } else if (indexObj instanceof Number) {
                index = ((Number) indexObj).intValue();
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Index is required and must be a number");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid index value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> result = formService.selectByIndex(sessionId, elementId, index);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get all selected options from a dropdown
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return Selected options or error information
     */
    @GetMapping("/select/options/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> getSelectedOptions(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = formService.getSelectedOptions(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get all available options from a dropdown
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return All options or error information
     */
    @GetMapping("/select/allOptions/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> getAllOptions(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = formService.getAllOptions(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Deselect all options in a multi-select dropdown
     *
     * @param sessionId The session ID
     * @param elementId The element ID
     * @return Success or failure message
     */
    @GetMapping("/select/deselectAll/{sessionId}/{elementId}")
    public ResponseEntity<Map<String, Object>> deselectAll(
            @PathVariable String sessionId,
            @PathVariable String elementId) {

        Map<String, Object> result = formService.deselectAll(sessionId, elementId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
