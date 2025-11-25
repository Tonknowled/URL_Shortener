package ui;

import service.UrlShortenerService;
import service.UserManager;
import core.ShortLink;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleInterface {
    private final UrlShortenerService urlService;
    private final UserManager userManager;
    private final Scanner scanner;
    private UUID currentUserId;

    public ConsoleInterface(UrlShortenerService urlService, UserManager userManager) {
        this.urlService = urlService;
        this.userManager = userManager;
        this.scanner = new Scanner(System.in);
        this.currentUserId = null;
    }

    public void start() {
        initializeUser();
        showWelcomeMessage();

        while (true) {
            showMenu();
            String command = scanner.nextLine().trim();

            if (command.equals("1") || command.equals("create")) {
                createShortUrl();
            } else if (command.equals("2") || command.equals("open")) {
                openShortUrl();
            } else if (command.equals("3") || command.equals("list")) {
                listUserUrls();
            } else if (command.equals("4") || command.equals("edit")) {
                editUrl();
            } else if (command.equals("5") || command.equals("delete")) {
                deleteUrl();
            } else if (command.equals("6") || command.equals("stats")) {
                showStats();
            } else if (command.equals("7") || command.equals("help")) {
                showHelp();
            } else if (command.equals("8") || command.equals("exit") || command.equals("quit")) {
                System.out.println("До свидания!");
                return;
            } else {
                System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            }
        }
    }

    private void initializeUser() {
        String sessionId = "user-session";
        currentUserId = userManager.getOrCreateUserId(sessionId);
        System.out.println("Ваш ID пользователя: " + currentUserId);
    }

    private void showWelcomeMessage() {
        System.out.println("=== Сервис сокращения ссылок ===");
        System.out.println("Добро пожаловать! Ваш ID: " + currentUserId);
    }

    private void showMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. create - Создать короткую ссылку");
        System.out.println("2. open   - Открыть короткую ссылку");
        System.out.println("3. list   - Мои ссылки");
        System.out.println("4. edit   - Изменить лимит переходов");
        System.out.println("5. delete - Удалить ссылку");
        System.out.println("6. stats  - Статистика");
        System.out.println("7. help   - Помощь");
        System.out.println("8. exit   - Выход");
        System.out.print("Выберите команду: ");
    }

    private void createShortUrl() {
        System.out.print("Введите длинный URL: ");
        String longUrl = scanner.nextLine().trim();

        if (longUrl.isEmpty()) {
            System.out.println("URL не может быть пустым!");
            return;
        }

        try {
            String shortUrl = urlService.shortenUrl(longUrl, currentUserId);
            System.out.println("Короткая ссылка создана: " + shortUrl);
            System.out.println("Лимит переходов: 10 (по умолчанию)");
            System.out.println("Время жизни: 24 часа (по умолчанию)");
        } catch (Exception e) {
            System.out.println("Ошибка при создании ссылки: " + e.getMessage());
        }
    }

    private void openShortUrl() {
        System.out.print("Введите короткую ссылку: ");
        String shortUrl = scanner.nextLine().trim();

        try {
            String longUrl = urlService.openUrl(shortUrl);
            System.out.println("Перенаправляем на: " + longUrl);
            BrowserUtil.openUrlInBrowser(longUrl);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void listUserUrls() {
        List<ShortLink> userLinks = urlService.getUserLinks(currentUserId);

        if (userLinks.isEmpty()) {
            System.out.println("У вас нет созданных ссылок.");
            return;
        }

        System.out.println("\n=== Ваши ссылки ===");
        for (int i = 0; i < userLinks.size(); i++) {
            ShortLink link = userLinks.get(i);
            String status = link.canBeAccessed() ? "АКТИВНА" : "НЕАКТИВНА";
            System.out.println((i + 1) + ". " + link.getShortUrl() + " -> " + link.getLongUrl());
            System.out.println("   Переходы: " + link.getClickCount() + "/" + link.getMaxClicks() +
                    ", Статус: " + status + ", Истекает: " + link.getExpiresAt().toString());
        }
    }

    private void editUrl() {
        System.out.print("Введите короткую ссылку для редактирования: ");
        String shortUrl = scanner.nextLine().trim();

        System.out.print("Введите новый лимит переходов: ");
        try {
            int newLimit = Integer.parseInt(scanner.nextLine().trim());

            if (urlService.updateClickLimit(shortUrl, currentUserId, newLimit)) {
                System.out.println("Лимит успешно обновлен!");
            } else {
                System.out.println("Не удалось обновить лимит. Проверьте ссылку и права доступа.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректное число!");
        }
    }

    private void deleteUrl() {
        System.out.print("Введите короткую ссылку для удаления: ");
        String shortUrl = scanner.nextLine().trim();

        if (urlService.deleteLink(shortUrl, currentUserId)) {
            System.out.println("Ссылка успешно удалена!");
        } else {
            System.out.println("Не удалось удалить ссылку. Проверьте ссылку и права доступа.");
        }
    }

    private void showStats() {
        List<ShortLink> userLinks = urlService.getUserLinks(currentUserId);

        if (userLinks.isEmpty()) {
            System.out.println("У вас нет созданных ссылок.");
            return;
        }

        int totalLinks = userLinks.size();
        int activeLinks = 0;
        int totalClicks = 0;

        for (ShortLink link : userLinks) {
            if (link.canBeAccessed()) {
                activeLinks++;
            }
            totalClicks += link.getClickCount();
        }

        System.out.println("\n=== Статистика ===");
        System.out.println("Всего ссылок: " + totalLinks);
        System.out.println("Активных ссылок: " + activeLinks);
        System.out.println("Всего переходов: " + totalClicks);
    }

    private void showHelp() {
        System.out.println("\n=== Помощь ===");
        System.out.println("create - Создать короткую ссылку из длинного URL");
        System.out.println("open   - Открыть короткую ссылку в браузере");
        System.out.println("list   - Показать все ваши ссылки с статистикой");
        System.out.println("edit   - Изменить лимит переходов для ссылки");
        System.out.println("delete - Удалить вашу ссылку");
        System.out.println("stats  - Общая статистика по вашим ссылкам");
        System.out.println("help   - Показать эту справку");
        System.out.println("exit   - Выйти из программы");
    }
}