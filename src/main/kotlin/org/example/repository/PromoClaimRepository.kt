package org.example.repository

import org.example.entity.PromoClaimEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PromoClaimRepository : JpaRepository<PromoClaimEntity, Long> {
    fun existsByEventIdAndPhone(eventId: Long, phone: String): Boolean
    fun findByEventIdAndTelegramId(eventId: Long, telegramId: Long): PromoClaimEntity?
}