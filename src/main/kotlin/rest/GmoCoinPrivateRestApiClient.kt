package rest

import Credential.API_KEY
import Credential.SECRET_KEY
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.URLBuilder
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.net.URL
import java.util.Calendar
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

class GmoCoinPrivateRestApiClient private constructor() : Closeable {
    companion object {
        private var INSTANCE: GmoCoinPrivateRestApiClient? = null
        private const val ENDPOINT_URL = "https://api.coin.z.com/private"

        fun getInstance(): GmoCoinPrivateRestApiClient {
            return INSTANCE ?: synchronized(this) { GmoCoinPrivateRestApiClient().also { INSTANCE = it } }
        }
    }

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    fun fetchAccountMargin() = runBlocking {
        val timestamp = Calendar.getInstance().timeInMillis.toString()
        val method = "GET"
        val path = "/v1/account/margin"

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(SECRET_KEY.toByteArray(), "HmacSHA256"))
        val sign = mac.doFinal((timestamp + method + path).toByteArray())
            .joinToString("") { String.format("%02x", it and 255.toByte()) }

        val response = client.get<HttpResponse>(URL(ENDPOINT_URL + path)) {
            headers.append("API-KEY", API_KEY)
            headers.append("API-TIMESTAMP", timestamp)
            headers.append("API-SIGN", sign)
        }
        println(response.readText())
    }

    fun fetchOrderInformation() = runBlocking {
        val timestamp = Calendar.getInstance().timeInMillis.toString()
        val method = "GET"
        val path = "/v1/latestExecutions"

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(SECRET_KEY.toByteArray(), "HmacSHA256"))
        val sign = mac.doFinal((timestamp + method + path).toByteArray())
            .joinToString("") { String.format("%02x", it and 255.toByte()) }

        val response = client.get<HttpResponse>(
            URLBuilder(ENDPOINT_URL + path)
                .also {
                    it.parameters.append("symbol", "BTC")
                    it.parameters.append("page", "1")
                    it.parameters.append("API-count", "100")
                }
                .build()
        ) {
            headers.append("API-KEY", API_KEY)
            headers.append("API-TIMESTAMP", timestamp)
            headers.append("API-SIGN", sign)
        }
        println(response.readText())
    }

    fun fetchAccessToken(): String = runBlocking {
        val timestamp = Calendar.getInstance().timeInMillis.toString()
        val method = "POST"
        val path = "/v1/ws-auth"
        val reqBody = """{}"""

        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)
        val sign = mac.doFinal((timestamp + method + path + reqBody).toByteArray())
            .joinToString("") { String.format("%02x", it and 255.toByte()) }

        val response = client.post<ApiToken>(ENDPOINT_URL + path) {
            headers.append("API-KEY", API_KEY)
            headers.append("API-TIMESTAMP", timestamp)
            headers.append("API-SIGN", sign)
            body = reqBody
        }
        response.data
    }

    override fun close() {
        client.close()
    }
}