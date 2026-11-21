package com.zxcrixd.pcsim

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zxcrixd.pcsim.model.Orders
import com.zxcrixd.pcsim.model.Part
import com.zxcrixd.pcsim.model.PartType

private const val START_MONEY = 1200
private const val DAILY_RENT = 30

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
    var message by mutableStateOf("Выбери деталь в магазине, затем установи её в корпус")
        private set
    var messageError by mutableStateOf(false)
        private set
    var order by mutableStateOf(Orders.forDay(1, 0))
        private set
    var readyPc by mutableStateOf<ReadyPc?>(null)
        private set

    val installed = mutableStateMapOf<PartType, Part>()

    fun place(part: Part, target: PartType) {
        if (part.type != target) {
            note("${part.type.label} сюда не устанавливается — найди подходящий слот", true)
            return
        }
        install(part)
    }

    fun install(part: Part) {
        if (readyPc != null) {
            note("Сначала продай или разбери собранный ПК", true)
            return
        }
        if (part.requiredReputation > reputation) {
            note("Эта деталь откроется при репутации ${part.requiredReputation}", true)
            return
        }
        val old = installed[part.type]
        if (old?.id == part.id) {
            note("${part.name} уже установлена")
            return
        }
        val charge = part.price - (old?.price ?: 0)
        if (charge > money) {
            note("Не хватает денег: нужно ещё $charge ₽", true)
            return
        }
        money -= charge
        installed[part.type] = part
        note(if (old == null) "Установлена ${part.name}" else "${old.name} заменена на ${part.name}")
    }

    fun remove(type: PartType) {
        if (readyPc != null) {
            note("Сначала разбери готовый ПК", true)
            return
        }
        val part = installed.remove(type) ?: return
        val refund = part.price * 3 / 4
        money += refund
        note("${part.name} снята · возврат $refund ₽")
    }

    fun hint(text: String) = note(text)

    fun check(): BuildCheck {
        val errors = mutableListOf<String>()
        val missing = PartType.entries.filter { installed[it] == null }
        if (missing.isNotEmpty()) {
            errors += "Не установлено: " + missing.joinToString(", ") { it.label }
        }

        val cpu = installed[PartType.CPU]
        val board = installed[PartType.MOTHERBOARD]
        val ram = installed[PartType.RAM]
        val psu = installed[PartType.PSU]
        if (cpu != null && board != null && cpu.socket != board.socket) {
            errors += "Сокет CPU не подходит к материнской плате"
        }
        if (ram != null && board != null && ram.ramType != board.ramType) {
            errors += "Тип ОЗУ не подходит к материнской плате"
        }

        val cost = installed.values.sumOf { it.price }
        val power = installed.values.sumOf { it.power }
        val score = installed.values.sumOf { it.score }
        if (psu != null && psu.watt < power) {
            errors += "БП выдаёт ${psu.watt} Вт, сборке нужно $power Вт"
        }
        return BuildCheck(missing.isEmpty(), cost, power, score, errors)
    }

    fun assemble() {
        if (readyPc != null) {
            note("ПК уже собран — продай его или разбери", true)
            return
        }
        val result = check()
        val issue = result.errors.firstOrNull()
        if (issue != null) {
            note(issue, true)
            return
        }
        if (money < 0) {
            note("Баланс ушёл в минус", true)
            return
        }
        readyPc = ReadyPc(result.score, result.totalCost)
        note("POST успешен! ${result.score} очков производительности")
    }

    fun sell() {
        val pc = readyPc ?: return
        val orderNow = order
        val meetsOrder = pc.score >= orderNow.minScore
        val basePrice = (pc.cost * 1.30).toInt() + pc.score / 2
        val revenue = if (meetsOrder) basePrice + orderNow.bonus else (basePrice * 0.78).toInt()
        val net = revenue - DAILY_RENT
        money += net
        totalProfit += net - pc.cost
        reputation += if (meetsOrder) 2 else 1
        day += 1
        readyPc = null
        installed.clear()
        order = Orders.forDay(day, reputation)
        if (meetsOrder) {
            note("Заказ выполнен! Продажа $revenue ₽ · аренда −$DAILY_RENT ₽")
        } else {
            note("Продано без бонуса за $revenue ₽ · аренда −$DAILY_RENT ₽", true)
        }
    }

    fun scrap() {
        val pc = readyPc ?: return
        val refund = pc.cost / 2
        money += refund
        readyPc = null
        installed.clear()
        note("ПК разобран · возврат $refund ₽", true)
    }

    fun reset() {
        money = START_MONEY
        reputation = 0
        day = 1
        totalProfit = 0
        readyPc = null
        installed.clear()
        order = Orders.forDay(1, 0)
        note("Новая мастерская готова. Собери ПК!")
    }

    private fun note(text: String, isError: Boolean = false) {
        message = text
        messageError = isError
    }
}
