package com.zxcrixd.pcsim.model

data class Order(val title: String, val minScore: Int, val bonus: Int)

object Orders {
    private val base = listOf(
        Order("Офисный ПК для бухгалтерии", 40, 120),
        Order("ПК для школьника", 60, 140),
        Order("Домашний мультимедиа-ПК", 90, 180),
        Order("Сервер для небольшой студии", 150, 260),
        Order("Игровой ПК для стримера", 220, 340),
        Order("Рабочая станция для монтажа", 300, 460),
        Order("Топовый билд для обзорщика", 420, 700)
    )

    fun forDay(day: Int, reputation: Int): Order {
        val pool = base.filter { it.minScore <= 90 + reputation * 45 }.ifEmpty { listOf(base.first()) }
        val picked = pool[(day - 1) % pool.size]
        return picked.copy(bonus = picked.bonus + reputation * 10)
    }
}
