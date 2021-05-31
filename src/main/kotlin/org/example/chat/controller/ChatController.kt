package org.example.chat.controller

import kotlinx.coroutines.channels.Channel
import org.example.chat.grpc.ChatClient
import org.example.chat.grpc.ChatServer
import org.example.chat.util.ChatMessage
import org.example.grpc.gen.ChatMessage
import tornadofx.Controller
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ChatController : Controller() {
    val sendChannel = Channel<ChatMessage>()
    val receiveChannel = Channel<ChatMessage>()
    var userName = "noname"

    suspend fun sendToClient(inputValue: String) {
        sendChannel.send(
            ChatMessage {
                name = userName
                message = inputValue
                time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
            }
        )
    }

    suspend fun startClient(clientName: String, host: String, port: String) {
        userName = clientName
        val chatClient = ChatClient("${host}:${port}")
        chatClient.use { client ->
            client.start()
        }
    }

    fun startServer(serverName: String, host: String, port: Int) {
        userName = serverName
        val server = ChatServer(host, port)
        server.start()
        server.await()
    }
}