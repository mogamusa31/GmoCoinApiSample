package rest

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.runBlocking
import java.io.Closeable

class GmoCoinPublicRestApiClient private constructor() : Closeable {
    companion object {
        private var INSTANCE: GmoCoinPublicRestApiClient? = null

        private const val ENDPOINT_URL = "https://api.coin.z.com/public"

        fun getInstance(): GmoCoinPublicRestApiClient {
            return INSTANCE ?: synchronized(this) { INSTANCE ?: GmoCoinPublicRestApiClient().also { INSTANCE = it } }
        }
    }

    private val client = HttpClient()

    fun fetchExchangeStatus() = runBlocking {
        val response = client.get<HttpResponse>("$ENDPOINT_URL/v1/status")
        println(response.readText())
    }

    fun fetchLatestRate() = runBlocking {
        val response = client.get<HttpResponse>("$ENDPOINT_URL/v1/ticker?symbol=BTC")
        println(response.readText())
    }

    override fun close() {
        client.close()
    }
}
