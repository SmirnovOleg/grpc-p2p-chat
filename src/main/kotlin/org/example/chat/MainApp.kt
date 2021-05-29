package org.example.chat

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
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

    var name = SimpleStringProperty("gRPC lover")
    var host = SimpleStringProperty("localhost")
    var port = SimpleStringProperty("50051")
    private val toggleGroup = ToggleGroup()

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
        hbox {
            radiobutton("Server", toggleGroup).also {
                it.isSelected = true
            }
            radiobutton("Client", toggleGroup).also {
                it.hboxConstraints { marginLeft = 15.0 }
            }
        }

        hbox {
            button("Start") {
                action {
                    if (toggleGroup.toggles[0].isSelected) {
                        serverController.serverName = name.value
                        GlobalScope.launch {
                            val server = ChatServer(host.value, port.value.toInt())
                            server.start()
                            server.await()
                        }
                        replaceWith<MyServerView>()
                    } else {
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
            alignment = Pos.CENTER_RIGHT
            vboxConstraints {
                margin = Insets(5.0)
            }
        }
    }
}


fun main(args: Array<String>) {
    launch<MyApp>(args)
}