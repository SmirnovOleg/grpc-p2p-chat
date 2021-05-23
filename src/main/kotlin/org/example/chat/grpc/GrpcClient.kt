package org.example.chat.grpc

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.example.grpc.gen.ChatGrpcKt
import java.io.Closeable
import java.util.concurrent.TimeUnit

private class ChatClient(address: String) : Closeable {
    private val channel = ManagedChannelBuilder
        .forTarget(address)
        .usePlaintext()
        .executor(Dispatchers.Default.asExecutor())
        .build()

    private val stub = ChatGrpcKt.ChatCoroutineStub(channel)

    suspend fun start() {
        val requests = readMessages().map {
            ChatMessageFromClient {
                name = ""
                message = it
            }
        }
        val responses = stub.chat(requests)
        GlobalScope.launch {
            responses.collect { resp ->
                println(resp.message)
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