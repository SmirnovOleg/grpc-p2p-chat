package org.example.chat.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.grpc.gen.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromServer

fun readMessages(): Flow<String?> = flow {
    while (true) {
        val message = readLine()
        emit(message)
    }
}

inline fun ChatMessageFromServer(block: ChatMessageFromServer.Builder.() -> Unit): ChatMessageFromServer =
    ChatMessageFromServer.newBuilder().apply(block).build()

inline fun ChatMessageFromClient(block: ChatMessageFromClient.Builder.() -> Unit): ChatMessageFromClient =
    ChatMessageFromClient.newBuilder().apply(block).build()