package org.example.bot

import org.example.service.PromoService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

@Component
class PartyTelegramBot(
    @Value("\${bot.token}") botToken: String,
    @Value("\${bot.username}") private val botName: String,
    @Value("\${bot.channel-username}") private val channelUsername: String,
    @Value("\${bot.channel-url}") private val channelUrl: String,
    private val promoService: PromoService
) : TelegramLongPollingBot(botToken) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage()) return
        val message = update.message
        val chatId = message.chatId.toString()
        val userId = message.from.id

        if (message.hasText() && message.text.startsWith("/start")) {
            sendWelcomeWithPoster(chatId)
            return
        }

        if (message.hasContact()) {
            val contact = message.contact

            if (contact.userId != userId) {
                sendMessage(chatId, "⚠️ Отправь свой собственный номер телефона с помощью кнопки внизу.")
                return
            }

            if (!checkChannelSubscription(userId)) {
                sendSubscribeRequired(chatId)
                return
            }

            try {
                val (ticketUrl, event) = promoService.claimTicketForActiveEvent(
                    telegramId = userId,
                    phone = contact.phoneNumber,
                    username = message.from.userName,
                    firstName = message.from.firstName ?: "Гость"
                )
                sendSuccess(chatId, ticketUrl, event.name)
            } catch (e: Exception) {
                log.error("Ошибка при выдаче билета для юзера $userId", e)
                sendMessage(chatId, "❌ ${e.message ?: "Произошла ошибка, попробуй позже."}")
            }
        }
    }

    private fun checkChannelSubscription(userId: Long): Boolean {
        return try {
            val member = execute(GetChatMember(channelUsername, userId))
            val status = member.status
            status in listOf("member", "creator", "administrator")
        } catch (e: Exception) {
            log.error("Не удалось проверить подписку юзера $userId на канал $channelUsername: ${e.message}")
            true
        }
    }

    private fun sendWelcomeWithPoster(chatId: String) {
        val caption = """
            Здарова! 🔥
            
            Мы дарим **бесплатную проходку** новичкам на тусовку *Flashing Lights*!
            
            📌 **Условия простые:**
            1. Быть подписанным на наш канал
            2. Нажать кнопку ниже и подтвердить свой номер телефона
        """.trimIndent()

        val button = KeyboardButton("📱 Забрать бесплатный билет").apply {
            requestContact = true
        }
        val keyboard = ReplyKeyboardMarkup(listOf(KeyboardRow(listOf(button)))).apply {
            resizeKeyboard = true
            oneTimeKeyboard = true
        }

        try {
            val posterResource = ClassPathResource("afisha fl.jpg")
            if (posterResource.exists()) {
                val sendPhoto = SendPhoto().apply {
                    this.chatId = chatId
                    this.photo = InputFile(posterResource.inputStream, "poster.jpg")
                    this.caption = caption
                    this.parseMode = "Markdown"
                    this.replyMarkup = keyboard
                }
                execute(sendPhoto)
                return
            }
        } catch (e: Exception) {
            log.warn("Не удалось отправить картинку афиши, отправляем обычный текст: ${e.message}")
        }

        execute(SendMessage(chatId, caption).apply {
            parseMode = "Markdown"
            replyMarkup = keyboard
        })
    }


    private fun sendSubscribeRequired(chatId: String) {
        val text = """
            ⚠️ *Ты ещё не подписан на наш канал!*
            
            Бесплатный вход доступен только подписчикам нашего комьюнити.
            
            1. Подпишись на канал по кнопке ниже 👇
            2. После этого снова нажми кнопку **«📱 Забрать бесплатный билет»**!
        """.trimIndent()

        val channelButton = InlineKeyboardButton("📢 Подписаться на канал").apply {
            url = channelUrl
        }
        val markup = InlineKeyboardMarkup(listOf(listOf(channelButton)))

        execute(SendMessage(chatId, text).apply {
            parseMode = "Markdown"
            replyMarkup = markup
        })
    }

    private fun sendSuccess(chatId: String, ticketUrl: String, eventName: String) {
        val text = """
            🎉 *Поздравляем! Твой бесплатный билет готов!*
            
            Тусовка: *«$eventName»*
            
            👇 Нажми на кнопку ниже, чтобы открыть свой **электронный билет с QR-кодом**.
            
            ⚠️ Сохрани ссылку или сделай скриншот — на входе охрана отсканирует этот QR-код для прохода!
        """.trimIndent()

        val linkButton = InlineKeyboardButton("🎟 Открыть мой билет (QR-код)").apply {
            url = ticketUrl
        }
        val inlineMarkup = InlineKeyboardMarkup(listOf(listOf(linkButton)))

        execute(SendMessage(chatId, text).apply {
            parseMode = "Markdown"
            replyMarkup = inlineMarkup
        })
    }

    private fun sendMessage(chatId: String, text: String) {
        execute(SendMessage(chatId, text))
    }
}