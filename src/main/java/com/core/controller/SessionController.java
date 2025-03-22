package com.core.controller;

import com.core.service.WebDriverSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import oshi.SystemInfo;
import oshi.hardware.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for WebDriver session management
 */
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private WebDriverSessionService sessionService;

    /**
     * Initialize a new Chrome WebDriver with a visible window
     *
     * @return Session ID for the created WebDriver
     */
    @GetMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeDriver() {
        String sessionId = sessionService.initializeVisibleDriver();
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", "WebDriver initialized with visible window");
        return ResponseEntity.ok(response);
    }

    /**
     * Close a specific WebDriver session
     *
     * @param sessionId The session ID to close
     * @return Success or failure message
     */
    @GetMapping("/close/{sessionId}")
    public ResponseEntity<Map<String, Object>> closeDriver(@PathVariable String sessionId) {
        boolean closed = sessionService.closeDriver(sessionId);
        Map<String, Object> response = new HashMap<>();

        if (closed) {
            response.put("success", true);
            response.put("message", "WebDriver session closed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Session not found or already closed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * List all active WebDriver sessions
     *
     * @return Map of session IDs to their current URLs
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, String>> getActiveSessions() {
        Map<String, String> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Set implicit wait timeout for a WebDriver session
     *
     * @param sessionId The session ID
     * @param request Request containing timeout seconds
     * @return Success or failure message
     */
    @PostMapping("/implicitWait/{sessionId}")
    public ResponseEntity<Map<String, Object>> setImplicitWait(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {

        Integer timeoutSeconds;
        try {
            Object timeoutObj = request.get("timeout");
            if (timeoutObj instanceof Integer) {
                timeoutSeconds = (Integer) timeoutObj;
            } else if (timeoutObj instanceof String) {
                timeoutSeconds = Integer.parseInt((String) timeoutObj);
            } else if (timeoutObj instanceof Number) {
                timeoutSeconds = ((Number) timeoutObj).intValue();
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Timeout is required and must be a number");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid timeout value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean success = sessionService.setImplicitWait(sessionId, timeoutSeconds);
        Map<String, Object> response = new HashMap<>();

        if (success) {
            response.put("success", true);
            response.put("message", "Implicit wait set to " + timeoutSeconds + " seconds");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Session not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get the current implicit wait setting
     *
     * @param sessionId The session ID
     * @return Current implicit wait timeout
     */
    @GetMapping("/implicitWait/{sessionId}")
    public ResponseEntity<Map<String, Object>> getImplicitWait(@PathVariable String sessionId) {
        Integer timeout = sessionService.getImplicitWait(sessionId);
        Map<String, Object> response = new HashMap<>();

        if (timeout != null) {
            response.put("success", true);
            response.put("timeout", timeout);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Session not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Close all active WebDriver sessions
     *
     * @return Success or failure message with count of closed sessions
     */
    @GetMapping("/closeAll")
    public ResponseEntity<Map<String, Object>> closeAllDrivers() {
        int closedCount = sessionService.closeAllDrivers();
        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", closedCount + " WebDriver sessions closed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get status and count of initialized sessions with system metrics
     *
     * @return Status information about active sessions and system metrics
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSessionStatus() {
        Map<String, String> sessions = sessionService.getActiveSessions();
        Map<String, Object> response = new HashMap<>();

        // Session information
        response.put("activeSessionCount", sessions.size());
        response.put("status", sessions.size() > 0 ? "Active" : "Idle");

        // Add detailed system metrics
        try {
            // Memory metrics
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            long totalMemory = runtime.totalMemory() / (1024 * 1024);

            Map<String, Object> memoryMetrics = new HashMap<>();
            memoryMetrics.put("used", usedMemory + " MB");
            memoryMetrics.put("total", totalMemory + " MB");
            memoryMetrics.put("max", maxMemory + " MB");
            memoryMetrics.put("usagePercentage", Math.round((double) usedMemory / maxMemory * 100) + "%");
            response.put("memory", memoryMetrics);

            // OSHI metrics
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();

            // CPU metrics
            CentralProcessor processor = hardware.getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            // Wait a bit to get a difference
            Thread.sleep(500);
            long[] ticks = processor.getSystemCpuLoadTicks();
            double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

            Map<String, Object> cpuMetrics = new HashMap<>();
            cpuMetrics.put("usage", String.format("%.2f%%", cpuUsage));
            cpuMetrics.put("cores", processor.getLogicalProcessorCount());
            cpuMetrics.put("model", processor.getProcessorIdentifier().getName());
            response.put("cpu", cpuMetrics);

            // GPU metrics (if available)
            List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
            if (!graphicsCards.isEmpty()) {
                List<Map<String, Object>> gpuList = new ArrayList<>();
                for (GraphicsCard gpu : graphicsCards) {
                    Map<String, Object> gpuMetrics = new HashMap<>();
                    gpuMetrics.put("name", gpu.getName());
                    gpuMetrics.put("vendor", gpu.getVendor());
                    gpuMetrics.put("vram", gpu.getVRam() / (1024 * 1024) + " MB");
                    gpuList.add(gpuMetrics);
                }
                response.put("gpu", gpuList);
            }

            // System RAM
            GlobalMemory memory = hardware.getMemory();
            long totalRAM = memory.getTotal() / (1024 * 1024);
            long availableRAM = memory.getAvailable() / (1024 * 1024);
            long usedRAM = totalRAM - availableRAM;

            Map<String, Object> ramMetrics = new HashMap<>();
            ramMetrics.put("total", totalRAM + " MB");
            ramMetrics.put("used", usedRAM + " MB");
            ramMetrics.put("available", availableRAM + " MB");
            ramMetrics.put("usagePercentage", Math.round((double) usedRAM / totalRAM * 100) + "%");
            response.put("ram", ramMetrics);

            // Disk usage
            List<Map<String, Object>> diskList = new ArrayList<>();
            for (HWDiskStore disk : hardware.getDiskStores()) {
                Map<String, Object> diskMetrics = new HashMap<>();
                diskMetrics.put("name", disk.getName());
                diskMetrics.put("model", disk.getModel());
                diskMetrics.put("size", disk.getSize() / (1024 * 1024 * 1024) + " GB");
                diskMetrics.put("reads", disk.getReads());
                diskMetrics.put("writes", disk.getWrites());
                diskList.add(diskMetrics);
            }
            response.put("disks", diskList);

        } catch (Exception e) {
            // If there's an error getting system metrics, include basic info
            response.put("error", "Failed to retrieve detailed system metrics: " + e.getMessage());

            // Add basic memory info as fallback
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            response.put("memoryUsage", usedMemory + "MB / " + maxMemory + "MB");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get all active session IDs
     *
     * @return List of active session IDs
     */
    @GetMapping("/ids")
    public ResponseEntity<Map<String, Object>> getSessionIds() {
        List<String> sessionIds = sessionService.getActiveSessionIds();
        Map<String, Object> response = new HashMap<>();

        response.put("sessionIds", sessionIds);
        response.put("count", sessionIds.size());

        return ResponseEntity.ok(response);
    }


}