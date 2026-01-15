package com.lanmain.passport.listeners;

import com.lanmain.passport.PlayerPassport;
import com.lanmain.passport.gui.GuiListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Слушатель для ввода данных через чат
 */
public class ChatInputListener implements Listener {

    private final PlayerPassport plugin;

    // Ожидающие ввода игроки
    private static final Map<UUID, Consumer<String>> pendingInputs = new HashMap<>();

    public ChatInputListener(PlayerPassport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Проверяем, ожидает ли игрок ввода
        Consumer<String> callback = pendingInputs.remove(uuid);
        if (callback != null) {
            event.setCancelled(true);
            String input = event.getMessage().trim();

            // Выполняем callback в главном потоке
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                callback.accept(input);
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        pendingInputs.remove(uuid);
        GuiListener.removeCreateSession(uuid);
    }

    /**
     * Ожидать ввод от игрока
     */
    public static void awaitInput(Player player, Consumer<String> callback) {
        pendingInputs.put(player.getUniqueId(), callback);
    }

    /**
     * Проверить, ожидает ли игрок ввода
     */
    public static boolean isAwaitingInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }

    /**
     * Отменить ожидание ввода
     */
    public static void cancelInput(Player player) {
        pendingInputs.remove(player.getUniqueId());
    }
}
