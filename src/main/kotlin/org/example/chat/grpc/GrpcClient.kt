package org.example.chat.grpc

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.controller.ChatController
import org.example.grpc.gen.ChatGrpcKt
import tornadofx.find
import java.io.Closeable
import java.util.concurrent.TimeUnit

/**
 * The main client model.
 *
 * Implements chat messaging with the server. Gets client's messages from
 * {%link org.example.chat.controller.ChatController#sendChannel} and sends it to the server using gRPC
 * in server-stub call.
 *
 * Receives the messages from the server in the concurrent coroutine and sends them back to client's view:
 * {%link org.example.chat.controller.ChatController#receiveChannel}.
 */
class ChatClient(address: String) : Closeable {
    private val channel = ManagedChannelBuilder
        .forTarget(address)
        .usePlaintext()
        .executor(Dispatchers.Default.asExecutor())
        .build()

    private val stub = ChatGrpcKt.ChatCoroutineStub(channel)

    suspend fun start() {
        val clientController = find(ChatController::class)
        val requests = clientController.sendChannel.receiveAsFlow()
        val responses = stub.chat(requests)
        GlobalScope.launch {
            responses.collect { response ->
                clientController.receiveChannel.send(response)
            }
        }.join()
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}