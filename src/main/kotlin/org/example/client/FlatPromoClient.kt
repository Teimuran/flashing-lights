package org.example.client

import org.example.client.dto.response.CreateInviteResponse
import org.example.dto.request.CreateInviteRequest
import org.example.dto.request.CreateKeyRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange("/v1")
interface FlatPromoClient {

    @PostExchange
    fun createKey(
        @PathVariable("eventId") eventId: Long,
        @PathVariable("promocodeId") promocodeId: Long,
        @RequestBody body: CreateKeyRequest
    )

    @PostExchange("/events/visitors/lists/{listId}/items")
    fun createInvite(
        @PathVariable("listId") listId: Long?,
        @RequestBody body: CreateInviteRequest
    ): CreateInviteResponse
}