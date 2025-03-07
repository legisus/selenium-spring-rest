package com.core.service;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for cookie management
 */
@Service
public class WebDriverCookieService {

    @Autowired
    private WebDriverSessionService sessionService;

    /**
     * Get all cookies from the current session
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with cookies or error information
     */
    public Map<String, Object> getCookies(String sessionId) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Set<Cookie> cookies = driver.manage().getCookies();
            List<Map<String, Object>> cookieList = new ArrayList<>();

            for (Cookie cookie : cookies) {
                Map<String, Object> cookieMap = new HashMap<>();
                cookieMap.put("name", cookie.getName());
                cookieMap.put("value", cookie.getValue());
                cookieMap.put("domain", cookie.getDomain());
                cookieMap.put("path", cookie.getPath());
                cookieMap.put("expiry", cookie.getExpiry());
                cookieMap.put("secure", cookie.isSecure());
                cookieMap.put("httpOnly", cookie.isHttpOnly());

                cookieList.add(cookieMap);
            }

            result.put("success", true);
            result.put("cookies", cookieList);
            result.put("count", cookieList.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting cookies: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get a specific cookie by name
     *
     * @param sessionId The session ID of the WebDriver
     * @param name The name of the cookie to get
     * @return Map with cookie information or error
     */
    public Map<String, Object> getCookie(String sessionId, String name) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Cookie cookie = driver.manage().getCookieNamed(name);

            if (cookie != null) {
                Map<String, Object> cookieMap = new HashMap<>();
                cookieMap.put("name", cookie.getName());
                cookieMap.put("value", cookie.getValue());
                cookieMap.put("domain", cookie.getDomain());
                cookieMap.put("path", cookie.getPath());
                cookieMap.put("expiry", cookie.getExpiry());
                cookieMap.put("secure", cookie.isSecure());
                cookieMap.put("httpOnly", cookie.isHttpOnly());

                result.put("success", true);
                result.put("cookie", cookieMap);
            } else {
                result.put("success", false);
                result.put("error", "Cookie not found: " + name);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting cookie: " + e.getMessage());
        }

        return result;
    }

    /**
     * Add a cookie to the current session
     *
     * @param sessionId The session ID of the WebDriver
     * @param name Cookie name
     * @param value Cookie value
     * @param domain Cookie domain (optional)
     * @param path Cookie path (optional)
     * @param expiry Cookie expiry date (optional)
     * @param secure Whether cookie is secure (optional)
     * @param httpOnly Whether cookie is HTTP only (optional)
     * @return Map with success or error information
     */
    public Map<String, Object> addCookie(String sessionId, String name, String value,
                                         String domain, String path, Date expiry,
                                         boolean secure, boolean httpOnly) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Cookie.Builder cookieBuilder = new Cookie.Builder(name, value);

            if (domain != null && !domain.isEmpty()) {
                cookieBuilder.domain(domain);
            }

            if (path != null && !path.isEmpty()) {
                cookieBuilder.path(path);
            }

            if (expiry != null) {
                cookieBuilder.expiresOn(expiry);
            }

            if (secure) {
                cookieBuilder.isSecure(true);
            }

            if (httpOnly) {
                cookieBuilder.isHttpOnly(true);
            }

            Cookie cookie = cookieBuilder.build();
            driver.manage().addCookie(cookie);

            result.put("success", true);
            result.put("message", "Cookie added successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error adding cookie: " + e.getMessage());
        }

        return result;
    }

    /**
     * Delete a specific cookie
     *
     * @param sessionId The session ID of the WebDriver
     * @param name Name of the cookie to delete
     * @return Map with success or error information
     */
    public Map<String, Object> deleteCookie(String sessionId, String name) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Cookie cookie = driver.manage().getCookieNamed(name);
            if (cookie == null) {
                result.put("success", false);
                result.put("error", "Cookie not found: " + name);
                return result;
            }

            driver.manage().deleteCookieNamed(name);
            result.put("success", true);
            result.put("message", "Cookie deleted successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error deleting cookie: " + e.getMessage());
        }

        return result;
    }

    /**
     * Delete all cookies
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> deleteAllCookies(String sessionId) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.manage().deleteAllCookies();
            result.put("success", true);
            result.put("message", "All cookies deleted successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error deleting cookies: " + e.getMessage());
        }

        return result;
    }
}
