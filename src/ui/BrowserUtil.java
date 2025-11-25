package ui;

import java.awt.Desktop;
import java.net.URI;

public class BrowserUtil {
    public static void openUrlInBrowser(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Desktop.getDesktop().browse(new URI(url));
            System.out.println("Открываем в браузере: " + url);
        } catch (Exception e) {
            System.err.println("Ошибка открытия URL в браузере: " + e.getMessage());
            System.out.println("Вы можете открыть ссылку вручную: " + url);
        }
    }
}