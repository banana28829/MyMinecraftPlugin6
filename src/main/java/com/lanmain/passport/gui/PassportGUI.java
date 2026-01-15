package com.lanmain.passport.gui;

import com.lanmain.passport.PlayerPassport;
import com.lanmain.passport.data.Passport;
import com.lanmain.passport.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI для отображения паспорта игрока
 * Дизайн в виде развёрнутой книги
 */
public class PassportGUI {

    private final PlayerPassport plugin;
    private final Player viewer;
    private final Passport passport;
    private final boolean viewOnly;
    private Inventory inventory;

    public static final String GUI_TITLE_PREFIX = "§6§l✦ ПАСПОРТ: ";

    public PassportGUI(PlayerPassport plugin, Player viewer, Passport passport, boolean viewOnly) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.passport = passport;
        this.viewOnly = viewOnly;
    }

    /**
     * Открыть GUI паспорта
     */
    public void open() {
        String title = GUI_TITLE_PREFIX + passport.getOwnerName() + " §6§l✦";
        inventory = Bukkit.createInventory(null, 54, Component.text(title));

        fillBackground();
        createPassportLayout();

        viewer.openInventory(inventory);
        
        if (plugin.getConfig().getBoolean("passport.sounds-enabled", true)) {
            viewer.playSound(viewer.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
        }
    }

    /**
     * Заполнить фон
     */
    private void fillBackground() {
        ItemStack brownPane = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name(" ").build();
        ItemStack blackPane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();

        // Рамка книги (коричневая)
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, brownPane); // Верхний ряд
            inventory.setItem(45 + i, brownPane); // Нижний ряд
        }
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9, brownPane); // Левый край
            inventory.setItem(i * 9 + 8, brownPane); // Правый край
        }

        // Разделитель посередине (корешок книги)
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9 + 4, blackPane);
        }

        // Внутренние страницы (бежевый/белый фон)
        ItemStack pagePane = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build();
        int[] pageSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39, // Левая страница
                           14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43}; // Правая страница
        for (int slot : pageSlots) {
            inventory.setItem(slot, pagePane);
        }
    }

    /**
     * Создать содержимое паспорта
     */
    private void createPassportLayout() {
        // ===== ЛЕВАЯ СТРАНИЦА (Основная информация) =====

        // Голова игрока (скин)
        ItemStack playerHead = createPlayerHead();
        inventory.setItem(11, playerHead);

        // Ник игрока
        ItemStack nicknameItem = new ItemBuilder(Material.NAME_TAG)
                .name("§6§lНикнейм")
                .lore(
                        "§f" + passport.getOwnerName(),
                        "",
                        "§7Официальное имя владельца"
                )
                .build();
        inventory.setItem(20, nicknameItem);

        // UUID
        ItemStack uuidItem = new ItemBuilder(Material.PAPER)
                .name("§6§lУникальный ID")
                .lore(
                        "§f" + passport.getShortUuid(),
                        "",
                        "§8" + passport.getOwnerUuid().toString()
                )
                .build();
        inventory.setItem(29, uuidItem);

        // Дата создания
        String dateFormat = plugin.getMessageManager().getDateFormat();
        ItemStack dateItem = new ItemBuilder(Material.CLOCK)
                .name("§6§lДата выдачи")
                .lore(
                        "§f" + passport.getFormattedCreatedDate(dateFormat),
                        "",
                        "§7Дата создания паспорта"
                )
                .build();
        inventory.setItem(38, dateItem);

        // ===== ПРАВАЯ СТРАНИЦА (RP-информация) =====

        // Пол
        Material genderMaterial = getGenderMaterial(passport.getGender());
        ItemStack genderItem = new ItemBuilder(genderMaterial)
                .name("§6§lПол")
                .lore(
                        "§f" + passport.getGender(),
                        "",
                        viewOnly ? "" : "§eНажмите для изменения"
                )
                .build();
        inventory.setItem(14, genderItem);

        // Рост
        ItemStack heightItem = new ItemBuilder(Material.ARROW)
                .name("§6§lРост")
                .lore(
                        "§f" + passport.getHeight(),
                        "",
                        viewOnly ? "" : "§eНажмите для изменения"
                )
                .build();
        inventory.setItem(15, heightItem);

        // Вес
        ItemStack weightItem = new ItemBuilder(Material.IRON_INGOT)
                .name("§6§lВес")
                .lore(
                        "§f" + passport.getWeight(),
                        "",
                        viewOnly ? "" : "§eНажмите для изменения"
                )
                .build();
        inventory.setItem(16, weightItem);

        // Раса
        ItemStack raceItem = new ItemBuilder(Material.TOTEM_OF_UNDYING)
                .name("§6§lРаса")
                .lore(
                        "§f" + passport.getRace(),
                        "",
                        viewOnly ? "" : "§eНажмите для изменения"
                )
                .build();
        inventory.setItem(23, raceItem);

        // Роль/Класс
        ItemStack roleItem = new ItemBuilder(Material.DIAMOND_SWORD)
                .name("§6§lРоль/Класс")
                .lore(
                        "§f" + passport.getRole(),
                        "",
                        viewOnly ? "" : "§eНажмите для изменения"
                )
                .glow()
                .build();
        inventory.setItem(24, roleItem);

        // ===== КНОПКИ УПРАВЛЕНИЯ =====

        // Кнопка закрытия
        ItemStack closeButton = new ItemBuilder(Material.BARRIER)
                .name("§c§l✖ Закрыть")
                .lore("§7Нажмите для закрытия паспорта")
                .build();
        inventory.setItem(49, closeButton);

        // Кнопка редактирования (если это свой паспорт)
        if (!viewOnly && passport.getOwnerUuid().equals(viewer.getUniqueId())) {
            ItemStack editButton = new ItemBuilder(Material.WRITABLE_BOOK)
                    .name("§e§l✎ Редактировать")
                    .lore("§7Нажмите для редактирования")
                    .build();
            inventory.setItem(51, editButton);
        }

        // Статус заполненности
        if (passport.isComplete()) {
            ItemStack completeItem = new ItemBuilder(Material.EMERALD)
                    .name("§a§l✔ Паспорт заполнен")
                    .lore("§7Все поля заполнены")
                    .glow()
                    .build();
            inventory.setItem(47, completeItem);
        } else {
            ItemStack incompleteItem = new ItemBuilder(Material.REDSTONE)
                    .name("§c§l✖ Паспорт не полный")
                    .lore("§7Заполните все поля")
                    .build();
            inventory.setItem(47, incompleteItem);
        }
    }

    /**
     * Создать голову игрока со скином
     */
    private ItemStack createPlayerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            // Устанавливаем владельца головы
            Player owner = Bukkit.getPlayer(passport.getOwnerUuid());
            if (owner != null) {
                meta.setOwningPlayer(owner);
            } else {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(passport.getOwnerUuid()));
            }

            meta.displayName(Component.text("§6§lФото владельца")
                    .decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§f" + passport.getOwnerName())
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("§7Внешний вид игрока")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);

            head.setItemMeta(meta);
        }

        return head;
    }

    /**
     * Получить материал для иконки пола
     */
    private Material getGenderMaterial(String gender) {
        return switch (gender.toLowerCase()) {
            case "мужской" -> Material.LIGHT_BLUE_DYE;
            case "женский" -> Material.PINK_DYE;
            default -> Material.GRAY_DYE;
        };
    }

    /**
     * Получить паспорт
     */
    public Passport getPassport() {
        return passport;
    }

    /**
     * Проверить, только просмотр
     */
    public boolean isViewOnly() {
        return viewOnly;
    }
}
