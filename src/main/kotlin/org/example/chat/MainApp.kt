package org.example.chat

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.chat.grpc.ChatClient
import org.example.chat.grpc.ChatServer
import org.example.chat.ui.MyController
import org.example.chat.ui.MyView
import tornadofx.*
import java.net.InetAddress

class MyApp : App(InitView::class)

class InitView : View() {
    private val controller: MyController by inject()

    var name = SimpleStringProperty("simp")
    var host = SimpleStringProperty(InetAddress.getLocalHost().hostAddress)
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
                controller.userName = name.value

                GlobalScope.launch {
                    val server = ChatServer(host.value, port.value.toInt())
                    server.start()
                    server.await()
                }

                replaceWith<MyView>()
            }
        }

        button("Start client") {
            action {
                controller.userName = name.value

                GlobalScope.launch {
                    ChatClient("${host.value}:${port.value}").use { client ->
                        client.start()
                    }
                }

                replaceWith<MyView>()
            }
        }
    }
}


fun main(args: Array<String>) {
    launch<MyApp>(args)
}