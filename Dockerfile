# Первый этап: сборка проекта
FROM openjdk:21-jdk-slim AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем pom.xml и исходные файлы в контейнер
COPY pom.xml .
COPY src ./src

# Устанавливаем Maven и собираем проект
RUN apt-get update && apt-get install -y maven && mvn clean package

# Второй этап: создание финального образа
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем скомпилированный JAR файл из предыдущего этапа
COPY --from=builder /app/target/TGbot-1.0-SNAPSHOT.jar /app/TGbot.jar

# Копируем конфиги из хоста
COPY configs /configs

# Создаем директорию для логов и даем права на запись
RUN mkdir -p /output/logs && chmod -R 777 /output

# Устанавливаем переменную окружения для конфигурации
ENV CONFIG_PATH="/configs/configs.ini"

# Команда запуска контейнера
CMD ["java", "-jar", "/app/TGbot.jar", "autostart"]
