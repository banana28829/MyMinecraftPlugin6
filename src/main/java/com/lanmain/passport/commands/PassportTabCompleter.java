package com.lanmain.passport.commands;

import com.lanmain.passport.PlayerPassport;
import com.lanmain.passport.data.Passport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab-комплитер для команды /passport
 */
public class PassportTabCompleter implements TabCompleter {

    private final PlayerPassport plugin;
    private final List<String> subCommands = Arrays.asList("create", "edit", "delete", "reload", "help");

    public PassportTabCompleter(PlayerPassport plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                                 @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // Добавляем подкоманды
            for (String sub : subCommands) {
                if (sub.startsWith(input)) {
                    // Проверяем права на команду
                    if (hasPermissionForSubCommand(sender, sub)) {
                        completions.add(sub);
                    }
                }
            }

            // Добавляем имена игроков с паспортами
            if (sender.hasPermission("passport.view.others")) {
                for (Passport passport : plugin.getPassportManager().getAllPassports().values()) {
                    if (passport.getOwnerName().toLowerCase().startsWith(input)) {
                        completions.add(passport.getOwnerName());
                    }
                }

                // Также добавляем онлайн игроков
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input) && 
                        !completions.contains(player.getName())) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            // Для delete показываем имена игроков с паспортами
            if (subCommand.equals("delete") && sender.hasPermission("passport.admin.delete")) {
                completions = plugin.getPassportManager().getAllPassports().values().stream()
                        .map(Passport::getOwnerName)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }

    /**
     * Проверить права на подкоманду
     */
    private boolean hasPermissionForSubCommand(CommandSender sender, String subCommand) {
        return switch (subCommand) {
            case "create" -> sender.hasPermission("passport.create");
            case "edit" -> sender.hasPermission("passport.use");
            case "delete" -> sender.hasPermission("passport.admin.delete");
            case "reload" -> sender.hasPermission("passport.admin");
            case "help" -> true;
            default -> false;
        };
    }
}
