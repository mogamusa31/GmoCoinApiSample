import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import rest.GmoCoinPrivateRestApiClient
import rest.GmoCoinPublicRestApiClient
import websocket.GmoCoinPrivateWSApiClient
import websocket.GmoCoinPublicWSApiClient

fun main() = runBlocking {
    println("===== Public REST API =====")
    GmoCoinPublicRestApiClient.getInstance().use {
        it.fetchExchangeStatus()
        it.fetchLatestRate()
    }

    println("===== Private REST API =====")
    GmoCoinPrivateRestApiClient.getInstance().use {
        it.fetchAccountMargin()
        it.fetchOrderInformation()
    }


    val parentJob = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + parentJob)
    println("======= Public Web Socket API ======")
    val wsClient = GmoCoinPublicWSApiClient.getInstance()
    scope.launch {
        wsClient.fetchLatestRate()
    }
    scope.launch {
        wsClient.fetchTradeHistory()
    }

    println("======= Private Web Socket API ======")
    val privateWsClient = GmoCoinPrivateWSApiClient.getInstance()
    scope.launch {
        privateWsClient.fetchLatestRate()
    }

    delay(30_000)
    println("=== STOP ===")
    scope.coroutineContext.cancelChildren()
    println("=== COMPLETED ===")
    delay(10_000)
    println("=== FINISHED ===")
}
