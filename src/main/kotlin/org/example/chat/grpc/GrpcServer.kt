package org.example.chat.grpc

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.ui.MyServerController
import org.example.grpc.gen.ChatGrpcKt
import org.example.grpc.gen.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromServer
import tornadofx.find
import java.net.InetSocketAddress


private class ChatService : ChatGrpcKt.ChatCoroutineImplBase() {
    override fun chat(requests: Flow<ChatMessageFromClient>): Flow<ChatMessageFromServer> {
        val serverController = find(MyServerController::class)
        return serverController.sendChannel.receiveAsFlow()
            .also {
                GlobalScope.launch {
                    requests.collect { request -> serverController.receiveChannel.send(request) }
                }
            }
    }
}

class ChatServer(host: String, port: Int) {
    val server: Server = NettyServerBuilder
        .forAddress(InetSocketAddress(host, port))
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
    val server = ChatServer("localhost", port)
    server.start()
    server.await()
}