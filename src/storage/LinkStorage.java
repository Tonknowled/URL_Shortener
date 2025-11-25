package storage;

import core.ShortLink;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LinkStorage {
    private final Map<String, ShortLink> shortToLinkMap = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> userLinksMap = new ConcurrentHashMap<>();

    // Сохраняем ссылку
    public void saveLink(ShortLink link) {
        shortToLinkMap.put(link.getShortUrl(), link);

        // Добавляем в список ссылок пользователя
        userLinksMap.computeIfAbsent(link.getUserId(), k -> new HashSet<>())
                .add(link.getShortUrl());
    }

    // Получаем ссылку по короткому URL
    public ShortLink getLink(String shortUrl) {
        return shortToLinkMap.get(shortUrl);
    }

    // Получаем все ссылки пользователя
    public List<ShortLink> getUserLinks(UUID userId) {
        Set<String> userShortUrls = userLinksMap.get(userId);
        if (userShortUrls == null) return new ArrayList<>();

        List<ShortLink> result = new ArrayList<>();
        for (String shortUrl : userShortUrls) {
            ShortLink link = shortToLinkMap.get(shortUrl);
            if (link != null) {
                result.add(link);
            }
        }
        return result;
    }

    // Удаляем ссылку
    public boolean deleteLink(String shortUrl, UUID userId) {
        ShortLink link = shortToLinkMap.get(shortUrl);
        if (link != null && link.getUserId().equals(userId)) {
            shortToLinkMap.remove(shortUrl);

            // Удаляем из списка пользователя
            Set<String> userLinks = userLinksMap.get(userId);
            if (userLinks != null) {
                userLinks.remove(shortUrl);
            }
            return true;
        }
        return false;
    }

    // Очищаем просроченные ссылки
    public void cleanupExpiredLinks() {
        Iterator<Map.Entry<String, ShortLink>> iterator = shortToLinkMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ShortLink> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();

                // Также удаляем из пользовательского списка
                UUID userId = entry.getValue().getUserId();
                Set<String> userLinks = userLinksMap.get(userId);
                if (userLinks != null) {
                    userLinks.remove(entry.getKey());
                }
            }
        }
    }
}