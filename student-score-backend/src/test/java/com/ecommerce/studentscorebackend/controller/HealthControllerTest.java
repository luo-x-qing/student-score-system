package com.ecommerce.studentscorebackend.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Health check controller test for Phase 2 verification
 * Simple unit test without Spring context for now
 */
class HealthControllerTest {

    @Test
    void health_shouldReturnUpStatus() {
        HealthController controller = new HealthController();
        var response = controller.health();

        assertNotNull(response);
        assertEquals("UP", response.get("status"));
        assertEquals("student-score-backend", response.get("application"));
        assertNotNull(response.get("timestamp"));
    }
}
