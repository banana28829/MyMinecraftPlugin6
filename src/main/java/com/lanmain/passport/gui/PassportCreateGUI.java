package com.lanmain.passport.gui;

import com.lanmain.passport.PlayerPassport;
import com.lanmain.passport.data.Passport;
import com.lanmain.passport.listeners.ChatInputListener;
import com.lanmain.passport.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * GUI для создания/редактирования паспорта
 */
public class PassportCreateGUI {

    private final PlayerPassport plugin;
    private final Player player;
    private Passport passport;
    private final boolean isEditing;
    private Inventory inventory;

    // Текущий шаг создания
    private CreationStep currentStep = CreationStep.GENDER;

    public static final String GUI_TITLE_CREATE = "§6§l✦ СОЗДАНИЕ ПАСПОРТА ✦";
    public static final String GUI_TITLE_EDIT = "§6§l✦ РЕДАКТИРОВАНИЕ ✦";

    public enum CreationStep {
        GENDER("Выберите пол"),
        HEIGHT("Введите рост"),
        WEIGHT("Введите вес"),
        RACE("Выберите расу"),
        ROLE("Выберите роль"),
        CONFIRM("Подтверждение");

        private final String title;

        CreationStep(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public PassportCreateGUI(PlayerPassport plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.isEditing = false;
        this.passport = new Passport(player.getUniqueId(), player.getName());
    }

    public PassportCreateGUI(PlayerPassport plugin, Player player, Passport passport) {
        this.plugin = plugin;
        this.player = player;
        this.isEditing = true;
        this.passport = passport;
    }

    /**
     * Открыть GUI
     */
    public void open() {
        showGenderSelection();
    }

    /**
     * Показать выбор пола
     */
    public void showGenderSelection() {
        currentStep = CreationStep.GENDER;
        String title = "§6Шаг 1: Выберите пол";
        inventory = Bukkit.createInventory(null, 27, Component.text(title));

        fillBackground(inventory);

        List<String> genders = plugin.getConfig().getStringList("genders");
        int startSlot = 10;

        for (int i = 0; i < genders.size() && i < 7; i++) {
            String gender = genders.get(i);
            Material material = getGenderMaterial(gender);
            boolean selected = passport.getGender().equals(gender);

            ItemStack item = new ItemBuilder(material)
                    .name((selected ? "§a✔ " : "§e") + gender)
                    .lore(
                            "",
                            selected ? "§a▶ Выбрано" : "§7Нажмите для выбора"
                    )
                    .build();

            if (selected) {
                item = new ItemBuilder(item).glow().build();
            }

            inventory.setItem(startSlot + i, item);
        }

        addNavigationButtons(inventory, false, true);
        player.openInventory(inventory);
        playSound();
    }

    /**
     * Показать выбор расы
     */
    public void showRaceSelection() {
        currentStep = CreationStep.RACE;
        String title = "§6Шаг 4: Выберите расу";
        inventory = Bukkit.createInventory(null, 36, Component.text(title));

        fillBackground(inventory);

        List<String> races = plugin.getConfig().getStringList("races");
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

        for (int i = 0; i < races.size() && i < slots.length; i++) {
            String race = races.get(i);
            boolean selected = passport.getRace().equals(race);

            ItemStack item = new ItemBuilder(getRaceMaterial(race))
                    .name((selected ? "§a✔ " : "§e") + race)
                    .lore(
                            "",
                            selected ? "§a▶ Выбрано" : "§7Нажмите для выбора"
                    )
                    .build();

            if (selected) {
                item = new ItemBuilder(item).glow().build();
            }

            inventory.setItem(slots[i], item);
        }

        addNavigationButtons(inventory, true, true);
        player.openInventory(inventory);
        playSound();
    }

    /**
     * Показать выбор роли
     */
    public void showRoleSelection() {
        currentStep = CreationStep.ROLE;
        String title = "§6Шаг 5: Выберите роль";
        inventory = Bukkit.createInventory(null, 36, Component.text(title));

        fillBackground(inventory);

        List<String> roles = plugin.getConfig().getStringList("roles");
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

        for (int i = 0; i < roles.size() && i < slots.length; i++) {
            String role = roles.get(i);
            boolean selected = passport.getRole().equals(role);

            ItemStack item = new ItemBuilder(getRoleMaterial(role))
                    .name((selected ? "§a✔ " : "§e") + role)
                    .lore(
                            "",
                            selected ? "§a▶ Выбрано" : "§7Нажмите для выбора"
                    )
                    .build();

            if (selected) {
                item = new ItemBuilder(item).glow().build();
            }

            inventory.setItem(slots[i], item);
        }

        addNavigationButtons(inventory, true, true);
        player.openInventory(inventory);
        playSound();
    }

    /**
     * Показать экран подтверждения
     */
    public void showConfirmation() {
        currentStep = CreationStep.CONFIRM;
        String title = "§6Подтверждение";
        inventory = Bukkit.createInventory(null, 45, Component.text(title));

        fillBackground(inventory);

        // Показываем сводку
        inventory.setItem(11, new ItemBuilder(getGenderMaterial(passport.getGender()))
                .name("§6Пол: §f" + passport.getGender())
                .build());

        inventory.setItem(12, new ItemBuilder(Material.ARROW)
                .name("§6Рост: §f" + passport.getHeight())
                .build());

        inventory.setItem(13, new ItemBuilder(Material.IRON_INGOT)
                .name("§6Вес: §f" + passport.getWeight())
                .build());

        inventory.setItem(14, new ItemBuilder(getRaceMaterial(passport.getRace()))
                .name("§6Раса: §f" + passport.getRace())
                .build());

        inventory.setItem(15, new ItemBuilder(getRoleMaterial(passport.getRole()))
                .name("§6Роль: §f" + passport.getRole())
                .build());

        // Кнопка подтверждения
        inventory.setItem(30, new ItemBuilder(Material.LIME_WOOL)
                .name("§a§l✔ Подтвердить")
                .lore("§7Нажмите для сохранения паспорта")
                .build());

        // Кнопка отмены
        inventory.setItem(32, new ItemBuilder(Material.RED_WOOL)
                .name("§c§l✖ Отмена")
                .lore("§7Нажмите для отмены")
                .build());

        // Кнопка назад
        inventory.setItem(36, new ItemBuilder(Material.ARROW)
                .name("§7◄ Назад")
                .lore("§7Вернуться к редактированию")
                .build());

        player.openInventory(inventory);
        playSound();
    }

    /**
     * Заполнить фон
     */
    private void fillBackground(Inventory inv) {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
    }

    /**
     * Добавить кнопки навигации
     */
    private void addNavigationButtons(Inventory inv, boolean showBack, boolean showNext) {
        int size = inv.getSize();

        if (showBack) {
            inv.setItem(size - 9, new ItemBuilder(Material.ARROW)
                    .name("§7◄ Назад")
                    .build());
        }

        // Кнопка отмены
        inv.setItem(size - 5, new ItemBuilder(Material.BARRIER)
                .name("§c✖ Отмена")
                .build());

        if (showNext && currentStep != CreationStep.ROLE) {
            inv.setItem(size - 1, new ItemBuilder(Material.ARROW)
                    .name("§7Далее ►")
                    .build());
        } else if (currentStep == CreationStep.ROLE) {
            inv.setItem(size - 1, new ItemBuilder(Material.LIME_DYE)
                    .name("§aЗавершить ►")
                    .build());
        }
    }

    /**
     * Получить материал для пола
     */
    private Material getGenderMaterial(String gender) {
        return switch (gender.toLowerCase()) {
            case "мужской" -> Material.LIGHT_BLUE_DYE;
            case "женский" -> Material.PINK_DYE;
            default -> Material.GRAY_DYE;
        };
    }

    /**
     * Получить материал для расы
     */
    private Material getRaceMaterial(String race) {
        return switch (race.toLowerCase()) {
            case "человек" -> Material.PLAYER_HEAD;
            case "эльф" -> Material.FEATHER;
            case "гном", "дворф" -> Material.IRON_PICKAXE;
            case "орк" -> Material.IRON_AXE;
            case "полуэльф" -> Material.GOLDEN_APPLE;
            default -> Material.TOTEM_OF_UNDYING;
        };
    }

    /**
     * Получить материал для роли
     */
    private Material getRoleMaterial(String role) {
        return switch (role.toLowerCase()) {
            case "воин" -> Material.DIAMOND_SWORD;
            case "маг" -> Material.BLAZE_ROD;
            case "торговец" -> Material.EMERALD;
            case "ремесленник" -> Material.CRAFTING_TABLE;
            case "охотник" -> Material.BOW;
            case "целитель" -> Material.GOLDEN_APPLE;
            case "странник" -> Material.COMPASS;
            default -> Material.NETHER_STAR;
        };
    }

    /**
     * Воспроизвести звук
     */
    private void playSound() {
        if (plugin.getConfig().getBoolean("passport.sounds-enabled", true)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        }
    }

    // Геттеры и сеттеры

    public CreationStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(CreationStep step) {
        this.currentStep = step;
    }

    public Passport getPassport() {
        return passport;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void nextStep() {
        switch (currentStep) {
            case GENDER -> {
                currentStep = CreationStep.HEIGHT;
                requestHeightInput();
            }
            case HEIGHT -> {
                currentStep = CreationStep.WEIGHT;
                requestWeightInput();
            }
            case WEIGHT -> showRaceSelection();
            case RACE -> showRoleSelection();
            case ROLE -> showConfirmation();
            case CONFIRM -> savePassport();
        }
    }

    public void previousStep() {
        switch (currentStep) {
            case HEIGHT -> showGenderSelection();
            case WEIGHT -> {
                currentStep = CreationStep.HEIGHT;
                requestHeightInput();
            }
            case RACE -> {
                currentStep = CreationStep.WEIGHT;
                requestWeightInput();
            }
            case ROLE -> showRaceSelection();
            case CONFIRM -> showRoleSelection();
            default -> {}
        }
    }

    /**
     * Запросить ввод роста в чат
     */
    public void requestHeightInput() {
        player.closeInventory();
        player.sendMessage(plugin.getMessageManager().get("messages.enter-height"));
        player.sendMessage("§7Напишите в чат значение или §cотмена §7для отмены");

        ChatInputListener.awaitInput(player, input -> {
            if (input.equalsIgnoreCase("отмена") || input.equalsIgnoreCase("cancel")) {
                player.sendMessage(plugin.getMessageManager().get("messages.input-cancelled"));
                showGenderSelection();
                return;
            }
            passport.setHeight(input);
            nextStep();
        });
    }

    /**
     * Запросить ввод веса в чат
     */
    public void requestWeightInput() {
        player.closeInventory();
        player.sendMessage(plugin.getMessageManager().get("messages.enter-weight"));
        player.sendMessage("§7Напишите в чат значение или §cотмена §7для отмены");

        ChatInputListener.awaitInput(player, input -> {
            if (input.equalsIgnoreCase("отмена") || input.equalsIgnoreCase("cancel")) {
                player.sendMessage(plugin.getMessageManager().get("messages.input-cancelled"));
                currentStep = CreationStep.HEIGHT;
                requestHeightInput();
                return;
            }
            passport.setWeight(input);
            nextStep();
        });
    }

    /**
     * Сохранить паспорт
     */
    public void savePassport() {
        player.closeInventory();

        if (isEditing) {
            plugin.getPassportManager().savePassport(passport);
            player.sendMessage(plugin.getMessageManager().get("messages.passport-updated"));
        } else {
            plugin.getPassportManager().createPassport(player.getUniqueId(), player.getName());
            Passport created = plugin.getPassportManager().getPassport(player.getUniqueId());
            if (created != null) {
                created.setGender(passport.getGender());
                created.setHeight(passport.getHeight());
                created.setWeight(passport.getWeight());
                created.setRace(passport.getRace());
                created.setRole(passport.getRole());
                plugin.getPassportManager().savePassport(created);
            }
            player.sendMessage(plugin.getMessageManager().get("messages.passport-created"));
        }

        if (plugin.getConfig().getBoolean("passport.sounds-enabled", true)) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }
}
