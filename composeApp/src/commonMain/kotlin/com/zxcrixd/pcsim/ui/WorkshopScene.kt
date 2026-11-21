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

private val Copper = Color(0xFFD29455)
private val BoardGreen = Color(0xFF244A35)
private val TraceGreen = Color(0xFF789064)

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
                val componentOrder = listOf(PartType.CPU, PartType.RAM, PartType.GPU, PartType.STORAGE, PartType.PSU)
                val componentSlot = componentOrder.firstOrNull { slots.getValue(it).contains(point) }
                val target = componentSlot ?: if (slots.getValue(PartType.MOTHERBOARD).contains(point)) PartType.MOTHERBOARD else null
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
            drawRect(Brush.verticalGradient(listOf(Color(0xFF302B24), Color(0xFF1B1A18), Color(0xFF24221E))))
            for (i in 0..12) {
                val x = width * i / 12f
                drawLine(Color(0x12D6B98B), Offset(x, 0f), Offset(x, height), 1f)
            }
            for (i in 0..7) {
                val y = height * i / 7f
                drawLine(Color(0x12D6B98B), Offset(0f, y), Offset(width, y), 1f)
            }

            // Warm wood workbench, monitor bezel and a subdued diagnostics screen.
            drawRoundRect(Color(0xFF795638), Offset(width * 0.08f, height * 0.83f), Size(width * 0.84f, height * 0.09f), CornerRadius(12f))
            drawRoundRect(Color(0xFF242321), Offset(width * 0.055f, height * 0.22f), Size(width * 0.18f, height * 0.34f), CornerRadius(12f))
            drawRoundRect(Color(0xFF29362D), Offset(width * 0.07f, height * 0.24f), Size(width * 0.15f, height * 0.29f), CornerRadius(8f))
            drawRoundRect(Color(0xFF667258), Offset(width * 0.07f, height * 0.24f), Size(width * 0.15f, height * 0.29f), CornerRadius(8f), style = Stroke(2f))
            drawText(textMeasurer, "POST", Offset(width * 0.105f, height * 0.31f), style = TextStyle(color = Color(0xFFD8B578), fontSize = 14.sp, fontWeight = FontWeight.Bold))
            drawText(textMeasurer, "READY", Offset(width * 0.097f, height * 0.39f), style = TextStyle(color = Color(0xFFC2B9A7), fontSize = 11.sp))

            val slots = slotRects(size)
            val caseRect = slots.getValue(PartType.CASE)
            val casePart = installed[PartType.CASE]
            val caseTint = if (casePart == null) Color(0xFF282A29) else partColor(PartType.CASE, casePart.score)
            drawRoundRect(caseTint, caseRect.topLeft, caseRect.size, CornerRadius(18f))
            drawRoundRect(Color(0xFF77756C), caseRect.topLeft, caseRect.size, CornerRadius(18f), style = Stroke(5f))

            val board = slots.getValue(PartType.MOTHERBOARD)
            val boardColor = if (installed.containsKey(PartType.MOTHERBOARD)) BoardGreen else Color(0xFF30382F)
            drawRoundRect(boardColor, board.topLeft, board.size, CornerRadius(12f))
            drawRoundRect(Color(0xFF7B795D), board.topLeft, board.size, CornerRadius(12f), style = Stroke(2f))
            repeat(5) { index ->
                val y = board.top + board.height * (0.18f + index * 0.13f)
                drawLine(TraceGreen.copy(alpha = 0.55f), Offset(board.left + 8f, y), Offset(board.right - 8f, y), 1f)
            }
            if (casePart != null) {
                drawText(textMeasurer, casePart.name, Offset(caseRect.left + 5f, caseRect.bottom - 19f), style = TextStyle(color = Color(0xFFF2E7D2), fontSize = 9.sp, fontWeight = FontWeight.Bold))
            }

            val displayOrder = listOf(PartType.CPU, PartType.RAM, PartType.GPU, PartType.STORAGE, PartType.PSU)
            for (type in displayOrder) {
                val rect = slots.getValue(type)
                val part = installed[type]
                val highlighted = selectedPart?.type == type
                if (part == null) {
                    drawRoundRect(Color(0x33262520), rect.topLeft, rect.size, CornerRadius(7f))
                    drawRoundRect(
                        if (highlighted) Copper else Color(0xFF8A806C),
                        rect.topLeft,
                        rect.size,
                        CornerRadius(7f),
                        style = Stroke(if (highlighted) 3f else 1.5f, pathEffect = if (highlighted) null else PathEffect.dashPathEffect(floatArrayOf(7f, 5f)))
                    )
                    drawText(textMeasurer, type.label.uppercase(), Offset(rect.left + 4f, rect.top + 4f), style = TextStyle(color = Color(0xFFE0D5C0), fontSize = 9.sp, fontWeight = FontWeight.Bold))
                } else {
                    val color = partColor(type, part.score)
                    drawRoundRect(color, rect.topLeft, rect.size, CornerRadius(7f))
                    drawRoundRect(Color(0xFFE5D7BF), rect.topLeft, rect.size, CornerRadius(7f), style = Stroke(1.5f))
                    drawText(textMeasurer, part.name, Offset(rect.left + 5f, rect.top + rect.height * 0.38f), style = TextStyle(color = Color(0xFFFFF7EA), fontSize = 9.sp, fontWeight = FontWeight.Bold))
                    when (type) {
                        PartType.GPU -> {
                            drawCircle(Color(0xFF242321), rect.height * 0.32f, Offset(rect.left + rect.width * 0.32f, rect.center.y))
                            drawCircle(Color(0xFF242321), rect.height * 0.32f, Offset(rect.left + rect.width * 0.68f, rect.center.y))
                            drawCircle(Copper, rect.height * 0.07f, Offset(rect.left + rect.width * 0.32f, rect.center.y))
                            drawCircle(Copper, rect.height * 0.07f, Offset(rect.left + rect.width * 0.68f, rect.center.y))
                        }
                        PartType.PSU -> drawCircle(Color(0xFF242321), rect.height * 0.28f, Offset(rect.right - rect.width * 0.22f, rect.center.y))
                        PartType.CPU -> drawRect(Color(0x66FFF1D1), Offset(rect.left + rect.width * 0.2f, rect.top + 3f), Size(rect.width * 0.6f, 2f))
                        else -> Unit
                    }
                }
            }
            drawLine(Color(0xFF9A8A70), Offset(caseRect.left + 4f, caseRect.top + 8f), Offset(caseRect.left + 4f, caseRect.bottom - 8f), 3f)
            drawLine(Copper, Offset(caseRect.left, caseRect.bottom - 4f), Offset(caseRect.right, caseRect.bottom - 4f), 4f)
        }
    }
}

private fun partColor(type: PartType, score: Int): Color {
    val base = when (type) {
        PartType.CPU -> Color(0xFFB18A5A)
        PartType.MOTHERBOARD -> Color(0xFF3E694B)
        PartType.RAM -> Color(0xFF9A5360)
        PartType.GPU -> Color(0xFF555650)
        PartType.STORAGE -> Color(0xFF85847A)
        PartType.PSU -> Color(0xFF41413E)
        PartType.CASE -> Color(0xFF56534D)
    }
    return if (score > 130) Color(
        red = (base.red + 0.12f).coerceAtMost(1f),
        green = (base.green + 0.07f).coerceAtMost(1f),
        blue = (base.blue + 0.03f).coerceAtMost(1f),
        alpha = base.alpha
    ) else base
}
