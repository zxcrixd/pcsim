package com.zxcrixd.pcsim

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.zxcrixd.pcsim.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PC Creator Simulator",
        undecorated = true,
        state = rememberWindowState(size = DpSize(1280.dp, 800.dp))
    ) {
        App()
    }
}
