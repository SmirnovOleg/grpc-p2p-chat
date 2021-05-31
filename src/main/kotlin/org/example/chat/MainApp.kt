package org.example.chat

import javafx.scene.control.Alert
import org.example.chat.util.ExceptionEvent
import org.example.chat.view.InitView
import tornadofx.App
import tornadofx.DefaultErrorHandler
import tornadofx.launch
import tornadofx.runLater


class MyApp : App(InitView::class) {
    init {
        DefaultErrorHandler.filter = {
            it.consume()
            runLater {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "An error occurred"
                    isResizable = true
                    contentText = it.error.message
                    showAndWait()
                }
                fire(ExceptionEvent)
            }
        }
    }
}

fun main(args: Array<String>) {
    launch<MyApp>(args)
}