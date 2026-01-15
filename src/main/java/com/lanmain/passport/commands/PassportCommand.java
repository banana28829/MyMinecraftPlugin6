package com.lanmain.passport.commands;

import com.lanmain.passport.PlayerPassport;
import com.lanmain.passport.data.Passport;
import com.lanmain.passport.gui.PassportGUI;
import com.lanmain.passport.gui.PassportCreateGUI;
import com.lanmain.passport.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Обработчик команды /passport
 */
public class PassportCommand implements CommandExecutor {

    private final PlayerPassport plugin;
    private final MessageManager messages;

    public PassportCommand(PlayerPassport plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {

        // Проверка, что команду вызвал игрок
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.get("messages.player-only"));
            return true;
        }

        // Без аргументов - открыть свой паспорт
        if (args.length == 0) {
            return openOwnPassport(player);
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "create" -> createPassport(player);
            case "edit" -> editPassport(player);
            case "delete" -> deletePassport(player, args);
            case "reload" -> reloadConfig(player);
            case "help" -> showHelp(player);
            default -> viewOtherPassport(player, args[0]);
        };
    }

    /**
     * Открыть свой паспорт
     */
    private boolean openOwnPassport(Player player) {
        if (!player.hasPermission("passport.use")) {
            player.sendMessage(messages.get("messages.no-permission"));
            return true;
        }

        Passport passport = plugin.getPassportManager().getPassport(player.getUniqueId());
        
        if (passport == null) {
            player.sendMessage(messages.get("messages.your-passport-not-found"));
            return true;
        }

        // Обновляем ник на случай, если он изменился
        if (!passport.getOwnerName().equals(player.getName())) {
            passport.setOwnerName(player.getName());
            plugin.getPassportManager().savePassport(passport);
        }

        // Открываем GUI паспорта
        new PassportGUI(plugin, player, passport, false).open();
        return true;
    }

    /**
     * Создать паспорт
     */
    private boolean createPassport(Player player) {
        if (!player.hasPermission("passport.create")) {
            player.sendMessage(messages.get("messages.no-permission"));
            return true;
        }

        if (plugin.getPassportManager().hasPassport(player.getUniqueId())) {
            player.sendMessage(messages.get("messages.passport-already-exists"));
            return true;
        }

        // Открываем GUI создания паспорта
        new PassportCreateGUI(plugin, player).open();
        return true;
    }

    /**
     * Редактировать свой паспорт
     */
    private boolean editPassport(Player player) {
        if (!player.hasPermission("passport.use")) {
            player.sendMessage(messages.get("messages.no-permission"));
            return true;
        }

        Passport passport = plugin.getPassportManager().getPassport(player.getUniqueId());
        
        if (passport == null) {
            player.sendMessage(messages.get("messages.your-passport-not-found"));
            return true;
        }

        // Открываем GUI редактирования
        new PassportCreateGUI(plugin, player, passport).open();
        return true;
    }

    /**
     * Удалить паспорт
     */
    private boolean deletePassport(Player player, String[] args) {
        if (!player.hasPermission("passport.admin.delete")) {
            player.sendMessage(messages.get("messages.no-permission"));
            return true;
        }

        // Если указан ник - удаляем чужой паспорт
        if (args.length > 1) {
            Passport passport = plugin.getPassportManager().getPassportByName(args[1]);
            if (passport == null) {
                player.sendMessage(messages.get("messages.passport-not-found"));
                return true;
            }
            plugin.getPassportManager().deletePassport(passport.getOwnerUuid());
            player.sendMessage(messages.get("messages.passport-deleted"));
            return true;
        }

        // Иначе удаляем свой
        if (plugin.getPassportManager().deletePassport(player.getUniqueId())) {
            player.sendMessage(messages.get("messages.passport-deleted"));
        } else {
            player.sendMessage(messages.get("messages.your-passport-not-found"));
        }
        return true;
    }

    /**
     * Перезагрузить конфигурацию
     */
    private boolean reloadConfig(Player player) {
        if (!player.hasPermission("passport.admin")) {
            player.sendMessage(messages.get("messages.no-permission"));
            return true;
        }

        plugin.reloadPluginConfig();
        player.sendMessage(messages.getPrefix() + "§aКонфигурация перезагружена!");
        return true;
    }

    /**
     * Показать справку
     */
    private boolean showHelp(Player player) {
        player.sendMessage(messages.get("help.header"));
        for (String line : messages.getList("help.commands")) {
            player.sendMessage(line);
        }
        return true;
    }

    /**
     * Посмотреть паспорт другого игрока
     */
    private boolean viewOtherPassport(Player player, String targetName) {
        if (!player.hasPermission("passport.view.others")) {
            player.sendMessage(messages.get("messages.no-permission"));
            return true;
        }

        // Ищем паспорт по нику
        Passport passport = plugin.getPassportManager().getPassportByName(targetName);

        if (passport == null) {
            // Пробуем найти онлайн игрока
            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                passport = plugin.getPassportManager().getPassport(target.getUniqueId());
            }
        }

        if (passport == null) {
            player.sendMessage(messages.get("messages.passport-not-found"));
            return true;
        }

        // Открываем GUI паспорта (только просмотр)
        new PassportGUI(plugin, player, passport, true).open();
        return true;
    }
}
