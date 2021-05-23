package org.example.chat.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.example.grpc.gen.ChatGrpcKt
import org.example.grpc.gen.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromServer


private class ChatService : ChatGrpcKt.ChatCoroutineImplBase() {
    override fun chat(requests: Flow<ChatMessageFromClient>): Flow<ChatMessageFromServer> {
        return readMessages().map {
            ChatMessageFromServer {
                message = it
            }
        }.also {
            GlobalScope.launch {
                requests.collect { request -> println(request.message) }
            }
        }
    }
}

private class ChatServer(port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(ChatService())
        .build()

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(
            Thread { server.shutdown() }
        )
    }

    fun await() {
        server.awaitTermination()
    }
}

fun main() {
    val port = 50051
    val server = ChatServer(port)
    server.start()
    server.await()
}