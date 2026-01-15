# PlayerPassport

Плагин паспорта игрока для Minecraft RP серверов на Paper 1.21+

## Возможности

- Красивый GUI в виде развёрнутой книги
- Отображение скина игрока (голова)
- RP-поля: пол, рост, вес, раса, роль
- Сохранение данных в YAML-файлы
- Гибкая система прав
- Полная локализация на русский язык

## Требования

- Paper 1.21+ (или совместимые форки)
- Java 21+

## Сборка

```bash
./gradlew build
```

JAR файл появится в `build/libs/PlayerPassport-1.0.0.jar`

## Команды

| Команда | Описание | Право |
|---------|----------|-------|
| `/passport` | Открыть свой паспорт | `passport.use` |
| `/passport create` | Создать паспорт | `passport.create` |
| `/passport <ник>` | Посмотреть паспорт игрока | `passport.view.others` |
| `/passport edit` | Редактировать свой паспорт | `passport.use` |
| `/passport delete [ник]` | Удалить паспорт | `passport.admin.delete` |
| `/passport reload` | Перезагрузить конфиг | `passport.admin` |

## Права

| Право | Описание | По умолчанию |
|-------|----------|--------------|
| `passport.use` | Просмотр своего паспорта | Все |
| `passport.create` | Создание паспорта | Все |
| `passport.view.others` | Просмотр паспортов других | Все |
| `passport.admin` | Административные команды | OP |
| `passport.admin.delete` | Удаление паспортов | OP |

## Лицензия

MIT License

## Автор

LanMain
