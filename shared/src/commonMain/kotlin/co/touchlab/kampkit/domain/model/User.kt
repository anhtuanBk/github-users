package co.touchlab.kampkit.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val htmlUrl: String
)
