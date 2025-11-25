package service;

import core.ShortLink;
import storage.LinkStorage;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UrlShortenerService {
    private final LinkStorage storage;
    private final SecureRandom random;
    private final String baseUrl;
    private final int defaultMaxClicks;
    private final int defaultHoursToLive;

    public UrlShortenerService(String baseUrl, int defaultMaxClicks, int defaultHoursToLive) {
        this.storage = new LinkStorage();
        this.random = new SecureRandom();
        this.baseUrl = baseUrl;
        this.defaultMaxClicks = defaultMaxClicks;
        this.defaultHoursToLive = defaultHoursToLive;

        // Запускаем очистку просроченных ссылок каждые 10 минут
        startCleanupTask();
    }

    // Создаем короткую ссылку
    public String shortenUrl(String longUrl, UUID userId) {
        return shortenUrl(longUrl, userId, defaultMaxClicks, defaultHoursToLive);
    }

    public String shortenUrl(String longUrl, UUID userId, int maxClicks, int hoursToLive) {
        // Генерируем уникальный ключ
        String key;
        do {
            key = generateUniqueKey();
        } while (storage.getLink(baseUrl + "/" + key) != null);

        String shortUrl = baseUrl + "/" + key;

        // Создаем и сохраняем ссылку
        ShortLink link = new ShortLink(shortUrl, longUrl, userId, maxClicks, hoursToLive);
        storage.saveLink(link);

        return shortUrl;
    }

    // Открываем ссылку (увеличиваем счетчик)
    public String openUrl(String shortUrl) {
        ShortLink link = storage.getLink(shortUrl);

        if (link == null) {
            throw new RuntimeException("Ссылка не найдена: " + shortUrl);
        }

        if (link.isExpired()) {
            notifyUser(link.getUserId(), "Ссылка истекла: " + shortUrl);
            throw new RuntimeException("Ссылка истекла: " + shortUrl);
        }

        if (link.isClickLimitReached()) {
            notifyUser(link.getUserId(), "Лимит переходов исчерпан: " + shortUrl);
            throw new RuntimeException("Лимит переходов исчерпан: " + shortUrl);
        }

        // Увеличиваем счетчик и возвращаем оригинальный URL
        link.incrementClickCount();
        return link.getLongUrl();
    }

    // Получаем статистику пользователя
    public List<ShortLink> getUserLinks(UUID userId) {
        return storage.getUserLinks(userId);
    }

    // Редактируем лимит переходов
    public boolean updateClickLimit(String shortUrl, UUID userId, int newMaxClicks) {
        ShortLink link = storage.getLink(shortUrl);
        if (link != null && link.getUserId().equals(userId)) {
            link.setMaxClicks(newMaxClicks);
            return true;
        }
        return false;
    }

    // Удаляем ссылку
    public boolean deleteLink(String shortUrl, UUID userId) {
        return storage.deleteLink(shortUrl, userId);
    }

    // Генерация уникального ключа
    private String generateUniqueKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(characters.charAt(random.nextInt(characters.length())));
        }
        return key.toString();
    }

    // Уведомление пользователя (в консоль)
    private void notifyUser(UUID userId, String message) {
        System.out.println("Уведомление для пользователя " + userId + ": " + message);
    }

    // Задача для очистки просроченных ссылок
    private void startCleanupTask() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                storage::cleanupExpiredLinks, 10, 10, TimeUnit.MINUTES
        );
    }
}