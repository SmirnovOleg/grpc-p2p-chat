package org.example.chat.ui

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.grpc.ChatMessageFromServer
import org.example.grpc.gen.ChatMessageFromClient
import org.example.grpc.gen.ChatMessageFromServer
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyServerView : View() {
    private val serverController: MyServerController by inject()
    var chat = SimpleStringProperty("")
    var input = SimpleStringProperty("Type your message here")

    override val root = vbox {
        textarea(chat) {
            GlobalScope.launch {
                serverController.receiveChannel.receiveAsFlow().collect { response ->
                    chat.value += "(${response.time}) ${response.name}: ${response.message}\n"
                }
            }
        }.isEditable = false
        textarea(input) {
            selectAll()
            vboxConstraints {
                maxHeight = 40.0
            }
        }
        button("Send") {
            action {
                GlobalScope.launch {
                    serverController.sendToClient(input.value)
                }
                chat.value += "you: ${input.value}\n"
                input.value = ""
            }
        }
    }
}

class MyServerController : Controller() {
    val sendChannel = Channel<ChatMessageFromServer>()
    val receiveChannel = Channel<ChatMessageFromClient>()
    var serverName = "noname(server)"

    suspend fun sendToClient(inputValue: String) {
        sendChannel.send(
            ChatMessageFromServer {
                name = serverName
                message = inputValue
                time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME).toString()
            }
        )
    }
}