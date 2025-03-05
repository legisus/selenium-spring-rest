# Spring Boot Selenium WebDriver REST API

This service provides REST endpoints to control Chrome WebDriver instances. The application meets all the specified requirements:

- Uses Java 21
- Uses Spring Boot 3.2.0
- Manages Selenium WebDriver with Chrome
- Provides REST API endpoints
- Chrome window doesn't open when the API starts
- Includes endpoints for initializing, navigating, and closing WebDriver sessions

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
java -jar target/selenium-service-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080.

## API Endpoints

### Initialize a WebDriver
Creates a new Chrome WebDriver instance with a visible window.

```
GET /api/webdriver/initialize
```

Response:
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "WebDriver initialized with visible window"
}
```

### Navigate to URL
Opens a specified URL in the browser window.

```
POST /api/webdriver/navigate/{sessionId}
```

Request Body:
```json
{
  "url": "https://www.example.com"
}
```

Response:
```json
{
  "message": "Navigated to URL successfully",
  "url": "https://www.example.com"
}
```

### Close a WebDriver Session
Closes the browser window and the WebDriver instance.

```
GET /api/webdriver/close/{sessionId}
```

Response:
```json
{
  "message": "WebDriver session closed successfully"
}
```

### Get Current URL
Returns the current URL of a specific WebDriver session.

```
GET /api/webdriver/url/{sessionId}
```

Response:
```json
{
  "url": "https://www.example.com"
}
```

### List All Active Sessions
Returns all active WebDriver sessions and their current URLs.

```
GET /api/webdriver/sessions
```

Response:
```json
{
  "550e8400-e29b-41d4-a716-446655440000": "https://www.example.com",
  "7f000001-39f7-46f9-b7c9-bb64b0f25a00": "https://www.google.com"
}
```

## Example Usage with cURL

Initialize a new WebDriver session:
```bash
curl -X GET http://localhost:8080/api/webdriver/initialize
```

Navigate to a URL:
```bash
curl -X POST \
  http://localhost:8080/api/webdriver/navigate/550e8400-e29b-41d4-a716-446655440000 \
  -H 'Content-Type: application/json' \
  -d '{"url": "https://www.example.com"}'
```

Close a session:
```bash
curl -X GET http://localhost:8080/api/webdriver/close/550e8400-e29b-41d4-a716-446655440000
```

## WebDriver Lifecycle

- Each WebDriver instance is assigned a unique session ID
- Sessions remain active until explicitly closed or until the application shuts down
- No Chrome windows are opened until explicitly requested via the API
