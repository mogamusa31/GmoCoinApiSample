package rest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiToken(
    val status: Int,
    val data: String,
    @SerialName("responsetime")
    val responseTime: String
)
