package org.example.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class EventStateService(
    @Value("\${event.time:22:00}") private val eventTimeStr: String,
    @Value("\${event.zone-id:Asia/Krasnoyarsk}") private val zoneIdStr: String
) {
    private val zone = ZoneId.of(zoneIdStr)

    // Берет сегодняшнюю дату в Красноярске и фиксирует 22:00 старта.
    // Благодаря val после полуночи дата не собьется, и халява случайно не откроется снова.
    private val eventStartTime: ZonedDateTime = LocalDate.now(zone)
        .atTime(LocalTime.parse(eventTimeStr))
        .atZone(zone)

    fun isEventStarted(): Boolean {
        val now = ZonedDateTime.now(zone)
        return now.isAfter(eventStartTime)
    }
}