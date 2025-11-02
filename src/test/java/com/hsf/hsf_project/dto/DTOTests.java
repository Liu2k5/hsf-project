package com.hsf.hsf_project.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DTOTests {

    @Test
    void testUserDTOCreation() {
        UserDTO dto = new UserDTO();
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setRoleName("admin");
        dto.setLocked(false);

        assertEquals(1L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("admin", dto.getRoleName());
        assertFalse(dto.isLocked());
    }

    @Test
    void testOrderSummaryDTOCreation() {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setOrderId(1L);
        dto.setUsername("testuser");
        dto.setProductName("Test Product");
        dto.setPaidDate("2025-11-02");
        dto.setStatus("PENDING");

        assertEquals(1L, dto.getOrderId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("Test Product", dto.getProductName());
        assertEquals("2025-11-02", dto.getPaidDate());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void testLicenseDTOCreation() {
        LicenseDTO dto = new LicenseDTO();
        dto.setLicenseId(1L);
        dto.setLicenseKey("TEST-KEY-123");
        dto.setUsername("testuser");
        dto.setProductName("Test Product");
        dto.setEnabled(true);

        assertEquals(1L, dto.getLicenseId());
        assertEquals("TEST-KEY-123", dto.getLicenseKey());
        assertEquals("testuser", dto.getUsername());
        assertEquals("Test Product", dto.getProductName());
        assertTrue(dto.isEnabled());
    }

    @Test
    void testUpdateRoleRequest() {
        UpdateRoleRequest request = new UpdateRoleRequest(1L, "admin");
        assertEquals(1L, request.userId());
        assertEquals("admin", request.newRoleName());
    }

    @Test
    void testToggleLockRequest() {
        ToggleLockRequest request = new ToggleLockRequest(1L, true);
        assertEquals(1L, request.userId());
        assertTrue(request.lock());
    }

    @Test
    void testRevokeRequest() {
        RevokeRequest request = new RevokeRequest("TEST-KEY", "Test reason");
        assertEquals("TEST-KEY", request.licenseKey());
        assertEquals("Test reason", request.reason());
    }

    @Test
    void testResetRequest() {
        ResetRequest request = new ResetRequest("TEST-KEY");
        assertEquals("TEST-KEY", request.licenseKey());
    }

    @Test
    void testStatsSummaryDTO() {
        StatsSummaryDTO dto = new StatsSummaryDTO(1000.0, 5L, 10L);
        assertEquals(1000.0, dto.getTotalRevenue());
        assertEquals(5L, dto.getNewOrdersToday());
        assertEquals(10L, dto.getActiveSessions());
    }
}
