package websocket

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

class GmoCoinPublicWSApiClient private constructor() {
    companion object {
        private const val WEB_SOCKET_URL = "wss://api.coin.z.com/ws/public/v1"

        private var INSTANCE: GmoCoinPublicWSApiClient? = null

        fun getInstance(): GmoCoinPublicWSApiClient {
            return INSTANCE ?: synchronized(this) { INSTANCE ?: GmoCoinPublicWSApiClient().also { INSTANCE = it } }
        }
    }

    suspend fun subscribeLatestRate() {
        val sendMsg = """
            {
                "command": "subscribe", 
                "channel": "ticker", 
                "symbol": "BTC"
            }
            """
        HttpClient {
            install(WebSockets)
        }.use {
            it.webSocket(
                urlString = WEB_SOCKET_URL
            ) {
                send(Frame.Text(sendMsg))
                while (true) {
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> println(frame.readText())
                        else -> {
                        }
                    }
                }
            }
        }
    }

    suspend fun subscribeTradeHistory() {
        val sendMsg = """
            {
                "command": "subscribe", 
                "channel": "trades", 
                "symbol": "BTC"
            }
            """
        HttpClient {
            install(WebSockets)
        }.use {
            it.webSocket(
                urlString = WEB_SOCKET_URL
            ) {
                send(Frame.Text(sendMsg))
                while (true) {
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> println(frame.readText())
                        else -> {
                        }
                    }
                }
            }
        }
    }
}