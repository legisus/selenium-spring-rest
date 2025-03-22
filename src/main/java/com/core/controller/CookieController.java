package com.core.controller;

import com.core.service.WebDriverCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for cookie management
 */
@RestController
@RequestMapping("/api/cookie")
public class CookieController {

    @Autowired
    private WebDriverCookieService cookieService;

    /**
     * Get all cookies from the current session
     *
     * @param sessionId The session ID
     * @return Cookies or error information
     */
    @GetMapping("/all/{sessionId}")
    public ResponseEntity<Map<String, Object>> getCookies(@PathVariable String sessionId) {
        Map<String, Object> result = cookieService.getCookies(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Get a specific cookie by name
     *
     * @param sessionId The session ID
     * @param name The name of the cookie to get
     * @return Cookie information or error
     */
    @GetMapping("/{sessionId}/{name}")
    public ResponseEntity<Map<String, Object>> getCookie(
            @PathVariable String sessionId,
            @PathVariable String name) {

        Map<String, Object> result = cookieService.getCookie(sessionId, name);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            HttpStatus status = result.containsKey("error") &&
                    result.get("error").toString().contains("not found") ?
                    HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(result);
        }
    }

    /**
     * Add a cookie to the current session
     *
     * @param sessionId The session ID
     * @param request Request containing cookie information
     * @return Success or failure message
     */
    @PostMapping("/add/{sessionId}")
    public ResponseEntity<Map<String, Object>> addCookie(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        String name = (String) request.get("name");
        String value = (String) request.get("value");
        String domain = (String) request.get("domain");
        String path = (String) request.get("path");

        Boolean secure = request.containsKey("secure") ? (Boolean) request.get("secure") : false;
        Boolean httpOnly = request.containsKey("httpOnly") ? (Boolean) request.get("httpOnly") : false;

        if (name == null || value == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Cookie name and value are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Date expiry = null;
        if (request.containsKey("expiry")) {
            String expiryStr = (String) request.get("expiry");
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                expiry = format.parse(expiryStr);
            } catch (ParseException e) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Invalid expiry date format. Expected format: yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        Map<String, Object> result = cookieService.addCookie(
                sessionId, name, value, domain, path, expiry, secure, httpOnly);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Delete a specific cookie
     *
     * @param sessionId The session ID
     * @param name Name of the cookie to delete
     * @return Success or failure message
     */
    @DeleteMapping("/{sessionId}/{name}")
    public ResponseEntity<Map<String, Object>> deleteCookie(
            @PathVariable String sessionId,
            @PathVariable String name) {

        Map<String, Object> result = cookieService.deleteCookie(sessionId, name);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Delete all cookies
     *
     * @param sessionId The session ID
     * @return Success or failure message
     */
    @DeleteMapping("/all/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteAllCookies(@PathVariable String sessionId) {
        Map<String, Object> result = cookieService.deleteAllCookies(sessionId);

        if (result.containsKey("success") && (boolean)result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}