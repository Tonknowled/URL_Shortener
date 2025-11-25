package main.java;

import service.UrlShortenerService;
import service.UserManager;
import ui.ConsoleInterface;

public class Main {
    public static void main(String[] args) {
        try {
            // Настройки приложения
            String baseUrl = "clck.ru";
            int defaultMaxClicks = 10;
            int defaultHoursToLive = 24;

            // Создаем сервисы
            UrlShortenerService urlService = new UrlShortenerService(
                    baseUrl, defaultMaxClicks, defaultHoursToLive);
            UserManager userManager = new UserManager();

            // Запускаем консольный интерфейс
            ConsoleInterface console = new ConsoleInterface(urlService, userManager);
            console.start();

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}