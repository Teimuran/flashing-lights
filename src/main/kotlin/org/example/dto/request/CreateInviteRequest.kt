package org.example.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateInviteItem(
    @JsonProperty("invite_link") val inviteLink: Boolean = true,
    val multi: Boolean = true,
    @JsonProperty("multi_limit") val multiLimit: Int = 1,
    @JsonProperty("qr_count") val qrCount: Int = 1,
    val title: String? = null // Сюда передадим имя или номер телефона гостя!
)

data class CreateInviteRequest(
    val items: List<CreateInviteItem>
)