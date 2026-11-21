package com.zxcrixd.pcsim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zxcrixd.pcsim.BuildCheck
import com.zxcrixd.pcsim.GameState
import com.zxcrixd.pcsim.model.Catalog
import com.zxcrixd.pcsim.model.Order
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private val WorkshopColors = darkColorScheme(
    primary = Color(0xFFC8874A), onPrimary = Color(0xFF21160D),
    secondary = Color(0xFF8EAC91), background = Color(0xFF171716),
    onBackground = Color(0xFFEAE4DA), surface = Color(0xFF262522),
    onSurface = Color(0xFFEAE4DA), surfaceVariant = Color(0xFF34312B),
    onSurfaceVariant = Color(0xFFC1B7A8), error = Color(0xFFE27769)
)

private enum class Page(val title: String, val icon: String) {
    HOME("Главная", "⌂"), TASKS("Задания", "▤"), BUILD("Сборка ПК", "⚙"), SHOP("Магазин", "▦")
}

@Composable
fun App() {
    val game = remember { GameState() }
    var page by remember { mutableStateOf(Page.HOME) }
    var selectedPart by remember { mutableStateOf<Part?>(null) }
    MaterialTheme(colorScheme = WorkshopColors) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Header(game, page) { game.reset(); selectedPart = null; page = Page.HOME }
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f).fillMaxSize()) {
                        when (page) {
                            Page.HOME -> HomePage(game) { page = it }
                            Page.TASKS -> TasksPage(game) { task -> game.acceptTask(task); page = Page.BUILD }
                            Page.BUILD -> BuildPage(game, selectedPart) { selectedPart = null }
                            Page.SHOP -> ShopPage(game, selectedPart) { selectedPart = it; page = Page.BUILD }
                        }
                    }
                    Navigation(page) { page = it }
                }
            }
        }
    }
}

@Composable
private fun Header(game: GameState, page: Page, onReset: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF24231F), RoundedCornerShape(14.dp)).padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("PC WORKSHOP", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("${page.title}  ·  День ${game.day}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${game.money} ₽", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Ур. ${game.sellerLevel}  ·  ★ ${game.rating.toString().take(3)}  ·  ${game.sellerXp % 100}/100 XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onReset) { Text("Новая игра") }
    }
}

