# Первый этап: сборка проекта
FROM openjdk:21-jdk-slim AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл pom.xml в контейнер для разрешения зависимостей
COPY pom.xml .

# Скачиваем зависимости для сборки (используется отдельно, чтобы кэшировать этап)
RUN apt-get update && apt-get install -y maven && mvn dependency:resolve

# Копируем исходные файлы в контейнер
COPY src ./src

# Собираем проект в режиме production
RUN mvn clean package -DskipTests

# Второй этап: создание финального образа
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем скомпилированный JAR-файл из предыдущего этапа
COPY --from=builder /app/target/TGbot-1.0-SNAPSHOT.jar /app/TGbot.jar

# Копируем конфиги из хоста в контейнер
COPY configs /configs

# Создаем директорию для логов и задаем права доступа
RUN mkdir -p /output/logs && chmod -R 777 /output

# Устанавливаем переменную окружения для конфигурационного файла
ENV CONFIG_PATH="/configs/configs.ini"

# Указываем команду, которая будет выполняться при старте контейнера
CMD ["java", "-jar", "/app/TGbot.jar", "autostart"]
