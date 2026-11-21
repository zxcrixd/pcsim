package com.zxcrixd.pcsim.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.unit.sp
import com.zxcrixd.pcsim.GameState
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private val Neon = Color(0xFF54E6D0)
private val BoardGreen = Color(0xFF164B3B)

private fun slotRects(size: Size): Map<PartType, Rect> {
    val left = size.width * 0.29f
    val top = size.height * 0.055f
    val caseWidth = size.width * 0.43f
    val caseHeight = size.height * 0.88f
    fun relative(x1: Float, y1: Float, x2: Float, y2: Float) = Rect(
        left + caseWidth * x1,
        top + caseHeight * y1,
        left + caseWidth * x2,
        top + caseHeight * y2
    )
    return mapOf(
        PartType.CPU to relative(0.18f, 0.13f, 0.48f, 0.34f),
        PartType.RAM to relative(0.58f, 0.12f, 0.84f, 0.40f),
        PartType.GPU to relative(0.10f, 0.45f, 0.90f, 0.62f),
        PartType.STORAGE to relative(0.09f, 0.70f, 0.43f, 0.86f),
        PartType.PSU to relative(0.55f, 0.69f, 0.91f, 0.88f),
        PartType.MOTHERBOARD to relative(0.04f, 0.05f, 0.96f, 0.94f),
        PartType.CASE to Rect(left, top, left + caseWidth, top + caseHeight)
    )
}

@Composable
fun WorkshopScene(game: GameState, selectedPart: Part?, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val installed = game.installed.toMap()
    Box(
        modifier = modifier.pointerInput(selectedPart, installed) {
            detectTapGestures { point ->
                val slots = slotRects(Size(size.width.toFloat(), size.height.toFloat()))
                val targetOrder = listOf(
                    PartType.CPU,
                    PartType.RAM,
                    PartType.GPU,
                    PartType.STORAGE,
                    PartType.PSU,
                    PartType.MOTHERBOARD
                )
                val target = targetOrder.firstOrNull { slots.getValue(it).contains(point) }
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
            val width = size.width
            val height = size.height
            drawRect(Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF202C40))))
            for (i in 0..12) {
                val x = width * i / 12f
                drawLine(Color(0x1839D9C5), Offset(x, 0f), Offset(x, height), 1f)
            }
            for (i in 0..7) {
                val y = height * i / 7f
                drawLine(Color(0x1839D9C5), Offset(0f, y), Offset(width, y), 1f)
            }

            drawRoundRect(
                Color(0xFF604332),
                topLeft = Offset(width * 0.08f, height * 0.83f),
                size = Size(width * 0.84f, height * 0.09f),
                cornerRadius = CornerRadius(12f)
            )
            drawRoundRect(
                Color(0xFF29364A),
                topLeft = Offset(width * 0.055f, height * 0.22f),
                size = Size(width * 0.18f, height * 0.34f),
                cornerRadius = CornerRadius(12f)
            )
            drawRoundRect(
                Color(0xFF10283C),
                topLeft = Offset(width * 0.07f, height * 0.24f),
                size = Size(width * 0.15f, height * 0.29f),
                cornerRadius = CornerRadius(8f)
            )
            drawRoundRect(
                Neon.copy(alpha = 0.23f),
                topLeft = Offset(width * 0.07f, height * 0.24f),
                size = Size(width * 0.15f, height * 0.29f),
                cornerRadius = CornerRadius(8f),
                style = Stroke(2f)
            )
            drawText(
                textMeasurer,
                "POST",
                topLeft = Offset(width * 0.105f, height * 0.31f),
                style = TextStyle(color = Neon, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            drawText(
                textMeasurer,
                "READY",
                topLeft = Offset(width * 0.097f, height * 0.39f),
                style = TextStyle(color = Color(0xFF8BA2BE), fontSize = 11.sp)
            )

            val slots = slotRects(size)
            val caseRect = slots.getValue(PartType.CASE)
            drawRoundRect(Color(0xFF111722), caseRect.topLeft, caseRect.size, CornerRadius(18f))
            drawRoundRect(Color(0xFF202A38), caseRect.topLeft, caseRect.size, CornerRadius(18f), style = Stroke(5f))
            val board = slots.getValue(PartType.MOTHERBOARD)
            drawRoundRect(BoardGreen, board.topLeft, board.size, CornerRadius(12f))
            drawRoundRect(Color(0xFF33866B), board.topLeft, board.size, CornerRadius(12f), style = Stroke(2f))
            repeat(5) { index ->
                val y = board.top + board.height * (0.18f + index * 0.13f)
                drawLine(Color(0x6649C99A), Offset(board.left + 8f, y), Offset(board.right - 8f, y), 1f)
            }

            val displayOrder = listOf(PartType.CPU, PartType.RAM, PartType.GPU, PartType.STORAGE, PartType.PSU)
            for (type in displayOrder) {
                val rect = slots.getValue(type)
                val part = installed[type]
                if (part == null) {
                    drawRoundRect(Color(0x2226D9C5), rect.topLeft, rect.size, CornerRadius(7f))
                    drawRoundRect(Neon.copy(alpha = 0.65f), rect.topLeft, rect.size, CornerRadius(7f), style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 5f))))
                    drawText(textMeasurer, type.label.uppercase(), topLeft = Offset(rect.left + 4f, rect.top + 4f), style = TextStyle(color = Color(0xFFB7C8D8), fontSize = 9.sp, fontWeight = FontWeight.Bold))
                } else {
                    val color = partColor(type, part.score)
                    drawRoundRect(color, rect.topLeft, rect.size, CornerRadius(7f))
                    drawRoundRect(Color.White.copy(alpha = 0.75f), rect.topLeft, rect.size, CornerRadius(7f), style = Stroke(1.5f))
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
    return if (score > 130) Color(
        red = (base.red + 0.18f).coerceAtMost(1f),
        green = (base.green + 0.08f).coerceAtMost(1f),
        blue = base.blue,
        alpha = base.alpha
    ) else base
}