@Composable
private fun Navigation(current: Page, onNavigate: (Page) -> Unit) {
    Column(
        Modifier.width(150.dp).background(Color(0xFF24231F), RoundedCornerShape(14.dp)).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text("МЕНЮ", Modifier.padding(start = 10.dp, top = 8.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Page.entries.forEach { page ->
            val active = current == page
            Row(
                Modifier.fillMaxWidth().background(if (active) Color(0xFF5A4530) else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { onNavigate(page) }.padding(horizontal = 10.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(page.icon, color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(9.dp))
                Text(page.title, style = MaterialTheme.typography.labelMedium, color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("МАСТЕРСКАЯ\nPC Creator Simulator", Modifier.padding(10.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HomePage(game: GameState, onNavigate: (Page) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF302A21)), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxSize().padding(26.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("СВОЯ МАСТЕРСКАЯ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Собирай. Проверяй.\nЗарабатывай.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                    Text("Принимай заказы, подбирай совместимые детали и расти от новичка до мастера сборки.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { onNavigate(Page.BUILD) }) { Text("Продолжить смену  →") }
                }
                WorkshopIllustration(Modifier.weight(0.9f).fillMaxSize())
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryCard("Заказ дня", game.order.title, "${game.order.minScore}+ очков · бонус ${game.order.bonus} ₽", Modifier.weight(1f)) { onNavigate(Page.TASKS) }
            SummaryCard("Продавец", "Уровень ${game.sellerLevel} · ★ ${game.rating.toString().take(3)}", "${game.completedOrders} заказов выполнено", Modifier.weight(1f)) { onNavigate(Page.TASKS) }
            SummaryCard("Магазин", "${Catalog.parts.size} деталей", "Выбрать комплектующие →", Modifier.weight(1f)) { onNavigate(Page.SHOP) }
        }
    }
}

@Composable
private fun WorkshopIllustration(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val w = size.width; val h = size.height
        drawRoundRect(Color(0xFF6B4A31), androidx.compose.ui.geometry.Offset(w*.06f,h*.72f), androidx.compose.ui.geometry.Size(w*.88f,h*.12f), androidx.compose.ui.geometry.CornerRadius(16f))
        drawRoundRect(Color(0xFF242A2A), androidx.compose.ui.geometry.Offset(w*.24f,h*.12f), androidx.compose.ui.geometry.Size(w*.5f,h*.62f), androidx.compose.ui.geometry.CornerRadius(20f))
        drawRoundRect(Color(0xFF315344), androidx.compose.ui.geometry.Offset(w*.29f,h*.18f), androidx.compose.ui.geometry.Size(w*.4f,h*.5f), androidx.compose.ui.geometry.CornerRadius(12f))
        drawRoundRect(Color(0xFFB18A5A), androidx.compose.ui.geometry.Offset(w*.40f,h*.28f), androidx.compose.ui.geometry.Size(w*.16f,h*.14f), androidx.compose.ui.geometry.CornerRadius(8f))
        drawRoundRect(Color(0xFF9A5360), androidx.compose.ui.geometry.Offset(w*.59f,h*.25f), androidx.compose.ui.geometry.Size(w*.06f,h*.26f), androidx.compose.ui.geometry.CornerRadius(5f))
        drawRoundRect(Color(0xFF76776A), androidx.compose.ui.geometry.Offset(w*.38f,h*.51f), androidx.compose.ui.geometry.Size(w*.29f,h*.08f), androidx.compose.ui.geometry.CornerRadius(5f))
        drawCircle(Color(0xFFC8874A), h*.055f, androidx.compose.ui.geometry.Offset(w*.51f,h*.55f))
    }
}

@Composable
private fun SummaryCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TasksPage(game: GameState, onAccept: (Order) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("ЗАДАНИЯ ОТ КЛИЕНТОВ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("У каждого клиента свои требования, бюджет и бонус. Удачная сборка повышает рейтинг продавца.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            game.tasks.forEachIndexed { index, task ->
                Card(Modifier.weight(1f).fillMaxSize(), colors = CardDefaults.cardColors(containerColor = if (task.title == game.order.title) Color(0xFF302A21) else MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (task.title == game.order.title) "АКТИВНЫЙ ЗАКАЗ" else "ЗАКАЗ КЛИЕНТА ${index + 1}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(task.client, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(task.title, style = MaterialTheme.typography.titleSmall)
                        Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.weight(1f))
                        Text("Производительность: ${task.minScore}+", style = MaterialTheme.typography.labelMedium)
                        Text("Бюджет: ${task.budget} ₽", style = MaterialTheme.typography.labelMedium)
                        Text("Бонус: ${task.bonus} ₽", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = { onAccept(task) }, modifier = Modifier.fillMaxWidth()) { Text(if (task.title == game.order.title) "Собирать заказ" else "Взять заказ") }
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildPage(game: GameState, selectedPart: Part?, onPartPlaced: () -> Unit) {
    val check = game.check()
    LaunchedEffect(game.message) {
        if (game.message.startsWith("Установлена ") || " заменена на " in game.message) onPartPlaced()
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("СБОРКА ПК", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${game.order.client} · ${game.order.title} · бюджет ${game.order.budget} ₽", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Ур. ${game.sellerLevel} · ★ ${game.rating.toString().take(3)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        if (selectedPart != null) Text("Выбрано: ${selectedPart.name} — нажми на гнездо этой детали", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
        WorkshopScene(game, selectedPart, Modifier.fillMaxWidth().weight(1f))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(game.message, style = MaterialTheme.typography.labelMedium, color = if (game.messageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                Text("${check.score} очк · ${check.totalCost} ₽ · ${check.totalPower} Вт", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
            Button(onClick = { game.assemble() }, enabled = game.readyPc == null && check.canAssemble) { Text("POST / Собрать") }
            Spacer(Modifier.width(6.dp))
            Button(onClick = { game.sell() }, enabled = game.readyPc != null) { Text("Продать") }
            Spacer(Modifier.width(6.dp))
            OutlinedButton(onClick = { game.scrap() }, enabled = game.readyPc != null) { Text("Разобрать") }
        }
    }
}

@Composable
private fun ShopPage(game: GameState, selectedPart: Part?, onSelect: (Part) -> Unit) {
    var filter by remember { mutableStateOf<PartType?>(null) }
    val parts = if (filter == null) Catalog.parts else Catalog.byType(filter!!)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("МАГАЗИН КОМПЛЕКТУЮЩИХ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Выбор детали бесплатный — деньги списываются при установке.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Баланс ${game.money} ₽", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item { FilterChip(filter == null, { filter = null }, label = { Text("Все") }) }
            items(PartType.entries) { type -> FilterChip(filter == type, { filter = if (filter == type) null else type }, label = { Text(type.label) }) }
        }
        LazyRow(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            items(parts) { part -> ShopPartCard(part, part.requiredReputation > game.reputation, selectedPart?.id == part.id) { onSelect(part) } }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF302A21))) {
            Row(Modifier.fillMaxWidth().padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(selectedPart?.let { "Выбрано: ${it.name} · ${it.price} ₽" } ?: "Деталь не выбрана", Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                Text("Нажми на карточку, чтобы выбрать", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ShopPartCard(part: Part, locked: Boolean, selected: Boolean, onClick: () -> Unit) {
    val fill = if (selected) Color(0xFF5A4530) else MaterialTheme.colorScheme.surface
    Card(Modifier.width(190.dp).fillMaxSize().clickable(enabled = !locked, onClick = onClick), colors = CardDefaults.cardColors(containerColor = fill), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            PartSwatch(part.type, Modifier.fillMaxWidth().height(70.dp))
            Text(if (locked) "🔒 ${part.name}" else part.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(if (locked) "Откроется на реп. ${part.requiredReputation}" else "${part.price} ₽", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("${part.score} очков производительности", style = MaterialTheme.typography.labelSmall)
            Text(partSpec(part), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Text(if (selected) "ВЫБРАНО" else "Выбрать →", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun PartSwatch(type: PartType, modifier: Modifier = Modifier) {
    val color = when (type) {
        PartType.CPU -> Color(0xFFB18A5A)
        PartType.MOTHERBOARD -> Color(0xFF3E694B)
        PartType.RAM -> Color(0xFF9A5360)
        PartType.GPU -> Color(0xFF625C50)
        PartType.STORAGE -> Color(0xFF76776A)
        PartType.PSU -> Color(0xFF494A47)
        PartType.CASE -> Color(0xFF6B4A31)
    }
    Box(modifier.background(color, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
        Text(type.label.uppercase(), color = Color(0xFFE8DFD1), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
    }
}

private fun partSpec(part: Part): String = when (part.type) {
    PartType.CPU -> "Сокет ${part.socket} · ${part.power} Вт"
    PartType.MOTHERBOARD -> "${part.socket} · ${part.ramType}"
    PartType.RAM -> part.ramType ?: "ОЗУ"
    PartType.PSU -> "Мощность ${part.watt} Вт"
    else -> part.type.label
}
