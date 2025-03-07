package com.core.service.serviceUtils;

import org.openqa.selenium.By;

/**
 * Utility class for working with Selenium locators
 */
public class LocatorUtils {

    /**
     * Create a By locator based on locator type and value
     *
     * @param locatorType Type of locator
     * @param locatorValue Value of locator
     * @return By locator
     * @throws IllegalArgumentException if the locator type is invalid
     */
    public static By createLocator(String locatorType, String locatorValue) {
        if (locatorType == null || locatorValue == null) {
            throw new IllegalArgumentException("Locator type and value cannot be null");
        }

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
}
