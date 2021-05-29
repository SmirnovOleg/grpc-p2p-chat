package org.example.chat.ui

import kotlinx.coroutines.channels.Channel
import org.example.chat.grpc.ChatMessage
import org.example.grpc.gen.ChatMessage
import tornadofx.Controller
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MyController : Controller() {
    val sendChannel = Channel<ChatMessage>()
    val receiveChannel = Channel<ChatMessage>()
    var userName = "noname"

    suspend fun sendToServer(inputValue: String) {
        sendChannel.send(
            ChatMessage {
                name = userName
                message = inputValue
                time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
            }
        )
    }
}