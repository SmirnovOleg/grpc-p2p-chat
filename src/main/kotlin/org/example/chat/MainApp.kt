package org.example.chat

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ToggleGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.chat.ui.MyController
import org.example.chat.ui.MyView
import tornadofx.*
import java.net.InetAddress

object ExceptionEvent : FXEvent(EventBus.RunOn.BackgroundThread)

class MyApp : App(InitView::class) {
    init {
        DefaultErrorHandler.filter = {
            it.consume()
            runLater {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "An error occured"
                    isResizable = true
                    contentText = it.error.message
                    showAndWait()
                }
                fire(ExceptionEvent)
            }
        }
    }
}

class InitView : View() {
    private val controller: MyController by inject()

    var name = SimpleStringProperty("gRPC lover")
    var host = SimpleStringProperty(InetAddress.getLocalHost().hostAddress)
    var port = SimpleStringProperty("50051")
    private val toggleGroup = ToggleGroup()

    override fun onDock() {
        primaryStage.width = 250.0
        primaryStage.height = 260.0
    }

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
                    GlobalScope.launch {
                        if (toggleGroup.toggles[0].isSelected)
                            controller.startServer(name.value, host.value, port.value.toInt())
                        else
                            controller.startClient(name.value, host.value, port.value)
                    }
                    replaceWith<MyView>()
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