package org.example.bot

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import kotlin.concurrent.thread

@Component
class TelegramBotRunner(
    private val partyBot: PartyTelegramBot
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments?) {
        thread(name = "telegram-starter-thread") {
            try {
                log.info(">>> Запускаем регистрацию Telegram-бота в фоновом потоке...")
                val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
                botsApi.registerBot(partyBot)
                log.info(">>> БОТ УСПЕШНО ЗАПУЩЕН И ГОТОВ ПРИНИМАТЬ СООБЩЕНИЯ! 🚀")
            } catch (e: Exception) {
                log.error(">>> ОШИБКА ПРИ СТАРТЕ БОТА:", e)
            }
        }
    }
}