package com.lanmain.passport.gui;

import com.lanmain.passport.PlayerPassport;
import com.lanmain.passport.data.Passport;
import com.lanmain.passport.listeners.ChatInputListener;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Слушатель событий GUI
 */
public class GuiListener implements Listener {

    private final PlayerPassport plugin;

    // Хранение сессий создания/редактирования паспорта
    private static final Map<UUID, PassportCreateGUI> createSessions = new HashMap<>();

    public GuiListener(PlayerPassport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = getInventoryTitle(event);
        if (title == null) return;

        // Проверяем, это наш GUI
        if (!isPassportGUI(title)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Обработка просмотра паспорта
        if (title.startsWith(PassportGUI.GUI_TITLE_PREFIX)) {
            handlePassportViewClick(player, event.getSlot(), clicked);
            return;
        }

        // Обработка создания/редактирования паспорта
        if (title.startsWith("§6Шаг") || title.equals("§6Подтверждение")) {
            handlePassportCreateClick(player, event.getSlot(), clicked, title);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        
        // Не удаляем сессию при закрытии, если это ввод в чат
        if (ChatInputListener.isAwaitingInput(player)) {
            return;
        }
    }

    /**
     * Обработка клика в GUI просмотра паспорта
     */
    private void handlePassportViewClick(Player player, int slot, ItemStack clicked) {
        Material type = clicked.getType();

        // Кнопка закрытия
        if (slot == 49 && type == Material.BARRIER) {
            player.closeInventory();
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }

        // Кнопка редактирования
        if (slot == 51 && type == Material.WRITABLE_BOOK) {
            Passport passport = plugin.getPassportManager().getPassport(player.getUniqueId());
            if (passport != null) {
                PassportCreateGUI createGUI = new PassportCreateGUI(plugin, player, passport);
                createSessions.put(player.getUniqueId(), createGUI);
                createGUI.open();
            }
        }
    }

    /**
     * Обработка клика в GUI создания паспорта
     */
    private void handlePassportCreateClick(Player player, int slot, ItemStack clicked, String title) {
        PassportCreateGUI session = createSessions.get(player.getUniqueId());
        
        // Если сессии нет, создаём новую
        if (session == null) {
            session = new PassportCreateGUI(plugin, player);
            createSessions.put(player.getUniqueId(), session);
        }

        Material type = clicked.getType();
        PassportCreateGUI.CreationStep step = session.getCurrentStep();

        // Кнопка отмены
        if (type == Material.BARRIER) {
            player.closeInventory();
            createSessions.remove(player.getUniqueId());
            player.sendMessage(plugin.getMessageManager().get("messages.passport-creation-cancelled"));
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }

        // Кнопка назад
        if (type == Material.ARROW && clicked.getItemMeta() != null) {
            String name = getItemName(clicked);
            if (name.contains("Назад")) {
                session.previousStep();
                playSound(player, Sound.UI_BUTTON_CLICK);
                return;
            }
            if (name.contains("Далее")) {
                session.nextStep();
                playSound(player, Sound.UI_BUTTON_CLICK);
                return;
            }
        }

        // Кнопка завершения
        if (type == Material.LIME_DYE) {
            session.showConfirmation();
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }

        // Обработка выбора пола
        if (step == PassportCreateGUI.CreationStep.GENDER) {
            if (type == Material.LIGHT_BLUE_DYE || type == Material.PINK_DYE || type == Material.GRAY_DYE) {
                String gender = extractItemName(clicked);
                session.getPassport().setGender(gender);
                session.showGenderSelection(); // Обновляем GUI
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
            return;
        }

        // Обработка выбора расы
        if (step == PassportCreateGUI.CreationStep.RACE) {
            List<String> races = plugin.getConfig().getStringList("races");
            String name = extractItemName(clicked);
            if (races.contains(name)) {
                session.getPassport().setRace(name);
                session.showRaceSelection(); // Обновляем GUI
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
            return;
        }

        // Обработка выбора роли
        if (step == PassportCreateGUI.CreationStep.ROLE) {
            List<String> roles = plugin.getConfig().getStringList("roles");
            String name = extractItemName(clicked);
            if (roles.contains(name)) {
                session.getPassport().setRole(name);
                session.showRoleSelection(); // Обновляем GUI
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
            return;
        }

        // Обработка подтверждения
        if (step == PassportCreateGUI.CreationStep.CONFIRM) {
            if (type == Material.LIME_WOOL) {
                session.savePassport();
                createSessions.remove(player.getUniqueId());
            } else if (type == Material.RED_WOOL) {
                player.closeInventory();
                createSessions.remove(player.getUniqueId());
                player.sendMessage(plugin.getMessageManager().get("messages.passport-creation-cancelled"));
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
        }
    }

    /**
     * Проверить, является ли это GUI паспорта
     */
    private boolean isPassportGUI(String title) {
        return title.contains("ПАСПОРТ") || 
               title.startsWith("§6Шаг") || 
               title.equals("§6Подтверждение") ||
               title.contains("Выберите");
    }

    /**
     * Получить заголовок инвентаря
     */
    private String getInventoryTitle(InventoryClickEvent event) {
        if (event.getView().title() instanceof net.kyori.adventure.text.TextComponent textComponent) {
            return textComponent.content();
        }
        // Fallback для сериализации
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(event.getView().title());
    }

    /**
     * Получить имя предмета
     */
    private String getItemName(ItemStack item) {
        if (item.getItemMeta() != null && item.getItemMeta().displayName() != null) {
            return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(item.getItemMeta().displayName());
        }
        return "";
    }

    /**
     * Извлечь чистое имя (без цветов и префиксов)
     */
    private String extractItemName(ItemStack item) {
        String name = getItemName(item);
        // Удаляем префиксы типа "✔ "
        name = name.replaceAll("^[§a✔ ]+", "").replaceAll("^[§e]+", "").trim();
        return name;
    }

    /**
     * Воспроизвести звук
     */
    private void playSound(Player player, Sound sound) {
        if (plugin.getConfig().getBoolean("passport.sounds-enabled", true)) {
            player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
        }
    }

    /**
     * Получить сессию создания паспорта
     */
    public static PassportCreateGUI getCreateSession(UUID uuid) {
        return createSessions.get(uuid);
    }

    /**
     * Установить сессию создания паспорта
     */
    public static void setCreateSession(UUID uuid, PassportCreateGUI session) {
        createSessions.put(uuid, session);
    }

    /**
     * Удалить сессию создания паспорта
     */
    public static void removeCreateSession(UUID uuid) {
        createSessions.remove(uuid);
    }
}
