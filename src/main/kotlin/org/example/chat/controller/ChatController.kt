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

/**
 * A main controller class.
 *
 * Can launch server or client models. Receives messages from views using channels
 * and sends them to the corresponding model (client or server).
 */
class ChatController : Controller() {
    val sendChannel = Channel<ChatMessage>()
    val receiveChannel = Channel<ChatMessage>()
    private var userName = "noname"

    suspend fun sendMessage(text: String) {
        sendChannel.send(
            ChatMessage {
                name = userName
                message = text
                time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
            }
        )
    }

    suspend fun startClient(clientName: String, host: String, port: Int) {
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