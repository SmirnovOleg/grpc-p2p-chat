package org.example.chat.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.example.chat.controller.ChatController
import org.example.chat.util.ExceptionEvent
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ChatView : View() {
    private val controller: ChatController by inject()
    var chat = SimpleStringProperty("")
    var input = SimpleStringProperty("Type your message here")

    init {
        subscribe<ExceptionEvent> {
            runLater {
                replaceWith<InitView>()
            }
        }
    }

    override fun onDock() {
        primaryStage.width = 500.0
        primaryStage.height = 400.0
    }

    override val root = vbox {
        textarea(chat) {
            GlobalScope.launch {
                controller.receiveChannel.receiveAsFlow().collect { response ->
                    chat.value += "(${response.time}) [${response.name}]: ${response.message}\n"
                }
            }
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
        }.isEditable = false

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
                            controller.sendMessage(input.value)
                        }
                        val time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
                        chat.value += "($time) [you]: ${input.value}\n"
                        input.value = ""
                    }
                }
            }
            alignment = Pos.CENTER_RIGHT
            vboxConstraints {
                margin = Insets(10.0)
            }
        }
    }
}