# Selenium REST API Controllers Guide

This guide explains the new modular controller structure that replaces the monolithic WebDriverControllerExpanded. Each controller is focused on a specific aspect of the Selenium API, making the code more maintainable and easier to understand.

## Controller Package Structure

```
com.example.seleniumservice/
└── controller/
    ├── SessionController.java            // WebDriver session management endpoints
    ├── NavigationController.java         // Browser navigation endpoints
    ├── ElementController.java            // Element finding and interaction endpoints
    ├── WaitController.java               // Explicit and implicit wait endpoints
    ├── ScriptController.java             // JavaScript execution and screenshot endpoints
    ├── AssertionController.java          // Assertion endpoints
    ├── CookieController.java             // Cookie management endpoints
    ├── FrameController.java              // Frame and alert handling endpoints
    └── FormController.java               // Form and dropdown handling endpoints
```

## API Routes

The REST API endpoints are now organized by function, with consistent URL structures:

### Session Management

- `GET /api/session/initialize` - Create a new WebDriver session
- `GET /api/session/close/{sessionId}` - Close a specific session
- `GET /api/session/list` - List all active sessions
- `POST /api/session/implicitWait/{sessionId}` - Set implicit wait timeout
- `GET /api/session/implicitWait/{sessionId}` - Get the current implicit wait setting

### Navigation

- `POST /api/navigation/to/{sessionId}` - Navigate to a URL
- `GET /api/navigation/url/{sessionId}` - Get current URL
- `GET /api/navigation/title/{sessionId}` - Get page title
- `GET /api/navigation/source/{sessionId}` - Get page source
- `GET /api/navigation/refresh/{sessionId}` - Refresh the current page
- `GET /api/navigation/back/{sessionId}` - Navigate back in history
- `GET /api/navigation/forward/{sessionId}` - Navigate forward in history

### Element Operations

- `POST /api/element/find/{sessionId}` - Find an element
- `POST /api/element/findAll/{sessionId}` - Find multiple elements
- `GET /api/element/click/{sessionId}/{elementId}` - Click an element
- `POST /api/element/sendKeys/{sessionId}/{elementId}` - Send text to an element
- `GET /api/element/attribute/{sessionId}/{elementId}/{attributeName}` - Get element attribute
- `GET /api/element/text/{sessionId}/{elementId}` - Get element text
- `GET /api/element/isDisplayed/{sessionId}/{elementId}` - Check if element is displayed
- `GET /api/element/isEnabled/{sessionId}/{elementId}` - Check if element is enabled
- `GET /api/element/isSelected/{sessionId}/{elementId}` - Check if element is selected

### Wait Operations

- `POST /api/wait/explicit/{sessionId}` - Explicit wait for element conditions
- `GET /api/wait/static/{sessionId}/{seconds}` - Static wait (sleep)
- `POST /api/wait/javascript/{sessionId}` - Wait for JavaScript condition to be true

### JavaScript and Screenshots

- `POST /api/script/execute/{sessionId}` - Execute JavaScript
- `GET /api/script/screenshot/{sessionId}` - Take a page screenshot
- `GET /api/script/screenshot/{sessionId}/{elementId}` - Take an element screenshot

### Assertions

- `POST /api/assert/element/{sessionId}/{elementId}` - Assert element properties
- `POST /api/assert/url/{sessionId}` - Assert URL matches a pattern
- `POST /api/assert/title/{sessionId}` - Assert page title matches a pattern

### Cookie Management

- `GET /api/cookie/all/{sessionId}` - Get all cookies
- `GET /api/cookie/{sessionId}/{name}` - Get a specific cookie
- `POST /api/cookie/add/{sessionId}` - Add a cookie
- `DELETE /api/cookie/{sessionId}/{name}` - Delete a specific cookie
- `DELETE /api/cookie/all/{sessionId}` - Delete all cookies

### Frame and Alert Handling

- `POST /api/frame/switchTo/{sessionId}` - Switch to a frame
- `GET /api/frame/switchToDefault/{sessionId}` - Switch to default content
- `GET /api/frame/switchToParent/{sessionId}` - Switch to parent frame
- `POST /api/frame/alert/handle/{sessionId}` - Handle alert (accept/dismiss)
- `POST /api/frame/alert/sendText/{sessionId}` - Send text to alert prompt
- `GET /api/frame/alert/getText/{sessionId}` - Get alert text

