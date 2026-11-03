package org.example.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "promo_claims")
class PromoClaimEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    val event: EventEntity,

    @Column(name = "telegram_id", nullable = false)
    val telegramId: Long,

    @Column(name = "phone", nullable = false)
    val phone: String,

    @Column(name = "username")
    val username: String? = null,

    @Column(name = "promo_code", nullable = false)
    val promoCode: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)