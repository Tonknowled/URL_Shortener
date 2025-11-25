package service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final ConcurrentHashMap<String, UUID> userSessions;

    public UserManager() {
        this.userSessions = new ConcurrentHashMap<>();
    }

    public UUID getOrCreateUserId(String sessionId) {
        return userSessions.computeIfAbsent(sessionId, k -> UUID.randomUUID());
    }

    public UUID getUserId(String sessionId) {
        return userSessions.get(sessionId);
    }
}