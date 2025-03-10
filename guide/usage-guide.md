# Selenium WebDriver REST API

A comprehensive REST API for orchestrating Selenium WebDriver, built with Spring Boot 3.4.3 and Java 21.

## Features

- Complete Selenium WebDriver functionality exposed through REST endpoints
- Modular architecture with separated concerns
- Chrome browser automation
- Multiple simultaneous browser sessions
- Element interaction, waiters, screenshots, JavaScript execution
- Assertions, cookies, form handling, and more
- Robust error handling and session management

## Building and Running the Application

### Prerequisites
- Java 21 JDK
- Maven 3.6+ 
- Chrome browser installed

### Build the Application
```bash
mvn clean package
```

### Run the Application
```bash
java -jar target/selenium-core-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080.

## Modular Architecture

The application follows a modular architecture divided into service and controller layers:

### Service Layer

```
com.example.seleniumservice/
└── service/
    ├── WebDriverSessionService.java       // Session management
    ├── WebDriverNavigationService.java    // Navigation operations
    ├── WebDriverElementService.java       // Element operations
    ├── WebDriverWaitService.java          // Wait operations
    ├── WebDriverAssertionService.java     // Assertions
    ├── WebDriverScriptService.java        // JavaScript execution
    ├── WebDriverCookieService.java        // Cookie management
    ├── WebDriverFrameService.java         // Frame and alert handling
    ├── WebDriverFormService.java          // Form operations
    └── ElementReferenceManager.java       // Utility for elements
```

### Controller Layer

```
com.example.seleniumservice/
└── controller/
    ├── SessionController.java            // Session endpoints
    ├── NavigationController.java         // Navigation endpoints
    ├── ElementController.java            // Element endpoints
    ├── WaitController.java               // Wait endpoints
    ├── ScriptController.java             // JavaScript endpoints
    ├── AssertionController.java          // Assertion endpoints
    ├── CookieController.java             // Cookie endpoints
    ├── FrameController.java              // Frame endpoints
    └── FormController.java               // Form endpoints
```

## API Endpoints

### Session Management

- `GET /api/session/initialize` - Create a new WebDriver session
- `GET /api/session/close/{sessionId}` - Close a specific session
- `GET /api/session/list` - List all active sessions
- `POST /api/session/implicitWait/{sessionId}` - Set implicit wait timeout

### Navigation

- `POST /api/navigation/to/{sessionId}` - Navigate to a URL
- `GET /api/navigation/url/{sessionId}` - Get current URL
- `GET /api/navigation/title/{sessionId}` - Get page title
- `GET /api/navigation/refresh/{sessionId}` - Refresh the current page
- `GET /api/navigation/back/{sessionId}` - Navigate back in history
- `GET /api/navigation/forward/{sessionId}` - Navigate forward in history

### Element Operations

- `POST /api/element/find/{sessionId}` - Find an element
- `POST /api/element/findAll/{sessionId}` - Find multiple elements
- `GET /api/element/click/{sessionId}/{elementId}` - Click an element
- `POST /api/element/sendKeys/{sessionId}/{elementId}` - Send text to an element
- `GET /api/element/text/{sessionId}/{elementId}` - Get element text
- `GET /api/element/attribute/{sessionId}/{elementId}/{attributeName}` - Get element attribute

### Wait Operations

- `POST /api/wait/explicit/{sessionId}` - Explicit wait for element conditions
- `GET /api/wait/static/{sessionId}/{seconds}` - Static wait
- `POST /api/wait/javascript/{sessionId}` - Wait for JavaScript condition

### JavaScript and Screenshots

- `POST /api/script/execute/{sessionId}` - Execute JavaScript
- `GET /api/script/screenshot/{sessionId}` - Take a screenshot

### Frame and Alert Handling

- `POST /api/frame/switchTo/{sessionId}` - Switch to a frame
- `GET /api/frame/switchToDefault/{sessionId}` - Switch to default content
- `POST /api/frame/alert/handle/{sessionId}` - Handle alert (accept/dismiss)

### Form Handling

- `POST /api/form/select/text/{sessionId}/{elementId}` - Select by visible text
- `POST /api/form/select/value/{sessionId}/{elementId}` - Select by value
- `POST /api/form/select/index/{sessionId}/{elementId}` - Select by index

## Example Usage with cURL

### Initialize a Session

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

### Navigate to a URL

```bash
curl -X POST \
  http://localhost:8080/api/navigation/to/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"url": "https://www.example.com", "timeout": 60}'
```

### Find an Element

```bash
curl -X POST \
  http://localhost:8080/api/element/find/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"locatorType": "xpath", "locatorValue": "//button[@id=\"login\"]"}'
```

### Click an Element

```bash
curl -X GET \
  http://localhost:8080/api/element/click/550e8400-e29b-41d4-a716-446655440000/f47ac10b-58cc-4372-a567-0e02b2c3d479
```

### Close a Session

```bash
curl -X GET \
  http://localhost:8080/api/session/close/550e8400-e29b-41d4-a716-446655440000
```

## Complete Example: Login to a Website

This bash script demonstrates a complete login workflow:

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

## Benefits of Modular Architecture

1. **Maintainability**: Each class has a clear, focused responsibility
2. **Testability**: Services and controllers can be tested in isolation
3. **Scalability**: Multiple developers can work on different areas
4. **Flexibility**: New features can be added without affecting existing code
5. **Readability**: Code organization follows a logical structure

## Dependencies

The project uses the following major dependencies:

- Spring Boot 3.4.3
- Selenium Java 4.29.0
- WebDriverManager 5.9.2
- Lombok (for code simplification)
- Java 21

For a complete list of dependencies, refer to the `pom.xml` file.

## Configuration

The application can be configured through `application.properties`:

```properties
# Server configuration
server.port=8080

# Selenium configuration
selenium.default-page-load-timeout=30
selenium.default-explicit-wait-timeout=30
selenium.default-implicit-wait-timeout=0
selenium.headless-by-default=true
selenium.chrome-options=--disable-gpu,--no-sandbox,--disable-dev-shm-usage
```

You can adjust these properties to customize timeouts, browser behavior, and other settings.
