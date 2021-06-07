package org.example.chat.util

import org.example.grpc.gen.ChatMessage
import tornadofx.EventBus
import tornadofx.FXEvent

/**
 * Kotlin-way builder for chat message's wrapper.
 */
inline fun ChatMessage(block: ChatMessage.Builder.() -> Unit): ChatMessage =
    ChatMessage.newBuilder().apply(block).build()

/**
 * Event for handling exceptions within GUI.
 */
object ExceptionEvent : FXEvent(EventBus.RunOn.BackgroundThread)