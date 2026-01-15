package com.lanmain.passport.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Класс, представляющий паспорт игрока
 */
public class Passport {

    private final UUID ownerUuid;
    private String ownerName;
    private String gender;
    private String height;
    private String weight;
    private String race;
    private String role;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

    /**
     * Конструктор для создания нового паспорта
     */
    public Passport(UUID ownerUuid, String ownerName) {
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.gender = "Не указан";
        this.height = "Не указан";
        this.weight = "Не указан";
        this.race = "Не указана";
        this.role = "Не указана";
    }

    /**
     * Конструктор для загрузки существующего паспорта
     */
    public Passport(UUID ownerUuid, String ownerName, String gender, 
                    String height, String weight, String race, String role,
                    LocalDateTime createdDate, LocalDateTime lastModified) {
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.race = race;
        this.role = role;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }

    // Геттеры и сеттеры

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        updateLastModified();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        updateLastModified();
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
        updateLastModified();
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
        updateLastModified();
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
        updateLastModified();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        updateLastModified();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Получить отформатированную дату создания
     */
    public String getFormattedCreatedDate(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return createdDate.format(formatter);
    }

    /**
     * Получить короткий UUID (первые 8 символов)
     */
    public String getShortUuid() {
        return ownerUuid.toString().substring(0, 8) + "...";
    }

    /**
     * Проверить, заполнен ли паспорт полностью
     */
    public boolean isComplete() {
        return !gender.equals("Не указан") &&
               !height.equals("Не указан") &&
               !weight.equals("Не указан") &&
               !race.equals("Не указана") &&
               !role.equals("Не указана");
    }

    @Override
    public String toString() {
        return "Passport{" +
                "ownerUuid=" + ownerUuid +
                ", ownerName='" + ownerName + '\'' +
                ", gender='" + gender + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", race='" + race + '\'' +
                ", role='" + role + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
