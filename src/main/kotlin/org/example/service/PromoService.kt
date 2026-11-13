package org.example.service

import org.example.client.FlatPromoClient
import org.example.dto.request.CreateInviteItem
import org.example.dto.request.CreateInviteRequest
import org.example.entity.EventEntity
import org.example.entity.PromoClaimEntity
import org.example.repository.EventRepository
import org.example.repository.PromoClaimRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromoService(
    private val flatClient: FlatPromoClient,
    private val claimRepository: PromoClaimRepository,
    private val eventRepository: EventRepository,
    private val eventStateService: EventStateService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun claimTicketForActiveEvent(
        telegramId: Long,
        phone: String,
        username: String?,
        firstName: String
    ): Pair<String, EventEntity> {
        val event = eventRepository.findFirstByIsActiveTrueOrderByIdDesc()
            ?: throw IllegalStateException("Сейчас нет активных вечеринок с акциями!")

        val normalizedPhone = phone.replace(Regex("[^0-9]"), "")

        // 1. Если чел уже получал билет — отдаем его старую готовую ссылку
        val existing = claimRepository.findByEventIdAndTelegramId(event.id!!, telegramId)
        if (existing != null) {
            log.info("Юзер $telegramId уже получал билет ${existing.promoCode}, отдаем повторно")
            return Pair(existing.promoCode, event)
        }

        // 2. ПРОВЕРКА ВРЕМЕНИ: если билета нет и ивент начался — отшиваем на вход
        if (eventStateService.isEventStarted()) {
            throw IllegalStateException(
                "Мероприятие уже началось, бесплатная выдача закрыта! ⏳\n\n" +
                        "Но ты еще можешь залететь на тусу: билеты продаются прямо на входе у админов. Ждем тебя! 🎫"
            )
        }

        // 3. Проверка номера телефона
        if (claimRepository.existsByEventIdAndPhone(event.id, normalizedPhone)) {
            throw IllegalStateException("На этот номер уже был получен билет на данную тусовку!")
        }

        log.info("Создаем персональный инвайт во FLAT для юзера $telegramId ($firstName)...")

        // 4. Стучимся во FLAT API и создаем инвайт в списке
        val title = username?.let { "@$it ($firstName)" } ?: "$firstName ($normalizedPhone)"
        val response = flatClient.createInvite(
            listId = event.flatListId,
            body = CreateInviteRequest(
                items = listOf(
                    CreateInviteItem(
                        inviteLink = true,
                        multi = true,
                        multiLimit = 1,
                        qrCount = 1,
                        title = title
                    )
                )
            )
        )

        val inviteItem = response.items.firstOrNull()
            ?: throw IllegalStateException("Не удалось получить инвайт от FLAT!")

        val inviteCode = inviteItem.inviteCode ?: inviteItem.inviteId
        ?: throw IllegalStateException("FLAT не вернул код инвайта!")

        // Прямая ссылка на электронный билет с QR-кодом!
        val ticketUrl = "https://flat.audio/personal/$inviteCode"

        // 5. Сохраняем ссылку в БД
        claimRepository.save(
            PromoClaimEntity(
                event = event,
                telegramId = telegramId,
                phone = normalizedPhone,
                username = username,
                promoCode = ticketUrl
            )
        )

        log.info("Инвайт успешно создан: $ticketUrl")
        return Pair(ticketUrl, event)
    }
}