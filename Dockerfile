# 1 этап: Сборка приложения
FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app

# Копируем всё в контейнер
COPY . .

# Собираем JAR без прогона тестов
RUN gradle bootJar --no-daemon -x test

# 2 этап: Запуск приложения
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Забираем скомпилированный jar-ник
COPY --from=builder /app/build/libs/*.jar app.jar

# Ограничиваем аппетит Java (чтобы не жрала лишнего)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]