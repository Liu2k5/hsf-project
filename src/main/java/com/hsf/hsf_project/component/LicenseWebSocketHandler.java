package com.hsf.hsf_project.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Manages WebSocket sessions.
 * Clients should connect with query params ?licenseKey=...&deviceId=...
 */
@Component
public class LicenseWebSocketHandler extends TextWebSocketHandler {

    // map licenseKey -> session (only one active session per license allowed by design)
    private final Map<String, WebSocketSession> sessionByLicense = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String licenseKey = getParam(session, "licenseKey");
        if (licenseKey != null) {
            sessionByLicense.put(licenseKey, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String licenseKey = getParam(session, "licenseKey");
        if (licenseKey != null) {
            sessionByLicense.remove(licenseKey, session);
        }
    }

    public void sendLock(String licenseKey, String reason) {
        WebSocketSession s = sessionByLicense.get(licenseKey);
        if (s != null && s.isOpen()) {
            try {
                String message = "{\"type\":\"LOCK\",\"reason\":\"" + reason + "\"}";
                s.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                // log
            }
        }
    }

    private String getParam(WebSocketSession session, String name) {
        var uri = session.getUri();
        if (uri == null) return null;
        var qs = uri.getQuery();
        if (qs == null) return null;
        for (String part : qs.split("&")) {
            var kv = part.split("=",2);
            if (kv.length == 2 && kv[0].equals(name)) return kv[1];
        }
        return null;
    }
}