# Selenium WebDriver REST API

A comprehensive REST API for orchestrating Selenium WebDriver, built with Spring Boot 3.4.3 and Java 21.

## Features

- Complete Selenium WebDriver functionality exposed through REST endpoints
- Modular architecture with separated concerns
- Chrome browser automation
- Multiple simultaneous browser sessions
- System monitoring and resource usage statistics
- Session management with bulk operations
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
- `GET /api/session/closeAll` - Close all active sessions
- `GET /api/session/list` - List all active sessions
- `GET /api/session/ids` - Get IDs of all active sessions
- `GET /api/session/status` - Get detailed system and session metrics
- `POST /api/session/implicitWait/{sessionId}` - Set implicit wait timeout
- `GET /api/session/implicitWait/{sessionId}` - Get current implicit wait timeout

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

### Get System Status and Metrics

```bash
curl -X GET http://localhost:8080/api/session/status
```

Response:
```json
{
  "activeSessionCount": 3,
  "status": "Active",
  "memory": {
    "used": "30 MB",
    "total": "256 MB",
    "max": "2048 MB",
    "usagePercentage": "1%"
  },
  "cpu": {
    "usage": "23.45%",
    "cores": 8,
    "model": "Intel(R) Core(TM) i7-9700K CPU @ 3.60GHz"
  },
  "ram": {
    "total": "16384 MB",
    "used": "8192 MB",
    "available": "8192 MB",
    "usagePercentage": "50%"
  },
  "gpu": [
    {
      "name": "NVIDIA GeForce RTX 3070",
      "vendor": "NVIDIA Corporation",
      "vram": "8192 MB"
    }
  ],
  "disks": [
    {
      "name": "sda",
      "model": "Samsung SSD 970 EVO 1TB",
      "size": "1000 GB",
      "reads": 1234567,
      "writes": 7654321
    }
  ]
}
```

### Get All Session IDs

```bash
curl -X GET http://localhost:8080/api/session/ids
```

Response:
```json
{
  "sessionIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "661f9511-f30c-52e5-b827-557766551111",
    "772a0622-g41d-63f6-c938-668877662222"
  ],
  "count": 3
}
```

### Close All Sessions

```bash
curl -X GET http://localhost:8080/api/session/closeAll
```

Response:
```json
{
  "success": true,
  "message": "3 WebDriver sessions closed successfully"
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

## Complete Example: Multisession Load Testing with Monitoring

This bash script demonstrates a complete workflow for multisession load testing with monitoring:

```bash
#!/bin/bash

# Check initial system status
echo "Checking initial system status..."
curl -s -X GET http://localhost:8080/api/session/status

# Create multiple sessions (5 browsers)
echo "Creating 5 browser sessions..."
SESSIONS=()
for i in {1..5}; do
  SESSION_RESPONSE=$(curl -s -X GET http://localhost:8080/api/session/initialize)
  SESSION_ID=$(echo $SESSION_RESPONSE | jq -r '.sessionId')
  SESSIONS+=($SESSION_ID)
  echo "Created session $i: $SESSION_ID"
done

# Monitor status after creating sessions
echo -e "\nChecking system status after creating sessions..."
curl -s -X GET http://localhost:8080/api/session/status

# Navigate all browsers to different pages
echo -e "\nNavigating browsers to different pages..."
for i in {0..4}; do
  SESSION_ID=${SESSIONS[$i]}
  URL="https://example.com/page$i"
  echo "Navigating session ${SESSION_ID} to ${URL}"
  curl -s -X POST \
    "http://localhost:8080/api/navigation/to/${SESSION_ID}" \
    -H 'Content-Type: application/json' \
    -d "{\"url\": \"${URL}\"}"
  
  # Slight delay between navigations
  sleep 1
done

# Take screenshots of all sessions
echo -e "\nCapturing screenshots from all sessions..."
for i in {0..4}; do
  SESSION_ID=${SESSIONS[$i]}
  echo "Taking screenshot for session ${SESSION_ID}"
  curl -s -X GET \
    "http://localhost:8080/api/script/screenshot/${SESSION_ID}" \
    > "screenshot_session_${i}.png"
done

# Get a list of all session IDs
echo -e "\nListing all active session IDs:"
curl -s -X GET http://localhost:8080/api/session/ids

# Check system status under load
echo -e "\nChecking system status under load..."
curl -s -X GET http://localhost:8080/api/session/status

# Close all sessions at once
echo -e "\nClosing all sessions..."
curl -s -X GET http://localhost:8080/api/session/closeAll

# Verify sessions are closed
echo -e "\nVerifying all sessions are closed..."
curl -s -X GET http://localhost:8080/api/session/status

echo -e "\nLoad test completed!"
```

## Benefits of the Enhanced Architecture

1. **Maintainability**: Each class has a clear, focused responsibility
2. **Testability**: Services and controllers can be tested in isolation
3. **Scalability**: Multiple developers can work on different areas
4. **Flexibility**: New features can be added without affecting existing code
5. **Monitoring**: Comprehensive system metrics track resource usage
6. **Session Management**: Bulk operations for managing multiple sessions
7. **Readability**: Code organization follows a logical structure

## Dependencies

The project uses the following major dependencies:

- Spring Boot 3.4.3
- Selenium Java 4.29.0
- WebDriverManager 5.9.2
- OSHI Core 6.4.0 (for system monitoring)
- Lombok (for code simplification)
- Java 21

For a complete list of dependencies, refer to the `pom.xml` file.

## Configuration

The application can be configured through `application.properties`:

```properties
# Server configuration
server.port=8080

# Selenium configuration
selenium