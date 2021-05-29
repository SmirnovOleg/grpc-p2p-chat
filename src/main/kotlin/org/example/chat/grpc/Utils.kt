package org.example.chat.grpc

import org.example.grpc.gen.ChatMessage

inline fun ChatMessage(block: ChatMessage.Builder.() -> Unit): ChatMessage =
    ChatMessage.newBuilder().apply(block).build()