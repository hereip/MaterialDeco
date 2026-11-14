# MaterialDeco

**MaterialDeco** — минималистичные полноэкранные часы для Android с дизайном в стиле Material You.

Приложение превращает экран телефона в красивые часы с датой, уровнем заряда батареи и плавными анимированными элементами.

## ✨ Возможности

- 🕒 Большие цифровые часы в формате `HH:mm`
- 📅 Отображение текущей даты на русском языке
- 🔋 Отображение уровня заряда аккумулятора
- 🫧 Плавно анимированные фоновые элементы
- 🎨 Оформление на основе Material Design 3
- 📱 Полноэкранный режим без системных панелей
- 💡 Экран остаётся включённым во время работы приложения

## 🛠 Технологии

- **Kotlin** — основной язык разработки
- **Jetpack Compose** — создание интерфейса
- **Material 3** — компоненты и оформление
- **Gradle** — система сборки

## 📋 Требования

- Android 12 (API 31) или новее
- JDK 25 для сборки проекта
- Android Studio с поддержкой Kotlin и Jetpack Compose

## 🔨 Сборка

Клонируйте репозиторий:

```bash
git clone https://github.com/HereIP/MaterialDeco.git
cd MaterialDeco
```

Соберите debug-версию:

**Windows:**
```powershell
.\gradlew.bat assembleDebug
```

**Linux / macOS:**
```bash
./gradlew assembleDebug
```

Готовый APK появится в:

```text
app/build/outputs/apk/debug/
```

## 📁 Структура проекта

```text
MaterialDeco/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/materialdeco/
│           │       ├── MainActivity.kt
│           │       └── ui/theme/
│           └── res/
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## 📄 Лицензия

Информация о лицензии пока не указана.

---

Made with ❤️ by [HereIP](https://github.com/HereIP)
