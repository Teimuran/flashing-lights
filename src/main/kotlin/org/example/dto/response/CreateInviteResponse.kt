package org.example.client.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class InviteItemDto(
    val id: Long,
    @JsonProperty("invite_code") val inviteCode: String?,
    @JsonProperty("invite_id") val inviteId: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CreateInviteResponse(
    val items: List<InviteItemDto> = emptyList()
)