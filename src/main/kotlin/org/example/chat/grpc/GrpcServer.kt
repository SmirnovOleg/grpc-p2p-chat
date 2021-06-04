package org.example.chat.grpc

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.controller.ChatController
import org.example.grpc.gen.ChatGrpcKt
import org.example.grpc.gen.ChatMessage
import tornadofx.find
import java.net.InetSocketAddress

/**
 * The main gRPC service, runs on server-side.
 *
 * Inherits from auto-generated {%link org.example.grpc.gen.ChatGrpcKt.ChatCoroutineImplBase}.
 * Implements a single endpoint with bi-directional streaming for chat messaging.
 *
 * Receives client's messages as a flow from controller and sends server's messages to the client in the concurrent coroutine.
 */
private class ChatService : ChatGrpcKt.ChatCoroutineImplBase() {
    override fun chat(requests: Flow<ChatMessage>): Flow<ChatMessage> {
        val serverController = find(ChatController::class)
        return serverController.sendChannel.receiveAsFlow()
            .also {
                GlobalScope.launch {
                    requests.collect { request ->
                        serverController.receiveChannel.send(request)
                    }
                }
            }
    }
}


/**
 * A class that hosts the server with {%link org.example.chat.grpc.ChatService}
 * using {%link io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder}.
 */
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