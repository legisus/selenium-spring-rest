package com.core.service;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for JavaScript execution and screenshot operations
 */
@Service
public class WebDriverScriptService {

    @Autowired
    private WebDriverSessionService sessionService;

    @Autowired
    private ElementReferenceManager elementReferenceManager;

    /**
     * Execute JavaScript in the browser
     *
     * @param sessionId The session ID of the WebDriver
     * @param script JavaScript code to execute
     * @param args Arguments to pass to the script
     * @return Map with result of JavaScript execution
     */
    public Map<String, Object> executeScript(String sessionId, String script, List<Object> args) {
        WebDriver driver = sessionService.getDriver(sessionId);
        Map<String, Object> result = new HashMap<>();

        if (driver == null) {
            result.put("success", false);
            result.put("error", "Session not found");
            return result;
        }

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Process arguments to handle element references
            List<Object> processedArgs = new ArrayList<>();
            if (args != null) {
                for (Object arg : args) {
                    if (arg instanceof Map && ((Map<?, ?>) arg).containsKey("elementId")) {
                        String elementId = (String) ((Map<?, ?>) arg).get("elementId");
                        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
                        if (element != null) {
                            processedArgs.add(element);
                        } else {
                            result.put("success", false);
                            result.put("error", "Element not found: " + elementId);
                            return result;
                        }
                    } else {
                        processedArgs.add(arg);
                    }
                }
            }

            Object scriptResult = js.executeScript(script, processedArgs.toArray());

            result.put("success", true);
            result.put("result", processScriptResult(scriptResult, sessionId));

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error executing script: " + e.getMessage());
        }

        return result;
    }

    /**
     * Execute JavaScript in the browser without arguments
     *
     * @param sessionId The session ID of the WebDriver
     * @param script JavaScript code to execute
     * @return Map with result of JavaScript execution
     */
    public Map<String, Object> executeScript(String sessionId, String script) {
        return executeScript(sessionId, script, null);
    }

    /**
     * Take a screenshot
     *
     * @param sessionId The session ID of the WebDriver
     * @return Map with screenshot data in Base64 or error information
     */
    public Map<String, Object> takeScreenshot(String sessionId) {
        WebDriver driver = sessionService.getDriver(sessionId);
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
     * Take a screenshot of a specific element
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID
     * @return Map with screenshot data in Base64 or error information
     */
    public Map<String, Object> takeElementScreenshot(String sessionId, String elementId) {
        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        Map<String, Object> result = new HashMap<>();

        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            String screenshot = element.getScreenshotAs(OutputType.BASE64);

            result.put("success", true);
            result.put("screenshot", screenshot);
            result.put("format", "base64");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error taking element screenshot: " + e.getMessage());
        }

        return result;
    }

    /**
     * Process JavaScript execution result to handle WebElements
     *
     * @param scriptResult The raw result from JavaScript execution
     * @param sessionId The WebDriver session ID
     * @return Processed result
     */
    private Object processScriptResult(Object scriptResult, String sessionId) {
        if (scriptResult instanceof WebElement) {
            WebElement element = (WebElement) scriptResult;
            String elementId = elementReferenceManager.storeElement(sessionId, element);

            Map<String, Object> elementInfo = new HashMap<>();
            elementInfo.put("elementId", elementId);

            try {
                elementInfo.put("tagName", element.getTagName());
            } catch (Exception e) {
                elementInfo.put("tagName", "unknown");
            }

            try {
                elementInfo.put("text", element.getText());
            } catch (Exception e) {
                elementInfo.put("text", "");
            }

            return elementInfo;

        } else if (scriptResult instanceof List) {
            List<?> list = (List<?>) scriptResult;
            List<Object> convertedList = new ArrayList<>();

            for (Object item : list) {
                convertedList.add(processScriptResult(item, sessionId));
            }

            return convertedList;

        } else if (scriptResult instanceof Map) {
            // Convert Map to HashMap to ensure serialization works
            return new HashMap<>((Map<?, ?>) scriptResult);
        }

        return scriptResult;
    }
}