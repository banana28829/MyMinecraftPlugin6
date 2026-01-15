package com.lanmain.passport.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилиты для создания предметов в GUI
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    /**
     * Установить название предмета
     */
    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.displayName(Component.text(MessageManager.colorize(name))
                    .decoration(TextDecoration.ITALIC, false));
        }
        return this;
    }

    /**
     * Установить название с компонентом
     */
    public ItemBuilder name(Component component) {
        if (meta != null) {
            meta.displayName(component.decoration(TextDecoration.ITALIC, false));
        }
        return this;
    }

    /**
     * Добавить строку описания
     */
    public ItemBuilder lore(String... lines) {
        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            for (String line : lines) {
                lore.add(Component.text(MessageManager.colorize(line))
                        .decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(lore);
        }
        return this;
    }

    /**
     * Добавить список описания
     */
    public ItemBuilder lore(List<String> lines) {
        if (meta != null) {
            List<Component> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(Component.text(MessageManager.colorize(line))
                        .decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(lore);
        }
        return this;
    }

    /**
     * Установить количество
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Добавить свечение (enchant glow)
     */
    public ItemBuilder glow() {
        if (meta != null) {
            meta.setEnchantmentGlintOverride(true);
        }
        return this;
    }

    /**
     * Скрыть атрибуты
     */
    public ItemBuilder hideAttributes() {
        if (meta != null) {
            meta.setHideTooltip(false);
        }
        return this;
    }

    /**
     * Установить кастомный model data
     */
    public ItemBuilder customModelData(int data) {
        if (meta != null) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    /**
     * Построить предмет
     */
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Создать пустую панель
     */
    public static ItemStack createPane(Material material) {
        return new ItemBuilder(material)
                .name(" ")
                .build();
    }

    /**
     * Создать кнопку закрытия
     */
    public static ItemStack createCloseButton() {
        return new ItemBuilder(Material.BARRIER)
                .name("§c✖ Закрыть")
                .lore("§7Нажмите для закрытия")
                .build();
    }

    /**
     * Создать кнопку подтверждения
     */
    public static ItemStack createConfirmButton() {
        return new ItemBuilder(Material.LIME_WOOL)
                .name("§a✔ Подтвердить")
                .lore("§7Нажмите для подтверждения")
                .build();
    }

    /**
     * Создать кнопку отмены
     */
    public static ItemStack createCancelButton() {
        return new ItemBuilder(Material.RED_WOOL)
                .name("§c✖ Отмена")
                .lore("§7Нажмите для отмены")
                .build();
    }
}
