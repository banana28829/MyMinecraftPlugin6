package com.lanmain.passport.utils;

import com.lanmain.passport.PlayerPassport;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер сообщений и локализации
 */
public class MessageManager {

    private final PlayerPassport plugin;
    private FileConfiguration messages;
    private String prefix;

    public MessageManager(PlayerPassport plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * Перезагрузить сообщения
     */
    public void reload() {
        File messagesFile = new File(plugin.getDataFolder(), "messages_ru.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages_ru.yml", false);
        }
        
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
        this.prefix = colorize(messages.getString("prefix", "§6[Паспорт] §r"));
    }

    /**
     * Получить сообщение по ключу
     */
    public String get(String key) {
        String message = messages.getString(key, "§cСообщение не найдено: " + key);
        return colorize(message);
    }

    /**
     * Получить сообщение с префиксом
     */
    public String getWithPrefix(String key) {
        return prefix + get(key);
    }

    /**
     * Получить сообщение с заменой плейсхолдеров
     */
    public String get(String key, String... replacements) {
        String message = get(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }

    /**
     * Получить список сообщений
     */
    public List<String> getList(String key) {
        List<String> list = messages.getStringList(key);
        List<String> colorized = new ArrayList<>();
        for (String line : list) {
            colorized.add(colorize(line));
        }
        return colorized;
    }

    /**
     * Получить префикс
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Применить цветовые коды
     */
    public static String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Получить формат даты
     */
    public String getDateFormat() {
        return messages.getString("date-format", "dd.MM.yyyy HH:mm");
    }
}
