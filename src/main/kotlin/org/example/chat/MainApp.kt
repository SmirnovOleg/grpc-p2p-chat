package org.example.chat

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
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

    var name = SimpleStringProperty("gRPC lover")
    var host = SimpleStringProperty(InetAddress.getLocalHost().hostAddress)
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
                        controller.userName = name.value
                        GlobalScope.launch {
                            val server = ChatServer(host.value, port.value.toInt())
                            server.start()
                            server.await()
                        }
                        replaceWith<MyView>()
                    } else {
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