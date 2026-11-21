package com.zxcrixd.pcsim.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcrixd.pcsim.GameState
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private val Neon = Color(0xFF54E6D0)
private val BoardGreen = Color(0xFF164B3B)

private fun slotRects(size: Size): Map<PartType, Rect> {
    val w = size.width
    val h = size.height
    val left = w * 0.29f
    val top = h * 0.055f
    val cw = w * 0.43f
    val ch = h * 0.88f
    fun r(x1: Float, y1: Float, x2: Float, y2: Float) = Rect(
        left + cw * x1, top + ch * y1, left + cw * x2, top + ch * y2
    )
    return mapOf(
        PartType.CPU to r(0.18f, 0.13f, 0.48f, 0.34f),
        PartType.RAM to r(0.58f, 0.12f, 0.84f, 0.40f),
        PartType.GPU to r(0.10f, 0.45f, 0.90f, 0.62f),
        PartType.STORAGE to r(0.09f, 0.70f, 0.43f, 0.86f),
        PartType.PSU to r(0.55f, 0.69f, 0.91f, 0.88f),
        PartType.MOTHERBOARD to r(0.04f, 0.05f, 0.96f, 0.94f),
        PartType.CASE to Rect(left, top, left + cw, top + ch)
    )
}

@Composable
fun WorkshopScene(
    game: GameState,
    selectedPart: Part?,
    modifier: Modifier = Modifier
) {
    val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()
    val installed = game.installed.toMap()
    Box(
        modifier = modifier
            .pointerInput(selectedPart, installed) {
                detectTapGestures { point ->
                    val slots = slotRects(Size(size.width.toFloat(), size.height.toFloat()))
                    val hitOrder = listOf(
                        PartType.CPU, PartType.RAM, PartType.GPU,
                        PartType.STORAGE, PartType.PSU, PartType.MOTHERBOARD
                    )
                    val target = hitOrder.firstOrNull { slots.getValue(it).contains(point) }
                    when {
                        target == null -> game.hint("Нажми на слот внутри корпуса")
                        selectedPart != null -> game.place(selectedPart, target)
                        installed.containsKey(target) -> game.remove(target)
                        else -> game.hint("Сначала выбери деталь в магазине")
                    }
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF202C40))))
            for (i in 0..12) {
                val x = w * i / 12f
                drawLine(Color(0x1839D9C5), Offset(x, 0f), Offset(x, h), 1f)
            }
            for (i in 0..7) {
                val y = h * i / 7f
                drawLine(Color(0x1839D9C5), Offset(0f, y), Offset(w, y), 1f)
            }
            // Workbench and monitor.
            drawRoundRect(Color(0xFF604332), topLeft = Offset(w * 0.08f, h * 0.83f), size = Size(w * 0.84f, h * 0.09f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f))
            drawRoundRect(Color(0xFF29364A), topLeft = Offset(w * 0.055f, h * 0.22f), size = Size(w * 0.18f, h * 0.34f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f))
            drawRoundRect(Color(0xFF10283C), topLeft = Offset(w * 0.07f, h * 0.24f), size = Size(w * 0.15f, h * 0.29f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f))
            drawRoundRect(Neon.copy(alpha = 0.23f), topLeft = Offset(w * 0.07f, h * 0.24f), size = Size(w * 0.15f, h * 0.29f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f), style = Stroke(2f))
            drawText(textMeasurer, "POST", topLeft = Offset(w * 0.105f, h * 0.31f), style = TextStyle(color = Neon, fontSize = 14.sp, fontWeight = FontWeight.Bold))
            drawText(textMeasurer, "READY", topLeft = Offset(w * 0.097f, h * 0.39f), style = TextStyle(color = Color(0xFF8BA2BE), fontSize = 11.sp))

            val slots = slotRects(size)
            val caseRect = slots.getValue(PartType.CASE)
            drawRoundRect(Color(0xFF111722), caseRect.topLeft, caseRect.size, androidx.compose.ui.geometry.CornerRadius(18f))
            drawRoundRect(Color(0xFF202A38), caseRect.topLeft, caseRect.size, androidx.compose.ui.geometry.CornerRadius(18f), style = Stroke(5f))
            val board = slots.getValue(PartType.MOTHERBOARD)
            drawRoundRect(BoardGreen, board.topLeft, board.size, androidx.compose.ui.geometry.CornerRadius(12f))
            drawRoundRect(Color(0xFF33866B), board.topLeft, board.size, androidx.compose.ui.geometry.CornerRadius(12f), style = Stroke(2f))
            // PCB traces.
            repeat(5) { i ->
                val y = board.top + board.height * (0.18f + i * 0.13f)
                drawLine(Color(0x6649C99A), Offset(board.left + 8f, y), Offset(board.right - 8f, y), 1f)
            }

            val displayOrder = listOf(PartType.CPU, PartType.RAM, PartType.GPU, PartType.STORAGE, PartType.PSU)
            for (type in displayOrder) {
                val rect = slots.getValue(type)
                val part = installed[type]
                if (part == null) {
                    drawRoundRect(Color(0x2226D9C5), rect.topLeft, rect.size, androidx.compose.ui.geometry.CornerRadius(7f))
                    drawRoundRect(Neon.copy(alpha = 0.65f), rect.topLeft, rect.size, androidx.compose.ui.geometry.CornerRadius(7f), style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 5f))))
                    drawText(textMeasurer, type.label.uppercase(), topLeft = Offset(rect.left + 4f, rect.top + 4f), style = TextStyle(color = Color(0xFFB7C8D8), fontSize = 9.sp, fontWeight = FontWeight.Bold))
                } else {
                    val c = partColor(type, part.score)
                    drawRoundRect(c, rect.topLeft, rect.size, androidx.compose.ui.geometry.CornerRadius(7f))
                    drawRoundRect(Color.White.copy(alpha = 0.75f), rect.topLeft, rect.size, androidx.compose.ui.geometry.CornerRadius(7f), style = Stroke(1.5f))
                    drawText(textMeasurer, part.name, topLeft = Offset(rect.left + 5f, rect.top + rect.height * 0.38f), style = TextStyle(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                    when (type) {
                        PartType.GPU -> {
                            drawCircle(Color(0xFF172033), rect.height * 0.32f, Offset(rect.left + rect.width * 0.32f, rect.center.y))
                            drawCircle(Color(0xFF172033), rect.height * 0.32f, Offset(rect.left + rect.width * 0.68f, rect.center.y))
                            drawCircle(Neon, rect.height * 0.08f, Offset(rect.left + rect.width * 0.32f, rect.center.y))
                            drawCircle(Neon, rect.height * 0.08f, Offset(rect.left + rect.width * 0.68f, rect.center.y))
                        }
                        PartType.PSU -> drawCircle(Color(0xFF19202C), rect.height * 0.28f, Offset(rect.right - rect.width * 0.22f, rect.center.y))
                        PartType.CPU -> drawRect(Color(0x66FFFFFF), Offset(rect.left + rect.width * 0.2f, rect.top + 3f), Size(rect.width * 0.6f, 2f))
                        else -> Unit
                    }
                }
            }
            // Glass reflection and RGB strip.
            drawLine(Color(0x559DEBFF), Offset(caseRect.left + 4f, caseRect.top + 8f), Offset(caseRect.left + 4f, caseRect.bottom - 8f), 3f)
            drawLine(Neon, Offset(caseRect.left, caseRect.bottom - 4f), Offset(caseRect.right, caseRect.bottom - 4f), 4f)
        }
    }
}

private fun partColor(type: PartType, score: Int): Color {
    val base = when (type) {
        PartType.CPU -> Color(0xFF3978E8)
        PartType.MOTHERBOARD -> Color(0xFF248C62)
        PartType.RAM -> Color(0xFFE44D78)
        PartType.GPU -> Color(0xFF874EE8)
        PartType.STORAGE -> Color(0xFF29A9A0)
        PartType.PSU -> Color(0xFF65758B)
        PartType.CASE -> Color(0xFF29364A)
    }
    return if (score > 130) base.copy(red = (base.red + 0.18f).coerceAtMost(1f), green = (base.green + 0.08f).coerceAtMost(1f)) else base
}
