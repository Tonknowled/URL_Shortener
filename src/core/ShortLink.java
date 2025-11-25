package core;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShortLink {
    private final String shortUrl;
    private final String longUrl;
    private final UUID userId;
    private int clickCount;
    private int maxClicks;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public ShortLink(String shortUrl, String longUrl, UUID userId, int maxClicks, int hoursToLive) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.userId = userId;
        this.clickCount = 0;
        this.maxClicks = maxClicks;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusHours(hoursToLive);
    }

    // Геттеры
    public String getShortUrl() { return shortUrl; }
    public String getLongUrl() { return longUrl; }
    public UUID getUserId() { return userId; }
    public int getClickCount() { return clickCount; }
    public int getMaxClicks() { return maxClicks; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    // Сеттеры
    public void setMaxClicks(int maxClicks) { this.maxClicks = maxClicks; }

    // Методы для работы со счетчиком
    public void incrementClickCount() { this.clickCount++; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isClickLimitReached() {
        return clickCount >= maxClicks;
    }

    public boolean canBeAccessed() {
        return !isExpired() && !isClickLimitReached();
    }
}