package com.lanmain.passport.data;

import com.lanmain.passport.PlayerPassport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Менеджер для управления паспортами игроков
 */
public class PassportManager {

    private final PlayerPassport plugin;
    private final Map<UUID, Passport> passports;
    private final File dataFolder;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PassportManager(PlayerPassport plugin) {
        this.plugin = plugin;
        this.passports = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "passports");

        // Создаём папку для данных
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // Загружаем все паспорта
        loadAllPassports();
    }

    /**
     * Создать новый паспорт для игрока
     */
    public Passport createPassport(UUID uuid, String playerName) {
        if (hasPassport(uuid)) {
            return getPassport(uuid);
        }

        Passport passport = new Passport(uuid, playerName);
        passports.put(uuid, passport);
        savePassport(passport);

        plugin.getLogger().info("Создан новый паспорт для игрока: " + playerName);
        return passport;
    }

    /**
     * Получить паспорт игрока по UUID
     */
    public Passport getPassport(UUID uuid) {
        return passports.get(uuid);
    }

    /**
     * Получить паспорт игрока по нику
     */
    public Passport getPassportByName(String playerName) {
        return passports.values().stream()
                .filter(p -> p.getOwnerName().equalsIgnoreCase(playerName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Проверить, есть ли паспорт у игрока
     */
    public boolean hasPassport(UUID uuid) {
        return passports.containsKey(uuid);
    }

    /**
     * Удалить паспорт игрока
     */
    public boolean deletePassport(UUID uuid) {
        Passport passport = passports.remove(uuid);
        if (passport != null) {
            File file = getPassportFile(uuid);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * Сохранить паспорт в файл
     */
    public void savePassport(Passport passport) {
        File file = getPassportFile(passport.getOwnerUuid());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("uuid", passport.getOwnerUuid().toString());
        config.set("name", passport.getOwnerName());
        config.set("gender", passport.getGender());
        config.set("height", passport.getHeight());
        config.set("weight", passport.getWeight());
        config.set("race", passport.getRace());
        config.set("role", passport.getRole());
        config.set("created-date", passport.getCreatedDate().format(DATE_FORMAT));
        config.set("last-modified", passport.getLastModified().format(DATE_FORMAT));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Ошибка сохранения паспорта: " + passport.getOwnerName(), e);
        }
    }

    /**
     * Загрузить паспорт из файла
     */
    private Passport loadPassport(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            UUID uuid = UUID.fromString(config.getString("uuid", ""));
            String name = config.getString("name", "Unknown");
            String gender = config.getString("gender", "Не указан");
            String height = config.getString("height", "Не указан");
            String weight = config.getString("weight", "Не указан");
            String race = config.getString("race", "Не указана");
            String role = config.getString("role", "Не указана");

            LocalDateTime createdDate = LocalDateTime.parse(
                    config.getString("created-date", LocalDateTime.now().format(DATE_FORMAT)),
                    DATE_FORMAT
            );
            LocalDateTime lastModified = LocalDateTime.parse(
                    config.getString("last-modified", LocalDateTime.now().format(DATE_FORMAT)),
                    DATE_FORMAT
            );

            return new Passport(uuid, name, gender, height, weight, race, role, createdDate, lastModified);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Ошибка загрузки паспорта из файла: " + file.getName(), e);
            return null;
        }
    }

    /**
     * Загрузить все паспорта из папки
     */
    public void loadAllPassports() {
        passports.clear();

        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        int loaded = 0;
        for (File file : files) {
            Passport passport = loadPassport(file);
            if (passport != null) {
                passports.put(passport.getOwnerUuid(), passport);
                loaded++;
            }
        }

        plugin.getLogger().info("Загружено паспортов: " + loaded);
    }

    /**
     * Сохранить все паспорта
     */
    public void saveAllPassports() {
        for (Passport passport : passports.values()) {
            savePassport(passport);
        }
        plugin.getLogger().info("Сохранено паспортов: " + passports.size());
    }

    /**
     * Получить файл паспорта по UUID
     */
    private File getPassportFile(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }

    /**
     * Получить все паспорта
     */
    public Map<UUID, Passport> getAllPassports() {
        return new HashMap<>(passports);
    }

    /**
     * Получить количество паспортов
     */
    public int getPassportCount() {
        return passports.size();
    }
}
