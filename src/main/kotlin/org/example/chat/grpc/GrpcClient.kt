package org.example.chat.grpc

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.ui.MyController
import org.example.grpc.gen.ChatGrpcKt
import tornadofx.find
import java.io.Closeable
import java.util.concurrent.TimeUnit

class ChatClient(address: String) : Closeable {
    private val channel = ManagedChannelBuilder
        .forTarget(address)
        .usePlaintext()
        .executor(Dispatchers.Default.asExecutor())
        .build()

    private val stub = ChatGrpcKt.ChatCoroutineStub(channel)

    suspend fun start() {
        val clientController = find(MyController::class)
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