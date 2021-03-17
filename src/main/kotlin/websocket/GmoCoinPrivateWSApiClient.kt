package websocket

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

class GmoCoinPrivateWSApiClient private constructor() {
    companion object {
        private var INSTANCE: GmoCoinPrivateWSApiClient? = null

        private const val WEB_SOCKET_URL = "wss://api.coin.z.com/ws/private/v1/"

        fun getInstance(): GmoCoinPrivateWSApiClient {
            return INSTANCE ?: synchronized(this) { INSTANCE ?: GmoCoinPrivateWSApiClient().also { INSTANCE = it } }
        }
    }

    suspend fun subscribeOrderEvents(accessToken: String) {
        val sendMsg = """
            {
                "command": "subscribe", 
                "channel": "orderEvents"
            }
            """
        HttpClient {
            install(WebSockets)
        }.use {
            it.webSocket(
                urlString = WEB_SOCKET_URL + accessToken
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
