package org.example.repository

import org.example.entity.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<EventEntity, Long> {
    fun findFirstByIsActiveTrueOrderByIdDesc(): EventEntity?
}