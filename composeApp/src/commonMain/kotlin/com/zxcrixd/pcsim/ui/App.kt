package com.zxcrixd.pcsim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zxcrixd.pcsim.BuildCheck
import com.zxcrixd.pcsim.GameState
import com.zxcrixd.pcsim.model.Catalog
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private val Palette = darkColorScheme(
    primary = Color(0xFF7C6CFF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF2ED9C3),
    background = Color(0xFF12121A),
    onBackground = Color(0xFFE6E6F0),
    surface = Color(0xFF1C1C26),
    onSurface = Color(0xFFE6E6F0),
    surfaceVariant = Color(0xFF272735),
    onSurfaceVariant = Color(0xFFB6B6C8),
    error = Color(0xFFFF6B6B)
)

@Composable
fun App() {
    val game = remember { GameState() }
    MaterialTheme(colorScheme = Palette) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            GameScreen(game)
        }
    }
}

@Composable
private fun GameScreen(game: GameState) {
    val check = game.check()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Header(game)
        Spacer(Modifier.height(10.dp))
        OrderCard(game)
        Spacer(Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PartType.entries) { type ->
                PartCategory(game, type)
            }
        }
        Spacer(Modifier.height(10.dp))
        Footer(game, check)
    }
}

@Composable
private fun Header(game: GameState) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "PC Creator Simulator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "День ${game.day} · репутация ${game.reputation}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${game.money} ₽",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("прибыль ${game.totalProfit} ₽", style = MaterialTheme.typography.labelSmall)
        }
        TextButton(onClick = { game.reset() }) { Text("⟲") }
    }
}

@Composable
private fun OrderCard(game: GameState) {
    val order = game.order
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "ЗАКАЗ ДНЯ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                order.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "нужно ≥ ${order.minScore} очк · бонус ${order.bonus} ₽",
                style = MaterialTheme.typography.labelSmall
            )
            val pc = game.readyPc
            if (pc != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Готовый ПК: ${pc.score} очк · себестоимость ${pc.cost} ₽",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun PartCategory(game: GameState, type: PartType) {
    val selected = game.selected[type]
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    type.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    selected?.name ?: "не выбрано",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Catalog.byType(type).forEach { part ->
                    PartChip(
                        part = part,
                        selected = part.id == selected?.id,
                        locked = game.reputation < part.requiredReputation,
                        onClick = { game.select(part) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartChip(part: Part, selected: Boolean, locked: Boolean, onClick: () -> Unit) {
    val background = when {
        selected -> MaterialTheme.colorScheme.primary
        locked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val titleColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        locked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(enabled = !locked, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(part.name, style = MaterialTheme.typography.labelMedium, color = titleColor)
        Text(
            text = when {
                locked -> "закрыто · реп. ${part.requiredReputation}"
                part.price == 0 -> "в комплекте"
                else -> "${part.price} ₽"
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            else MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun Footer(game: GameState, check: BuildCheck) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = game.message,
            style = MaterialTheme.typography.labelMedium,
            color = if (game.messageError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))

        val pc = game.readyPc
        if (pc == null) {
            Button(
                onClick = { game.assemble() },
                enabled = check.canAssemble,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Собрать ПК · ${check.totalCost} ₽ · ${check.score} очк")
            }
            if (!check.canAssemble) {
                Spacer(Modifier.height(4.dp))
                Text(
                    check.errors.firstOrNull() ?: "Выбери все комплектующие",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { game.sell() }, modifier = Modifier.weight(1f)) {
                    Text("Продать")
                }
                OutlinedButton(onClick = { game.scrap() }, modifier = Modifier.weight(1f)) {
                    Text("Разобрать")
                }
            }
        }
    }
}
