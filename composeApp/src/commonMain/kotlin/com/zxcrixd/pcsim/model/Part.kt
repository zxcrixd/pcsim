package com.zxcrixd.pcsim.model

enum class PartType(val label: String) {
    CPU("Процессор"),
    MOTHERBOARD("Материнская плата"),
    RAM("Оперативная память"),
    GPU("Видеокарта"),
    STORAGE("Накопитель"),
    PSU("Блок питания"),
    CASE("Корпус")
}

data class Part(
    val id: String,
    val type: PartType,
    val name: String,
    val price: Int,
    val score: Int,
    val power: Int = 0,
    val watt: Int = 0,
    val socket: String? = null,
    val ramType: String? = null,
    val requiredReputation: Int = 0
)
