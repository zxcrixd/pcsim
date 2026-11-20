package com.zxcrixd.pcsim.model

object Catalog {
    val parts: List<Part> = listOf(
        Part("cpu1", PartType.CPU, "i3-10100", 150, 40, 65, socket = "LGA1200"),
        Part("cpu2", PartType.CPU, "Ryzen 5 5600", 260, 70, 65, socket = "AM4"),
        Part("cpu3", PartType.CPU, "i5-12400F", 320, 90, 65, socket = "LGA1700"),
        Part("cpu4", PartType.CPU, "Ryzen 7 5800X", 420, 120, 105, socket = "AM4"),
        Part("cpu5", PartType.CPU, "i9-13900K", 750, 200, 125, socket = "LGA1700", requiredReputation = 6),

        Part("mb1", PartType.MOTHERBOARD, "H410M LGA1200 DDR4", 90, 10, 30, socket = "LGA1200", ramType = "DDR4"),
        Part("mb2", PartType.MOTHERBOARD, "B450M AM4 DDR4", 110, 15, 35, socket = "AM4", ramType = "DDR4"),
        Part("mb3", PartType.MOTHERBOARD, "B660M LGA1700 DDR4", 150, 20, 40, socket = "LGA1700", ramType = "DDR4"),
        Part("mb4", PartType.MOTHERBOARD, "X570 AM4 DDR4", 220, 30, 45, socket = "AM4", ramType = "DDR4"),
        Part("mb5", PartType.MOTHERBOARD, "Z790 LGA1700 DDR5", 320, 40, 50, socket = "LGA1700", ramType = "DDR5", requiredReputation = 5),

        Part("ram1", PartType.RAM, "8 ГБ DDR4-2666", 40, 10, 5, ramType = "DDR4"),
        Part("ram2", PartType.RAM, "16 ГБ DDR4-3200", 70, 25, 8, ramType = "DDR4"),
        Part("ram3", PartType.RAM, "32 ГБ DDR4-3600", 130, 45, 10, ramType = "DDR4"),
        Part("ram4", PartType.RAM, "16 ГБ DDR5-5200", 110, 40, 10, ramType = "DDR5", requiredReputation = 4),
        Part("ram5", PartType.RAM, "32 ГБ DDR5-6000", 210, 65, 12, ramType = "DDR5", requiredReputation = 6),

        Part("gpu1", PartType.GPU, "Интегрированная", 0, 5),
        Part("gpu2", PartType.GPU, "GTX 1650", 180, 50, 75),
        Part("gpu3", PartType.GPU, "RTX 3060", 340, 90, 170),
        Part("gpu4", PartType.GPU, "RTX 4070", 620, 150, 200, requiredReputation = 5),
        Part("gpu5", PartType.GPU, "RTX 4090", 1300, 260, 450, requiredReputation = 10),

        Part("ssd1", PartType.STORAGE, "HDD 1 ТБ", 35, 5, 10),
        Part("ssd2", PartType.STORAGE, "SSD SATA 480 ГБ", 50, 12, 5),
        Part("ssd3", PartType.STORAGE, "NVMe 1 ТБ", 90, 25, 8),
        Part("ssd4", PartType.STORAGE, "NVMe 2 ТБ Gen4", 160, 40, 10),

        Part("psu1", PartType.PSU, "400 Вт Bronze", 35, 5, watt = 400),
        Part("psu2", PartType.PSU, "550 Вт Bronze", 55, 8, watt = 550),
        Part("psu3", PartType.PSU, "750 Вт Gold", 95, 15, watt = 750),
        Part("psu4", PartType.PSU, "1000 Вт Platinum", 170, 25, watt = 1000, requiredReputation = 5),

        Part("case1", PartType.CASE, "Simple ATX", 30, 5),
        Part("case2", PartType.CASE, "Mid-Tower RGB", 60, 12),
        Part("case3", PartType.CASE, "Premium Airflow", 120, 20)
    )

    fun byType(type: PartType): List<Part> = parts.filter { it.type == type }
}
