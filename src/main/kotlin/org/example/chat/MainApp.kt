package org.example.chat

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.chat.grpc.ChatClient
import org.example.chat.grpc.ChatServer
import org.example.chat.ui.MyClientController
import org.example.chat.ui.MyClientView
import org.example.chat.ui.MyServerController
import org.example.chat.ui.MyServerView
import tornadofx.*

class MyApp : App(InitView::class)

class InitView : View() {
    private val clientController: MyClientController by inject()
    private val serverController: MyServerController by inject()

    var name = SimpleStringProperty("simp")
    var host = SimpleStringProperty("localhost")
    var port = SimpleStringProperty("50051")

    override val root = form {
        fieldset("Initial chat settings") {
            field("Your Name") {
                textfield(name)
            }
            field("Host") {
                textfield(host)
            }
            field("Port") {
                textfield(port)
            }
        }

        button("Start server") {
            action {
                serverController.serverName = name.value
                GlobalScope.launch {
                    val server = ChatServer(host.value, port.value.toInt())
                    server.start()
                    server.await()
                }
                replaceWith<MyServerView>()
            }
        }
        button("Start client") {
            action {
                clientController.clientName = name.value
                GlobalScope.launch {
                    ChatClient("${host.value}:${port.value}").use { client ->
                        client.start()
                    }
                }
                replaceWith<MyClientView>()
            }
        }
    }
}


fun main(args: Array<String>) {
    launch<MyApp>(args)
}