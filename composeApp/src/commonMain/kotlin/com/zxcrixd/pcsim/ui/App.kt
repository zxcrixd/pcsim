package com.zxcrixd.pcsim.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private val GameColors = darkColorScheme(
    primary = Color(0xFF55D9C7),
    onPrimary = Color(0xFF06201D),
    secondary = Color(0xFF9B7CFF),
    background = Color(0xFF0C1220),
    onBackground = Color(0xFFE9F1FA),
    surface = Color(0xFF172235),
    onSurface = Color(0xFFE9F1FA),
    surfaceVariant = Color(0xFF223149),
    onSurfaceVariant = Color(0xFFB6C6D8),
    error = Color(0xFFFF6F78)
)

@Composable
fun App() {
    val game = remember { GameState() }
    MaterialTheme(colorScheme = GameColors) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            WorkshopGame(game)
        }
    }
}

@Composable
private fun WorkshopGame(game: GameState) {
    var selectedPart by remember { mutableStateOf<Part?>(null) }
    var filter by remember { mutableStateOf<PartType?>(null) }
    LaunchedEffect(game.message) {
        if (game.message.startsWith("Установлена ") || " заменена на " in game.message) {
            selectedPart = null
        }
    }
    val check = game.check()
    val parts = if (filter == null) Catalog.parts else Catalog.byType(filter!!)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Hud(game)
        WorkshopScene(
            game = game,
            selectedPart = selectedPart,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        StatusLine(game, check, selectedPart)
        CategoryBar(filter) { filter = it }
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(84.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(parts) { part ->
                PartCard(
                    part = part,
                    locked = part.requiredReputation > game.reputation,
                    selected = selectedPart?.id == part.id,
                    onClick = {
                        if (part.type == PartType.CASE) {
                            game.install(part)
                            selectedPart = null
                        } else if (part.requiredReputation <= game.reputation) {
                            selectedPart = if (selectedPart?.id == part.id) null else part
                            game.hint("Выбрано: ${part.name}. Нажми на подходящий слот в корпусе")
                        }
                    }
                )
            }
        }
        Actions(game, check)
    }
}

@Composable
private fun Hud(game: GameState) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text("PC CREATOR WORKSHOP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            Text("День ${game.day} · репутация ${game.reputation}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${game.money} ₽", fontWeight = FontWeight.Bold)
            Text("прибыль ${game.totalProfit} ₽", style = MaterialTheme.typography.labelSmall)
        }
        TextButton(onClick = { game.reset() }) { Text("⟲") }
    }
}

@Composable
private fun StatusLine(game: GameState, check: BuildCheck, selected: Part?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(game.message, style = MaterialTheme.typography.labelMedium, color = if (game.messageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                Text("Сборка: ${check.score} очк · ${check.totalCost} ₽ · ${check.totalPower} Вт", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            if (selected != null) Text("▶ ${selected.type.label}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun CategoryBar(selected: PartType?, onSelect: (PartType?) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        item { FilterChip(selected == null, { onSelect(null) }, label = { Text("Все") }) }
        items(PartType.entries) { type ->
            FilterChip(selected == type, { onSelect(if (selected == type) null else type) }, label = { Text(type.label) })
        }
    }
}

@Composable
private fun PartCard(part: Part, locked: Boolean, selected: Boolean, onClick: () -> Unit) {
    val background = when {
        selected -> MaterialTheme.colorScheme.primary
        locked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surface
    }
    Card(
        modifier = Modifier.size(width = 154.dp, height = 78.dp).clickable(enabled = !locked, onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)) {
            Text(if (locked) "🔒 ${part.name}" else part.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(3.dp))
            Text(if (locked) "реп. ${part.requiredReputation}" else "${part.price} ₽ · ${part.score} очк", style = MaterialTheme.typography.labelSmall, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
            Text(
                when (part.type) {
                    PartType.CPU -> "socket ${part.socket}"
                    PartType.MOTHERBOARD -> "${part.socket} · ${part.ramType}"
                    PartType.RAM -> part.ramType ?: "память"
                    PartType.PSU -> "${part.watt} Вт"
                    else -> part.type.label
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Actions(game: GameState, check: BuildCheck) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Button(onClick = { game.assemble() }, enabled = game.readyPc == null && check.canAssemble, modifier = Modifier.weight(1f)) { Text("POST / СОБРАТЬ") }
        Button(onClick = { game.sell() }, enabled = game.readyPc != null, modifier = Modifier.weight(1f)) { Text("ПРОДАТЬ") }
        OutlinedButton(onClick = { game.scrap() }, enabled = game.readyPc != null, modifier = Modifier.weight(0.72f)) { Text("РАЗОБРАТЬ") }
    }
}
