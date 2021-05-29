package org.example.chat.ui

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MyView : View() {
    private val controller: MyController by inject()

    var chat = SimpleStringProperty("")
    var input = SimpleStringProperty("Type your message here")

    override val root = vbox {
        textarea(chat) {
            GlobalScope.launch {
                controller.receiveChannel.receiveAsFlow().collect { response ->
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
                    controller.sendToServer(input.value)
                }
                val time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
                chat.value += "($time) you: ${input.value}\n"
                input.value = ""
            }
        }
    }
}