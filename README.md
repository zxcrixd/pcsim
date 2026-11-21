# PC Creator Simulator

Горизонтальная 2D-игра про сборку ПК на **Kotlin Multiplatform + Compose Multiplatform**. Работает на Android и Desktop; сборка APK, Windows EXE и Linux app-image выполняется через GitHub Actions.

## Что внутри

- Главное меню и горизонтальная мастерская без верхней серой системной полосы с названием;
- правая боковая навигация: **Главная**, **Задания**, **Сборка ПК**, **Магазин**;
- профиль продавца, уровень, XP и рейтинг встроены в правую панель;
- адаптивные размеры интерфейса: compact, medium и desktop — выбираются по ширине окна;
- отдельные заказы клиентов с описанием, бюджетом, требованиями и бонусом;
- тёплая палитра мастерской: дерево, графит, зелёная PCB и медные акценты;
- 2D Canvas-сцена открытого корпуса с CPU/RAM/GPU/SSD/PSU-слотами;
- POST-проверка сокета CPU, типа DDR, мощности БП, бюджета клиента и обязательных компонентов;
- прибыль, рейтинг, репутация и открытие новых комплектующих.

## Игровой цикл

1. В **Заданиях** выбери заказ клиента.
2. В **Магазине** выбери комплектующие.
3. В **Сборке ПК** нажми на соответствующие слоты корпуса, чтобы установить детали.
4. Нажми **POST / Собрать**, затем **Продать**.
5. Получи деньги и XP, подними уровень и рейтинг продавца.

## Запуск и сборка

Требуются JDK 17 и Gradle 8.9:

```bash
gradle :composeApp:run
gradle :composeApp:assembleDebug
gradle :composeApp:createDistributable
```

Android принудительно запускается в landscape; desktop-окно — 1280×800. Размеры правой панели и карточек магазина меняются по ширине окна: compact (<720 dp), medium (<1000 dp), desktop (≥1000 dp).

## GitHub Actions

`.github/workflows/build.yml` собирает:

- `pcsim-android-apk` — debug APK;
- `pcsim-windows-exe` — Windows app-image с EXE;
- `pcsim-linux-binary` — Linux app-image.

Пуш тега `v*` дополнительно создаёт GitHub Release.

## Стек

Kotlin 2.0.21 · Compose Multiplatform 1.7.1 · Android Gradle Plugin 8.5.2 · Gradle 8.9 · JDK 17.
