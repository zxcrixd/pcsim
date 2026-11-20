package com.zxcrixd.pcsim

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zxcrixd.pcsim.model.Order
import com.zxcrixd.pcsim.model.Orders
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private const val START_MONEY = 1500
private const val RENT = 40

data class BuildCheck(
    val complete: Boolean,
    val totalCost: Int,
    val totalPower: Int,
    val score: Int,
    val errors: List<String>
) {
    val canAssemble: Boolean get() = complete && errors.isEmpty()
}

data class ReadyPc(val score: Int, val cost: Int)

class GameState {
    var money by mutableStateOf(START_MONEY)
        private set
    var reputation by mutableStateOf(0)
        private set
    var day by mutableStateOf(1)
        private set
    var totalProfit by mutableStateOf(0)
        private set
    var message by mutableStateOf("Собери первый ПК и продай его. Удачи!")
        private set
    var messageError by mutableStateOf(false)
        private set
    var order by mutableStateOf(Orders.forDay(1, 0))
        private set
    var readyPc by mutableStateOf<ReadyPc?>(null)
        private set

    val selected = mutableStateMapOf<PartType, Part>()

    fun select(part: Part) {
        if (readyPc != null) {
            note("Сначала распродай собранный ПК", true)
            return
        }
        if (reputation < part.requiredReputation) {
            note("Нужна репутация ${part.requiredReputation} для ${part.name}", true)
            return
        }
        if (selected[part.type]?.id == part.id) {
            selected.remove(part.type)
        } else {
            selected[part.type] = part
        }
    }

    fun check(): BuildCheck {
        val errors = mutableListOf<String>()
        val missing = PartType.entries.filter { selected[it] == null }
        if (missing.isNotEmpty()) {
            errors += "Не выбрано: " + missing.joinToString(", ") { it.label }
        }

        val cpu = selected[PartType.CPU]
        val mb = selected[PartType.MOTHERBOARD]
        val ram = selected[PartType.RAM]
        val psu = selected[PartType.PSU]

        if (cpu != null && mb != null && cpu.socket != mb.socket) {
            errors += "Сокет ${cpu.socket} не подходит к плате ${mb.socket}"
        }
        if (ram != null && mb != null && ram.ramType != mb.ramType) {
            errors += "Память ${ram.ramType} не подходит к плате (${mb.ramType})"
        }

        val cost = selected.values.sumOf { it.price }
        val power = selected.values.sumOf { it.power }

        if (psu != null && psu.watt < power) {
            errors += "Блок питания: ${psu.watt} Вт < нужно $power Вт"
        }
        if (cost > money) {
            errors += "Не хватает денег: нужно $cost ₽, есть $money ₽"
        }

        return BuildCheck(
            complete = missing.isEmpty(),
            totalCost = cost,
            totalPower = power,
            score = selected.values.sumOf { it.score },
            errors = errors
        )
    }

    fun assemble() {
        if (readyPc != null) {
            note("Сначала распродай собранный ПК", true)
            return
        }
        val c = check()
        val firstError = c.errors.firstOrNull()
        if (firstError != null) {
            note(firstError, true)
            return
        }

        money -= c.totalCost
        readyPc = ReadyPc(score = c.score, cost = c.totalCost)
        selected.clear()
        note("ПК собран за ${c.totalCost} ₽ · производительность ${c.score} очк.")
    }

    fun sell() {
        val pc = readyPc ?: return
        val orderBonus = order.bonus
        val orderMin = order.minScore
        val base = (pc.cost * 1.35).toInt() + pc.score / 2
        val meets = pc.score >= orderMin
        val price = if (meets) base + orderBonus else (base * 0.8).toInt()

        money += price - RENT
        totalProfit += price - pc.cost - RENT
        reputation += if (meets) 2 else 1
        day += 1
        readyPc = null
        order = Orders.forDay(day, reputation)

        if (meets) {
            note("Заказ выполнен! +$price ₽ (бонус $orderBonus ₽), аренда -$RENT ₽")
        } else {
            note("ПК ушёл дешевле: +$price ₽, аренда -$RENT ₽", true)
        }
    }

    fun scrap() {
        val pc = readyPc ?: return
        val refund = pc.cost / 2
        money += refund
        readyPc = null
        note("ПК разобран на запчасти, возврат $refund ₽", true)
    }

    fun reset() {
        money = START_MONEY
        reputation = 0
        day = 1
        totalProfit = 0
        readyPc = null
        selected.clear()
        order = Orders.forDay(1, 0)
        note("Новая игра начата")
    }

    private fun note(text: String, isError: Boolean = false) {
        message = text
        messageError = isError
    }
}
