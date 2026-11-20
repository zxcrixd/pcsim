# PC Creator Simulator

Мини-симулятор сборщика ПК на Kotlin Multiplatform + Compose Multiplatform.
Один и тот же код собирается в **Android APK**, **Windows EXE** и **Linux binary**.

## Геймплей

- Выбираешь комплектующие по 7 категориям (CPU, плата, ОЗУ, GPU, накопитель, БП, корпус).
- Игра проверяет совместимость: сокет CPU ↔ плата, тип памяти ↔ плата, мощность БП ≥ потребления.
- Собираешь ПК → продаёшь его под заказ дня, получаешь прибыль и репутацию.
- Репутация открывает дорогие детали (RTX 4090, DDR5, i9) и поднимает бонусы за заказы.

## Сборка

`setup-gradle` ставит Gradle 8.9; локально можно использовать свой Gradle 8.9+.

```bash
gradle :composeApp:run                 # запуск desktop-версии
gradle :composeApp:assembleDebug       # APK -> composeApp/build/outputs/apk/debug/
gradle :composeApp:createDistributable # EXE (Windows) / binary (Linux) в build/compose/binaries/main/app/
gradle :composeApp:packageMsi          # MSI-инсталлятор (Windows, нужен WiX)
gradle :composeApp:packageDeb          # DEB-пакет (Linux)
```

## CI

`.github/workflows/build.yml` — три параллельные джобы (`assembleDebug`, `createDistributable` на Windows и Linux).
Артефакты: `pcsim-android-apk`, `pcsim-windows-exe`, `pcsim-linux-binary`.
Пуш тега `v1.0.0` дополнительно создаёт GitHub Release со всеми тремя файлами.

## Стек

Kotlin 2.0.21 · Compose Multiplatform 1.7.1 · AGP 8.5.2 · Gradle 8.9 · JDK 17
