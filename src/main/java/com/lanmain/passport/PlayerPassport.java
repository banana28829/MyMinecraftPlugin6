package com.lanmain.passport;

import com.lanmain.passport.commands.PassportCommand;
import com.lanmain.passport.commands.PassportTabCompleter;
import com.lanmain.passport.data.PassportManager;
import com.lanmain.passport.gui.GuiListener;
import com.lanmain.passport.listeners.ChatInputListener;
import com.lanmain.passport.utils.MessageManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Главный класс плагина PlayerPassport
 * Плагин паспорта игрока для RP серверов
 */
public class PlayerPassport extends JavaPlugin {

    private static PlayerPassport instance;
    private PassportManager passportManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;

        // Сохраняем конфиги по умолчанию
        saveDefaultConfig();
        saveResource("messages_ru.yml", false);

        // Инициализация менеджеров
        this.messageManager = new MessageManager(this);
        this.passportManager = new PassportManager(this);

        // Регистрация команд
        registerCommands();

        // Регистрация слушателей
        registerListeners();

        getLogger().info("§a[PlayerPassport] Плагин успешно загружен!");
        getLogger().info("§a[PlayerPassport] Версия: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // Сохраняем все данные
        if (passportManager != null) {
            passportManager.saveAllPassports();
        }

        getLogger().info("§c[PlayerPassport] Плагин выключен!");
    }

    /**
     * Регистрация команд плагина
     */
    private void registerCommands() {
        PluginCommand passportCmd = getCommand("passport");
        if (passportCmd != null) {
            PassportCommand executor = new PassportCommand(this);
            passportCmd.setExecutor(executor);
            passportCmd.setTabCompleter(new PassportTabCompleter(this));
        }
    }

    /**
     * Регистрация слушателей событий
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this), this);
    }

    /**
     * Перезагрузка конфигурации плагина
     */
    public void reloadPluginConfig() {
        reloadConfig();
        messageManager.reload();
        getLogger().info("§a[PlayerPassport] Конфигурация перезагружена!");
    }

    /**
     * Получить экземпляр плагина
     */
    public static PlayerPassport getInstance() {
        return instance;
    }

    /**
     * Получить менеджер паспортов
     */
    public PassportManager getPassportManager() {
        return passportManager;
    }

    /**
     * Получить менеджер сообщений
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }
}
