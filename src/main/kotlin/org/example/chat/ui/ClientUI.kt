package org.example.chat.ui

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.grpc.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromServer
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyClientView : View() {
    private val clientController: MyClientController by inject()
    var chat = SimpleStringProperty("")
    var input = SimpleStringProperty("Type your message here")

    override val root = vbox {
        textarea(chat) {
            GlobalScope.launch {
                clientController.receiveChannel.receiveAsFlow().collect { response ->
                    chat.value += "(${response.time}) ${response.name}: ${response.message}\n"
                }
            }
        }.isEditable = false
        textarea(input) {
            selectAll()
        }
        button("Send") {
            action {
                GlobalScope.launch {
                    clientController.sendToServer(input.value)
                }
                chat.value += "you: ${input.value}\n"
                input.value = ""
            }
        }
    }
}

class MyClientController : Controller() {
    val sendChannel = Channel<ChatMessageFromClient>()
    val receiveChannel = Channel<ChatMessageFromServer>()
    var clientName = "noname(client)"

    suspend fun sendToServer(inputValue: String) {
        sendChannel.send(
            ChatMessageFromClient {
                name = clientName
                message = inputValue
                time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME).toString()
            }
        )
    }
}