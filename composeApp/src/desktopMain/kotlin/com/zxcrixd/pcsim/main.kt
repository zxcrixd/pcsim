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
        state = rememberWindowState(size = DpSize(440.dp, 840.dp))
    ) {
        App()
    }
}
