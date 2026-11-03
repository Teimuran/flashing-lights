package org.example.entity


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "events")
class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(name = "flat_event_id", nullable = false)
    val flatEventId: Long,

    @Column(name = "flat_promocode_id", nullable = false)
    val flatPromocodeId: Long,

    @Column(name = "event_url", nullable = false)
    val eventUrl: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "flat_list_id")
    val flatListId: Long? = null
)