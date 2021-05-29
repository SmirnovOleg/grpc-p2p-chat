package org.example.chat.grpc

import org.example.grpc.gen.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromServer

inline fun ChatMessageFromServer(block: ChatMessageFromServer.Builder.() -> Unit): ChatMessageFromServer =
    ChatMessageFromServer.newBuilder().apply(block).build()

inline fun ChatMessageFromClient(block: ChatMessageFromClient.Builder.() -> Unit): ChatMessageFromClient =
    ChatMessageFromClient.newBuilder().apply(block).build()