# PC Creator Simulator

Симулятор сборщика ПК на **Kotlin Multiplatform + Compose Multiplatform**.
Один и тот же код собирается в **Android APK**, **Windows EXE** и **Linux binary**.

## Геймплей

- Выбор комплектующих по 6 категориям: CPU, материнская плата, ОЗУ, видеокарта, накопитель, блок питания.
- Проверка совместимости: сокет CPU ↔ плата, DDR-тип памяти ↔ плата, мощность БП ≥ потребления.
- Цикл «собрал → продал под заказ дня», прибыль, репутация и аренда −40 ₽/день.
- Репутация открывает дорогие детали (RTX 4090, DDR5, i9-13900K).

## Сборка

```bash
gradle :composeApp:run                 # desktop-версия
gradle :composeApp:assembleDebug       # APK -> composeApp/build/outputs/apk/debug/
gradle :composeApp:createDistributable # EXE (Windows) / binary (Linux)
gradle :composeApp:packageMsi          # MSI (Windows, нужен WiX)
gradle :composeApp:packageDeb          # DEB (Linux)
```

CI использует `gradle/actions/setup-gradle@v4` с Gradle 8.9.

## CI

`.github/workflows/build.yml` — три параллельные джобы:

| Джоба | Артефакт |
|-------|----------|
| Android APK (`assembleDebug`) | `pcsim-android-apk` |
| Windows EXE (`createDistributable`) | `pcsim-windows-exe` |
| Linux binary (`createDistributable`) | `pcsim-linux-binary` |

Пуш тега `v1.0.0` создаёт GitHub Release со всеми тремя файлами.

## Стек

Kotlin 2.0.21 · Compose Multiplatform 1.7.1 · AGP 8.5.2 · Gradle 8.9 · JDK 17

## Godot-эксперимент

В корне остались файлы Godot-прототипа (`project.godot`, `Main.tscn`, `scripts/`, `export_presets.cfg`).
CI их **не собирает**: экспорт APK через `barichello/godot-ci:4.3` падал на конфигурации Android-пресета.
Файлы оставлены как история; активная сборка — Kotlin.
