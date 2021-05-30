package org.example.chat.util

import org.example.grpc.gen.ChatMessage
import tornadofx.EventBus
import tornadofx.FXEvent

inline fun ChatMessage(block: ChatMessage.Builder.() -> Unit): ChatMessage =
    ChatMessage.newBuilder().apply(block).build()

object ExceptionEvent : FXEvent(EventBus.RunOn.BackgroundThread)