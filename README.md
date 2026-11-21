# PC Creator Simulator

2D-игра про сборку ПК на Kotlin Multiplatform + Compose Multiplatform. Работает на Android и Desktop; сборка Android APK, Windows EXE и Linux app-image выполняется GitHub Actions.

## Игровой цикл

1. Выбери комплектующую в магазине внизу экрана.
2. Нажми на соответствующий слот в открытом корпусе, чтобы установить её. Нажатие на уже установленную деталь без выбранной замены снимает её с частичным возвратом денег.
3. Собери систему из CPU, материнской платы, ОЗУ, GPU, накопителя и БП. Корпус можно улучшать отдельно.
4. Проверки POST учитывают сокет процессора, тип DDR и мощность блока питания.
5. Выполни заказ дня, получи деньги и репутацию; дорогие компоненты открываются по мере прогресса. За каждый день списывается аренда.

В мастерской нарисованы стол, монитор, открытый корпус, PCB-трассы и отдельные посадочные места CPU/RAM/GPU/SSD/PSU. Это процедурная 2D-графика на Compose Canvas — внешние ассеты не нужны.

## Запуск и сборка

Открой проект в Android Studio/IntelliJ с Gradle 8.9 и JDK 17:

```bash
gradle :composeApp:run
gradle :composeApp:assembleDebug
gradle :composeApp:createDistributable
```

## GitHub Actions

`.github/workflows/build.yml` запускает три сборки на push в `main`, pull request и вручную:

- `pcsim-android-apk` — debug APK;
- `pcsim-windows-exe` — Windows app-image с EXE;
- `pcsim-linux-binary` — Linux app-image.

Тег `v*` дополнительно создаёт GitHub Release.

## Стек

Kotlin 2.0.21 · Compose Multiplatform 1.7.1 · Android Gradle Plugin 8.5.2 · Gradle 8.9 · JDK 17.

> Godot-прототип сохранён в корне как неактивный эксперимент; текущий проект и CI — Kotlin. Godot-файлы не участвуют в Gradle-сборке.
