package com.core.service;

import com.core.config.SeleniumProperties;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class WebDriverServiceExpanded {

    // Instead of autowiring ChromeOptions directly, we'll use the configuration properties
    @Autowired
    private SeleniumProperties seleniumProperties;

    // Store active WebDriver instances
    private final Map<String, WebDriver> activeDrivers = new HashMap<>();

    // Store implicit wait settings for each driver
    private final Map<String, Integer> implicitWaitSettings = new HashMap<>();

    /**
     * Initialize a new Chrome WebDriver instance with visible UI
     *
     * @return The session ID for the created WebDriver
     */
    public String initializeVisibleDriver() {
        // Create new Chrome options for a visible browser window
        ChromeOptions visibleOptions = new ChromeOptions();

        // Add default arguments for stability but ensure not headless
        visibleOptions.addArguments("--disable-gpu");
        visibleOptions.addArguments("--no-sandbox");
        visibleOptions.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(visibleOptions);
        String sessionId = UUID.randomUUID().toString();
        activeDrivers.put(sessionId, driver);

        // Set default implicit wait to 0 (no implicit wait)
        implicitWaitSettings.put(sessionId, 0);

        return sessionId;
    }

    /**
     * Navigate to a specific URL and wait for page to load completely
     *
     * @param sessionId      The session ID of the WebDriver
     * @param url            The URL to navigate to
     * @param timeoutSeconds Maximum time to wait for page load in seconds
     * @return true if navigation was successful, false otherwise
     */
    public boolean navigateToUrl(String sessionId, String url, int timeoutSeconds) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            try {
                // Set page load timeout
                driver.manage().timeouts().pageLoadTimeout(timeoutSeconds, TimeUnit.SECONDS);

                // Navigate to URL - this will block until page load complete or timeout
                driver.get(url);

                // Additional wait for any JavaScript to finish loading
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

                // Wait for document.readyState to be 'complete'
                wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));

                return true;
            } catch (Exception e) {
                // Log exception but don't fail - page might be partially loaded
                System.err.println("Error waiting for page load: " + e.getMessage());
                return true; // Still return true as navigation occurred
            }
        }
        return false;
    }

    /**
     * Navigate to a specific URL with default timeout
     *
     * @param sessionId The session ID of the WebDriver
     * @param url       The URL to navigate to
     * @return true if navigation was successful, false otherwise
     */
    public boolean navigateToUrl(String sessionId, String url) {
        // Default timeout of 30 seconds
        return navigateToUrl(sessionId, url, 30);
    }

    /**
     * Close a specific WebDriver instance
     *
     * @param sessionId The session ID of the WebDriver to close
     * @return true if the driver was closed successfully, false otherwise
     */
    public boolean closeDriver(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            try {
                driver.quit();
                activeDrivers.remove(sessionId);
                implicitWaitSettings.remove(sessionId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Get the current URL of a WebDriver session
     *
     * @param sessionId The session ID of the WebDriver
     * @return The current URL or null if the session doesn't exist
     */
    public String getCurrentUrl(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            return driver.getCurrentUrl();
        }
        return null;
    }

    /**
     * Get all active session IDs
     *
     * @return A map of session IDs to their current URLs
     */
    public Map<String, String> getActiveSessions() {
        Map<String, String> sessions = new HashMap<>();
        for (Map.Entry<String, WebDriver> entry : activeDrivers.entrySet()) {
            try {
                sessions.put(entry.getKey(), entry.getValue().getCurrentUrl());
            } catch (Exception e) {
                sessions.put(entry.getKey(), "Error: " + e.getMessage());
            }
        }
        return sessions;
    }

    /**
     * Set implicit wait timeout for a specific driver
     *
     * @param sessionId      The session ID of the WebDriver
     * @param timeoutSeconds Timeout in seconds
     * @return True if successful, false if session not found
     */
    public boolean setImplicitWait(String sessionId, int timeoutSeconds) {
        WebDriver driver = activeDrivers.get(sessionId);
        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(timeoutSeconds, TimeUnit.SECONDS);
            implicitWaitSettings.put(sessionId, timeoutSeconds);
            return true;
        }
        return false;
    }

    /**
     * Get the current implicit wait setting for a driver
     *
     * @param sessionId The session ID of the WebDriver
     * @return Current implicit wait in seconds, or null if session not found
     */
    public Integer getImplicitWait(String sessionId) {
        return implicitWaitSettings.get(sessionId);
    }

    /**
     * Find an element using various locator strategies
     *
     * @param sessionId    The session ID of the WebDriver
     * @param locatorType  Type of locator (xpath, id, css, etc.)
     * @param locatorValue Value of the locator
     * @return Map with element details or error information
     */
    public Map<String, Object> findElement(String sessionId, String locatorType, String locatorValue) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("error", "Session not found");
            return result;
        }

        By locator;
        try {
            locator = createLocator(locatorType, locatorValue);
        } catch (IllegalArgumentException e) {
            result.put("error", "Invalid locator type: " + locatorType);
            return result;
        }

        try {
            WebElement element = driver.findElement(locator);
            result.put("found", true);
            result.put("visible", element.isDisplayed());
            result.put("enabled", element.isEnabled());
            result.put("tagName", element.getTagName());

            // Store element ID for future reference
            String elementId = UUID.randomUUID().toString();
            storeElementReference(sessionId, elementId, element);
            result.put("elementId", elementId);

            try {
                result.put("text", element.getText());
            } catch (Exception e) {
                result.put("text", "");
            }

            try {
                result.put("value", element.getAttribute("value"));
            } catch (Exception e) {
                result.put("value", null);
            }

        } catch (NoSuchElementException e) {
            result.put("found", false);
            result.put("error", "Element not found: " + e.getMessage());
        } catch (Exception e) {
            result.put("error", "Error finding element: " + e.getMessage());
        }

        return result;
    }

    // Store elements for reuse
    private final Map<String, Map<String, WebElement>> sessionElements = new HashMap<>();

    private void storeElementReference(String sessionId, String elementId, WebElement element) {
        sessionElements.computeIfAbsent(sessionId, k -> new HashMap<>()).put(elementId, element);
    }

    private WebElement getStoredElement(String sessionId, String elementId) {
        Map<String, WebElement> elements = sessionElements.get(sessionId);
        if (elements == null) {
            return null;
        }
        return elements.get(elementId);
    }

    /**
     * Find multiple elements using various locator strategies
     *
     * @param sessionId    The session ID of the WebDriver
     * @param locatorType  Type of locator (xpath, id, css, etc.)
     * @param locatorValue Value of the locator
     * @return List of maps with element details
     */
    public Map<String, Object> findElements(String sessionId, String locatorType, String locatorValue) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("error", "Session not found");
            return result;
        }

        By locator;
        try {
            locator = createLocator(locatorType, locatorValue);
        } catch (IllegalArgumentException e) {
            result.put("error", "Invalid locator type: " + locatorType);
            return result;
        }

        try {
            List<WebElement> elements = driver.findElements(locator);
            List<Map<String, Object>> elementDetails = new ArrayList<>();

            for (WebElement element : elements) {
                Map<String, Object> details = new HashMap<>();
                details.put("visible", element.isDisplayed());
                details.put("enabled", element.isEnabled());
                details.put("tagName", element.getTagName());

                // Store element ID for future reference
                String elementId = UUID.randomUUID().toString();
                storeElementReference(sessionId, elementId, element);
                details.put("elementId", elementId);

                try {
                    details.put("text", element.getText());
                } catch (Exception e) {
                    details.put("text", "");
                }

                try {
                    details.put("value", element.getAttribute("value"));
                } catch (Exception e) {
                    details.put("value", null);
                }

                elementDetails.add(details);
            }

            result.put("count", elements.size());
            result.put("elements", elementDetails);

        } catch (Exception e) {
            result.put("error", "Error finding elements: " + e.getMessage());
        }

        return result;
    }

    /**
     * Click on an element
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID to click
     * @return Map with success or error information
     */
    public Map<String, Object> clickElement(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            element.click();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error clicking element: " + e.getMessage());
        }

        return result;
    }

    /**
     * Send keys to an element
     *
     * @param sessionId  The session ID of the WebDriver
     * @param elementId  The element ID to send keys to
     * @param text       The text to send
     * @param clearFirst Whether to clear the field first
     * @return Map with success or error information
     */
    public Map<String, Object> sendKeys(String sessionId, String elementId, String text, boolean clearFirst) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            if (clearFirst) {
                element.clear();
            }
            element.sendKeys(text);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error sending keys: " + e.getMessage());
        }

        return result;
    }

    /**
     * Wait for an element using explicit wait
     *
     * @param sessionId      The session ID of the WebDriver
     * @param locatorType    Type of locator (xpath, id, css, etc.)
     * @param locatorValue   Value of the locator
     * @param waitCondition  Condition to wait for (visible, clickable, present, invisible)
     * @param timeoutSeconds Maximum wait time in seconds
     * @return Map with result of wait operation
     */
    public Map<String, Object> explicitWait(String sessionId, String locatorType, String locatorValue,
                                            String waitCondition, int timeoutSeconds) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        By locator;
        try {
            locator = createLocator(locatorType, locatorValue);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("error", "Invalid locator type: " + locatorType);
            return result;
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            WebElement element = null;
            boolean success = false;

            switch (waitCondition.toLowerCase()) {
                case "visible":
                    element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    success = true;
                    break;
                case "clickable":
                    element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                    success = true;
                    break;
                case "present":
                    element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                    success = true;
                    break;
                case "invisible":
                    success = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid wait condition: " + waitCondition);
                    return result;
            }

            result.put("success", success);

            if (element != null) {
                String elementId = UUID.randomUUID().toString();
                storeElementReference(sessionId, elementId, element);
                result.put("elementId", elementId);
            }

        } catch (TimeoutException e) {
            result.put("success", false);
            result.put("error", "Timeout waiting for condition: " + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error during wait: " + e.getMessage());
        }

        return result;
    }

    /**
     * Execute JavaScript in the browser
     *
     * @param sessionId The session ID of the WebDriver
     * @param script    JavaScript code to execute
     * @param args      Arguments to pass to the script
     * @return Map with result of JavaScript execution
     */
    public Map<String, Object> executeScript(String sessionId, String script, List<Object> args) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object scriptResult = js.executeScript(script, args == null ? new Object[0] : args.toArray());

            result.put("success", true);

            // Convert script result to a manageable form
            if (scriptResult instanceof WebElement) {
                WebElement element = (WebElement) scriptResult;
                String elementId = UUID.randomUUID().toString();
                storeElementReference(sessionId, elementId, element);

                Map<String, Object> elementInfo = new HashMap<>();
                elementInfo.put("elementId", elementId);
                elementInfo.put("tagName", element.getTagName());
                try {
                    elementInfo.put("text", element.getText());
                } catch (Exception e) {
                    elementInfo.put("text", "");
                }

                result.put("result", elementInfo);
            } else if (scriptResult instanceof List) {
                List<?> list = (List<?>) scriptResult;
                List<Object> convertedList = new ArrayList<>();

                for (Object item : list) {
                    if (item instanceof WebElement) {
                        WebElement element = (WebElement) item;
                        String elementId = UUID.randomUUID().toString();
                        storeElementReference(sessionId, elementId, element);

                        Map<String, Object> elementInfo = new HashMap<>();
                        elementInfo.put("elementId", elementId);
                        elementInfo.put("tagName", element.getTagName());
                        try {
                            elementInfo.put("text", element.getText());
                        } catch (Exception e) {
                            elementInfo.put("text", "");
                        }

                        convertedList.add(elementInfo);
                    } else {
                        convertedList.add(item);
                    }
                }

                result.put("result", convertedList);
            } else {
                result.put("result", scriptResult);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error executing script: " + e.getMessage());
        }

        return result;
    }

    /**
     * Take a screenshot
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with screenshot data in Base64 or error information
     */
    public Map<String, Object> takeScreenshot(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            String screenshot = screenshotDriver.getScreenshotAs(OutputType.BASE64);

            result.put("success", true);
            result.put("screenshot", screenshot);
            result.put("format", "base64");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error taking screenshot: " + e.getMessage());
        }

        return result;
    }

    /**
     * Create a By locator based on locator type and value
     *
     * @param locatorType  Type of locator
     * @param locatorValue Value of locator
     * @return By locator
     */
    private By createLocator(String locatorType, String locatorValue) {
        switch (locatorType.toLowerCase()) {
            case "id":
                return By.id(locatorValue);
            case "name":
                return By.name(locatorValue);
            case "classname":
            case "class":
                return By.className(locatorValue);
            case "tagname":
            case "tag":
                return By.tagName(locatorValue);
            case "linktext":
            case "link":
                return By.linkText(locatorValue);
            case "partiallinktext":
            case "partiallink":
                return By.partialLinkText(locatorValue);
            case "cssselector":
            case "css":
                return By.cssSelector(locatorValue);
            case "xpath":
                return By.xpath(locatorValue);
            default:
                throw new IllegalArgumentException("Invalid locator type: " + locatorType);
        }
    }

    /**
     * Perform assertions on element attributes, text or conditions
     *
     * @param sessionId     The session ID of the WebDriver
     * @param elementId     The element ID to assert on
     * @param assertType    Type of assertion (equals, contains, etc.)
     * @param property      Property to check (text, value, attribute, etc.)
     * @param expectedValue Expected value to compare against
     * @param attributeName Attribute name if property is 'attribute'
     * @return Map with assertion result
     */
    public Map<String, Object> assertElement(String sessionId, String elementId,
                                             String assertType, String property,
                                             String expectedValue, String attributeName) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            String actualValue;

            // Get the actual value based on the property
            switch (property.toLowerCase()) {
                case "text":
                    actualValue = element.getText();
                    break;
                case "value":
                    actualValue = element.getAttribute("value");
                    break;
                case "attribute":
                    if (attributeName == null || attributeName.isEmpty()) {
                        result.put("success", false);
                        result.put("error", "Attribute name is required for 'attribute' property");
                        return result;
                    }
                    actualValue = element.getAttribute(attributeName);
                    break;
                case "visible":
                    actualValue = String.valueOf(element.isDisplayed());
                    break;
                case "enabled":
                    actualValue = String.valueOf(element.isEnabled());
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid property: " + property);
                    return result;
            }

            // Perform the assertion
            boolean assertionResult = false;
            switch (assertType.toLowerCase()) {
                case "equals":
                    assertionResult = expectedValue.equals(actualValue);
                    break;
                case "contains":
                    assertionResult = actualValue != null && actualValue.contains(expectedValue);
                    break;
                case "startswith":
                    assertionResult = actualValue != null && actualValue.startsWith(expectedValue);
                    break;
                case "endswith":
                    assertionResult = actualValue != null && actualValue.endsWith(expectedValue);
                    break;
                case "matches":
                    assertionResult = actualValue != null && actualValue.matches(expectedValue);
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Invalid assertion type: " + assertType);
                    return result;
            }

            result.put("success", true);
            result.put("assertion", assertionResult);
            result.put("actualValue", actualValue);
            result.put("expectedValue", expectedValue);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error performing assertion: " + e.getMessage());
        }

        return result;
    }

    /**
     * Switch to an iframe
     *
     * @param sessionId    The session ID of the WebDriver
     * @param frameLocator Type of locator for the frame
     * @param frameValue   Value of the locator
     * @return Map with success or error information
     */
    public Map<String, Object> switchToFrame(String sessionId, String frameLocator, String frameValue) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            if ("index".equalsIgnoreCase(frameLocator)) {
                // Switch by index
                int index = Integer.parseInt(frameValue);
                driver.switchTo().frame(index);
            } else if ("name".equalsIgnoreCase(frameLocator) || "id".equalsIgnoreCase(frameLocator)) {
                // Switch by name or id
                driver.switchTo().frame(frameValue);
            } else {
                // Switch by locator (element)
                By locator = createLocator(frameLocator, frameValue);
                WebElement frameElement = driver.findElement(locator);
                driver.switchTo().frame(frameElement);
            }

            result.put("success", true);
        } catch (NoSuchFrameException e) {
            result.put("success", false);
            result.put("error", "Frame not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("error", "Invalid frame index: " + frameValue);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error switching to frame: " + e.getMessage());
        }

        return result;
    }

    /**
     * Switch back to default content (out of all frames)
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> switchToDefaultContent(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.switchTo().defaultContent();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error switching to default content: " + e.getMessage());
        }

        return result;
    }

    /**
     * Accept or dismiss an alert
     *
     * @param sessionId The session ID of the WebDriver
     * @param accept    Whether to accept (true) or dismiss (false) the alert
     * @return Map with alert text and success or error information
     */
    public Map<String, Object> handleAlert(String sessionId, boolean accept) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            if (accept) {
                alert.accept();
            } else {
                alert.dismiss();
            }

            result.put("success", true);
            result.put("alertText", alertText);
        } catch (NoAlertPresentException e) {
            result.put("success", false);
            result.put("error", "No alert present");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error handling alert: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get all cookies from the current session
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with cookies or error information
     */
    public Map<String, Object> getCookies(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
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
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting cookies: " + e.getMessage());
        }

        return result;
    }

    /**
     * Add a cookie to the current session
     *
     * @param sessionId The session ID of the WebDriver
     * @param name      Cookie name
     * @param value     Cookie value
     * @param domain    Cookie domain
     * @param path      Cookie path
     * @param expiry    Cookie expiry date
     * @param secure    Whether cookie is secure
     * @param httpOnly  Whether cookie is HTTP only
     * @return Map with success or error information
     */
    public Map<String, Object> addCookie(String sessionId, String name, String value,
                                         String domain, String path, Date expiry,
                                         boolean secure, boolean httpOnly) {
        WebDriver driver = activeDrivers.get(sessionId);
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
     * @param name      Name of the cookie to delete
     * @return Map with success or error information
     */
    public Map<String, Object> deleteCookie(String sessionId, String name) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.manage().deleteCookieNamed(name);
            result.put("success", true);
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
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.manage().deleteAllCookies();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error deleting cookies: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get page title
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with page title or error information
     */
    public Map<String, Object> getTitle(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String title = driver.getTitle();
            result.put("success", true);
            result.put("title", title);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting title: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get page source
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with page source or error information
     */
    public Map<String, Object> getPageSource(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            String pageSource = driver.getPageSource();
            result.put("success", true);
            result.put("pageSource", pageSource);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting page source: " + e.getMessage());
        }

        return result;
    }

    /**
     * Refresh the current page
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> refreshPage(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.navigate().refresh();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error refreshing page: " + e.getMessage());
        }

        return result;
    }

    /**
     * Navigate back in browser history
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> navigateBack(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.navigate().back();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error navigating back: " + e.getMessage());
        }

        return result;
    }

    /**
     * Navigate forward in browser history
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with success or error information
     */
    public Map<String, Object> navigateForward(String sessionId) {
        WebDriver driver = activeDrivers.get(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            driver.navigate().forward();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error navigating forward: " + e.getMessage());
        }

        return result;
    }

    /**
     * Select an option from a dropdown by visible text
     *
     * @param sessionId   The session ID of the WebDriver
     * @param elementId   The element ID of the select element
     * @param visibleText The visible text to select
     * @return Map with success or error information
     */
    public Map<String, Object> selectByVisibleText(String sessionId, String elementId, String visibleText) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
            select.selectByVisibleText(visibleText);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error selecting by visible text: " + e.getMessage());
        }

        return result;
    }

    /**
     * Select an option from a dropdown by value
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID of the select element
     * @param value     The value to select
     * @return Map with success or error information
     */
    public Map<String, Object> selectByValue(String sessionId, String elementId, String value) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
            select.selectByValue(value);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error selecting by value: " + e.getMessage());
        }

        return result;
    }

    /**
     * Select an option from a dropdown by index
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID of the select element
     * @param index     The index to select
     * @return Map with success or error information
     */
    public Map<String, Object> selectByIndex(String sessionId, String elementId, int index) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
            select.selectByIndex(index);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error selecting by index: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get all selected options from a dropdown
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID of the select element
     * @return Map with selected options or error information
     */
    public Map<String, Object> getSelectedOptions(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = getStoredElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
            List<WebElement> selectedOptions = select.getAllSelectedOptions();

            List<Map<String, String>> optionsList = new ArrayList<>();
            for (WebElement option : selectedOptions) {
                Map<String, String> optionMap = new HashMap<>();
                optionMap.put("text", option.getText());
                optionMap.put("value", option.getAttribute("value"));
                optionsList.add(optionMap);
            }

            result.put("success", true);
            result.put("selectedOptions", optionsList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting selected options: " + e.getMessage());
        }

        return result;
    }
}
