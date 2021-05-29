package org.example.chat.grpc

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import org.example.chat.ui.MyClientController
import org.example.chat.ui.MyServerController
import org.example.grpc.gen.ChatGrpcKt
import tornadofx.find
import java.io.Closeable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ChatClient(address: String) : Closeable {
    private val channel = ManagedChannelBuilder
        .forTarget(address)
        .usePlaintext()
        .executor(Dispatchers.Default.asExecutor())
        .build()

    private val stub = ChatGrpcKt.ChatCoroutineStub(channel)

    suspend fun start() {
        val clientController = find(MyClientController::class)
        val requests = clientController.sendChannel.receiveAsFlow()
        val responses = stub.chat(requests)
        GlobalScope.launch {
            responses.collect { resp ->
                clientController.receiveChannel.send(resp)
            }
        }.join()
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

fun main() = runBlocking {
    ChatClient("localhost:50051").use { client ->
        client.start()
    }
}