### Form Handling

- `POST /api/form/select/text/{sessionId}/{elementId}` - Select option by visible text
- `POST /api/form/select/value/{sessionId}/{elementId}` - Select option by value
- `POST /api/form/select/index/{sessionId}/{elementId}` - Select option by index
- `GET /api/form/select/options/{sessionId}/{elementId}` - Get selected options
- `GET /api/form/select/allOptions/{sessionId}/{elementId}` - Get all available options
- `GET /api/form/select/deselectAll/{sessionId}/{elementId}` - Deselect all options

## Curl Examples

Here are examples of how to use the API with curl:

### Session Management

**Initialize a WebDriver session:**
```bash
curl -X GET http://localhost:8080/api/session/initialize
```

Response:
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "WebDriver initialized with visible window"
}
```

**Set implicit wait timeout:**
```bash
curl -X POST \
  http://localhost:8080/api/session/implicitWait/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"timeout": 10}'
```

**Close a session:**
```bash
curl -X GET \
  http://localhost:8080/api/session/close/550e8400-e29b-41d4-a716-446655440000
```

### Navigation Operations

**Navigate to a URL:**
```bash
curl -X POST \
  http://localhost:8080/api/navigation/to/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"url": "https://www.example.com", "timeout": 60}'
```

**Get current URL:**
```bash
curl -X GET \
  http://localhost:8080/api/navigation/url/550e8400-e29b-41d4-a716-446655440000
```

**Navigate back:**
```bash
curl -X GET \
  http://localhost:8080/api/navigation/back/550e8400-e29b-41d4-a716-446655440000
```

### Element Operations

**Find an element:**
```bash
curl -X POST \
  http://localhost:8080/api/element/find/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"locatorType": "id", "locatorValue": "username"}'
```

Response:
```json
{
  "success": true,
  "found": true,
  "tagName": "input",
  "displayed": true,
  "enabled": true,
  "selected": false,
  "text": "",
  "value": "",
  "elementId": "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890"
}
```

**Click on an element:**
```bash
curl -X GET \
  http://localhost:8080/api/element/click/550e8400-e29b-41d4-a716-446655440000/a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890
```

**Send text to an element:**
```bash
curl -X POST \
  http://localhost:8080/api/element/sendKeys/550e8400-e29b-41d4-a716-446655440000/a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890 \
  -H 'Content-Type: application/json' \
  -d '{"text": "Hello World", "clearFirst": true}'
```

**Get element text:**
```bash
curl -X GET \
  http://localhost:8080/api/element/text/550e8400-e29b-41d4-a716-446655440000/a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890
```

### Wait Operations

**Explicit wait for an element:**
```bash
curl -X POST \
  http://localhost:8080/api/wait/explicit/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{
    "locatorType": "id",
    "locatorValue": "submit-button",
    "waitCondition": "clickable",
    "timeout": 15
  }'
```

**Static wait:**
```bash
curl -X GET \
  http://localhost:8080/api/wait/static/550e8400-e29b-41d4-a716-446655440000/5
```

### JavaScript and Screenshots

**Execute JavaScript:**
```bash
curl -X POST \
  http://localhost:8080/api/script/execute/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{
    "script": "return document.title;",
    "args": []
  }'
```

**Take screenshot:**
```bash
curl -X GET \
  http://localhost:8080/api/script/screenshot/550e8400-e29b-41d4-a716-446655440000
```

### Assertions

**Assert element text:**
```bash
curl -X POST \
  http://localhost:8080/api/assert/element/550e8400-e29b-41d4-a716-446655440000/a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890 \
  -H 'Content-Type: application/json' \
  -d '{
    "assertType": "contains",
    "property": "text",
    "expectedValue": "Hello"
  }'
```

**Assert URL:**
```bash
curl -X POST \
  http://localhost:8080/api/assert/url/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{
    "assertType": "contains",
    "expectedUrl": "example.com"
  }'
```

### Cookie Management

**Get all cookies:**
```bash
curl -X GET \
  http://localhost:8080/api/cookie/all/550e8400-e29b-41d4-a716-446655440000
