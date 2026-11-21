package com.zxcrixd.pcsim.model

data class Order(
    val title: String,
    val minScore: Int,
    val bonus: Int,
    val client: String = "Клиент",
    val budget: Int = 0,
    val description: String = "Соберите надёжный ПК"
)

object Orders {
    private val base = listOf(
        Order("Офисный ПК", 40, 120, "Анна, бухгалтер", 700, "Тихая машина для документов и таблиц"),
        Order("ПК для школьника", 60, 140, "Илья, родитель", 850, "Учёба, браузер и лёгкие игры"),
        Order("Домашний мультимедиа-ПК", 90, 180, "Марина, дизайнер", 1200, "Фото, видео и домашний архив"),
        Order("Сервер студии", 150, 260, "Команда PixelFox", 1700, "Стабильность важнее внешнего вида"),
        Order("Игровой ПК стримера", 220, 340, "Даня Live", 2600, "Высокий FPS и запас по мощности"),
        Order("Рабочая станция", 300, 460, "Архитектурное бюро", 3600, "Рендеринг и тяжёлые проекты"),
        Order("Топовый билд обзорщика", 420, 700, "TechReview", 6000, "Максимальная производительность")
    )

    fun forDay(day: Int, reputation: Int): Order {
        val pool = base.filter { it.minScore <= 90 + reputation * 45 }.ifEmpty { listOf(base.first()) }
        val picked = pool[(day - 1) % pool.size]
        return picked.copy(bonus = picked.bonus + reputation * 10)
    }

    fun tasksForDay(day: Int, reputation: Int): List<Order> {
        val main = forDay(day, reputation)
        val available = base.filter { it.minScore <= 160 + reputation * 55 && it.title != main.title }
        val side = available.take(2).mapIndexed { index, task ->
            task.copy(
                bonus = task.bonus / 2 + reputation * 5 + index * 25,
                description = "Дополнительный заказ: ${task.description.lowercase()}"
            )
        }
        return listOf(main) + side
    }
}
