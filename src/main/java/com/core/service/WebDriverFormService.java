package com.core.service;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for form operations (select, dropdown, etc.)
 */
@Service
public class WebDriverFormService {

    @Autowired
    private ElementReferenceManager elementReferenceManager;

    /**
     * Select an option from a dropdown by visible text
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID of the select element
     * @param visibleText The visible text to select
     * @return Map with success or error information
     */
    public Map<String, Object> selectByVisibleText(String sessionId, String elementId, String visibleText) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
            result.put("success", true);
            result.put("selectedText", visibleText);
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
     * @param value The value to select
     * @return Map with success or error information
     */
    public Map<String, Object> selectByValue(String sessionId, String elementId, String value) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            Select select = new Select(element);
            select.selectByValue(value);
            result.put("success", true);
            result.put("selectedValue", value);
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
     * @param index The index to select
     * @return Map with success or error information
     */
    public Map<String, Object> selectByIndex(String sessionId, String elementId, int index) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            Select select = new Select(element);
            select.selectByIndex(index);
            result.put("success", true);
            result.put("selectedIndex", index);
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

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            Select select = new Select(element);
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
            result.put("isMultiple", select.isMultiple());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting selected options: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get all available options from a dropdown
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID of the select element
     * @return Map with all options or error information
     */
    public Map<String, Object> getAllOptions(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            Select select = new Select(element);
            List<WebElement> allOptions = select.getOptions();

            List<Map<String, String>> optionsList = new ArrayList<>();
            for (WebElement option : allOptions) {
                Map<String, String> optionMap = new HashMap<>();
                optionMap.put("text", option.getText());
                optionMap.put("value", option.getAttribute("value"));
                optionsList.add(optionMap);
            }

            result.put("success", true);
            result.put("options", optionsList);
            result.put("count", allOptions.size());
            result.put("isMultiple", select.isMultiple());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error getting all options: " + e.getMessage());
        }

        return result;
    }

    /**
     * Deselect all options in a multi-select dropdown
     *
     * @param sessionId The session ID of the WebDriver
     * @param elementId The element ID of the select element
     * @return Map with success or error information
     */
    public Map<String, Object> deselectAll(String sessionId, String elementId) {
        Map<String, Object> result = new HashMap<>();

        WebElement element = elementReferenceManager.getElement(sessionId, elementId);
        if (element == null) {
            result.put("success", false);
            result.put("error", "Element not found or expired");
            return result;
        }

        try {
            Select select = new Select(element);
            if (!select.isMultiple()) {
                result.put("success", false);
                result.put("error", "Not a multi-select element");
                return result;
            }

            select.deselectAll();
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error deselecting all options: " + e.getMessage());
        }

        return result;
    }
}