```

**Add a cookie:**
```bash
curl -X POST \
  http://localhost:8080/api/cookie/add/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "sessionToken",
    "value": "abc123",
    "domain": "example.com",
    "path": "/",
    "expiry": "2023-12-31T23:59:59.999+0000",
    "secure": true,
    "httpOnly": true
  }'
```

**Delete a cookie:**
```bash
curl -X DELETE \
  http://localhost:8080/api/cookie/550e8400-e29b-41d4-a716-446655440000/sessionToken
```

### Frame and Alert Handling

**Switch to frame:**
```bash
curl -X POST \
  http://localhost:8080/api/frame/switchTo/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{
    "frameLocator": "id",
    "frameValue": "myFrame"
  }'
```

**Accept an alert:**
```bash
curl -X POST \
  http://localhost:8080/api/frame/alert/handle/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"accept": true}'
```

### Form Handling

**Select option by visible text:**
```bash
curl -X POST \
  http://localhost:8080/api/form/select/text/550e8400-e29b-41d4-a716-446655440000/a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890 \
  -H 'Content-Type: application/json' \
  -d '{"text": "Option 1"}'
```

**Get all options in a select:**
```bash
curl -X GET \
  http://localhost:8080/api/form/select/allOptions/550e8400-e29b-41d4-a716-446655440000/a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890
```

## Complete Example: Login to a Website

Here's a complete example of using the API to log in to a website:

```bash
#!/bin/bash

# Initialize WebDriver session
SESSION_RESPONSE=$(curl -s -X GET http://localhost:8080/api/session/initialize)
SESSION_ID=$(echo $SESSION_RESPONSE | jq -r '.sessionId')
echo "Session ID: $SESSION_ID"

# Navigate to login page
curl -s -X POST \
  http://localhost:8080/api/navigation/to/$SESSION_ID \
  -H 'Content-Type: application/json' \
  -d '{"url": "https://example.com/login"}'

# Find username field
USERNAME_RESPONSE=$(curl -s -X POST \
  http://localhost:8080/api/element/find/$SESSION_ID \
  -H 'Content-Type: application/json' \
  -d '{"locatorType": "id", "locatorValue": "username"}')
USERNAME_ID=$(echo $USERNAME_RESPONSE | jq -r '.elementId')

# Enter username
curl -s -X POST \
  http://localhost:8080/api/element/sendKeys/$SESSION_ID/$USERNAME_ID \
  -H 'Content-Type: application/json' \
  -d '{"text": "testuser", "clearFirst": true}'

# Find password field
PASSWORD_RESPONSE=$(curl -s -X POST \
  http://localhost:8080/api/element/find/$SESSION_ID \
  -H 'Content-Type: application/json' \
  -d '{"locatorType": "id", "locatorValue": "password"}')
PASSWORD_ID=$(echo $PASSWORD_RESPONSE | jq -r '.elementId')

# Enter password
curl -s -X POST \
  http://localhost:8080/api/element/sendKeys/$SESSION_ID/$PASSWORD_ID \
  -H 'Content-Type: application/json' \
  -d '{"text": "password123", "clearFirst": true}'

# Find login button
BUTTON_RESPONSE=$(curl -s -X POST \
  http://localhost:8080/api/element/find/$SESSION_ID \
  -H 'Content-Type: application/json' \
  -d '{"locatorType": "xpath", "locatorValue": "//button[@type=\"submit\"]"}')
BUTTON_ID=$(echo $BUTTON_RESPONSE | jq -r '.elementId')

# Click login button
curl -s -X GET \
  http://localhost:8080/api/element/click/$SESSION_ID/$BUTTON_ID

# Wait for dashboard to load (verify login success)
curl -s -X POST \
  http://localhost:8080/api/wait/explicit/$SESSION_ID \
  -H 'Content-Type: application/json' \
  -d '{
    "locatorType": "xpath",
    "locatorValue": "//h1[contains(text(), \"Dashboard\")]",
    "waitCondition": "visible",
    "timeout": 10
  }'

# Take a screenshot of the dashboard
curl -s -X GET \
  http://localhost:8080/api/script/screenshot/$SESSION_ID \
  > dashboard.png

# Close the session
curl -s -X GET \
  http://localhost:8080/api/session/close/$SESSION_ID

echo "Login test completed!"
```