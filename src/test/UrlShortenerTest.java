import service.UrlShortenerService;
import service.UserManager;
import core.ShortLink;
import java.util.UUID;
import java.util.List;

public class UrlShortenerTest {
    public static void main(String[] args) {
        System.out.println("=== Тестирование сервиса сокращения ссылок ===");

        UrlShortenerService service = new UrlShortenerService("test.ru", 3, 1);
        UserManager userManager = new UserManager();
        UUID userId = userManager.getOrCreateUserId("test-session");

        try {
            System.out.println("\n1. Тест создания ссылки:");
            String shortUrl = service.shortenUrl("https://www.example.com", userId);
            System.out.println("Создана короткая ссылка: " + shortUrl);

            System.out.println("\n2. Тест открытия ссылки:");
            String longUrl = service.openUrl(shortUrl);
            System.out.println("Открываем: " + longUrl);

            System.out.println("\n3. Тест списка ссылок:");
            List<ShortLink> links = service.getUserLinks(userId);
            System.out.println("Найдено ссылок: " + links.size());

            System.out.println("\n4. Тест статистики:");
            System.out.println("Первая ссылка: " + links.get(0).getShortUrl());
            System.out.println("Количество переходов: " + links.get(0).getClickCount());

            System.out.println("\n✅ Все тесты пройдены успешно!");

        } catch (Exception e) {
            System.out.println("❌ Ошибка тестирования: " + e.getMessage());
            System.out.println("Детали ошибки: " + e.toString());
        }
    }
}