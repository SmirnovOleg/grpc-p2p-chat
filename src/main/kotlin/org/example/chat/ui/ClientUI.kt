package org.example.chat.ui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.NodeOrientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
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

    override fun onDock() {
        primaryStage.width = 500.0
        primaryStage.height = 400.0
    }

    override val root = vbox {
        textarea(chat) {
            GlobalScope.launch {
                clientController.receiveChannel.receiveAsFlow().collect { response ->
                    chat.value += "(${response.time}) [${response.name}]: ${response.message}\n"
                }
            }
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            isEditable = false
        }
        textarea(input) {
            selectAll()
            vboxConstraints {
                maxHeight = 40.0
            }
        }
        hbox {
            button("Send") {
                shortcut("Enter")
                action {
                    if (input.value != "") {

                        GlobalScope.launch {
                            clientController.sendToServer(input.value)
                        }
                        chat.value += "you: ${input.value}\n"
                        input.value = ""
                    }
                }
                vboxConstraints {
                    marginTop = 10.0
                    marginBottom = 10.0
                    nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
                }
            }
            alignment = Pos.CENTER_RIGHT
            vboxConstraints {
                margin = Insets(10.0)
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
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM")).toString()
            }
        )
    }
}