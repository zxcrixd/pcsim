extends RefCounted

const SLOTS := ["cpu", "board", "ram", "gpu", "storage", "psu"]
const LABELS := {
    "cpu": "Процессор", "board": "Материнская плата", "ram": "Оперативная память",
    "gpu": "Видеокарта", "storage": "Накопитель", "psu": "Блок питания"
}
const PARTS := {
    "cpu": [
        {"name":"Core i3 10100", "price":150, "score":40, "power":65, "socket":"LGA1200", "rep":0},
        {"name":"Ryzen 5 5600", "price":260, "score":70, "power":65, "socket":"AM4", "rep":0},
        {"name":"Core i5 12400F", "price":320, "score":90, "power":65, "socket":"LGA1700", "rep":1},
        {"name":"Ryzen 7 5800X", "price":420, "score":120, "power":105, "socket":"AM4", "rep":3},
        {"name":"Core i9 13900K", "price":750, "score":200, "power":125, "socket":"LGA1700", "rep":6}
    ],
    "board": [
        {"name":"H410M", "price":90, "score":10, "power":30, "socket":"LGA1200", "ram":"DDR4", "rep":0},
        {"name":"B450M", "price":110, "score":15, "power":35, "socket":"AM4", "ram":"DDR4", "rep":0},
        {"name":"B660M", "price":150, "score":20, "power":40, "socket":"LGA1700", "ram":"DDR4", "rep":1},
        {"name":"X570", "price":220, "score":30, "power":45, "socket":"AM4", "ram":"DDR4", "rep":2},
        {"name":"Z790 DDR5", "price":320, "score":40, "power":50, "socket":"LGA1700", "ram":"DDR5", "rep":5}
    ],
    "ram": [
        {"name":"8 ГБ DDR4", "price":40, "score":10, "power":5, "ram":"DDR4", "rep":0},
        {"name":"16 ГБ DDR4", "price":70, "score":25, "power":8, "ram":"DDR4", "rep":0},
        {"name":"32 ГБ DDR4", "price":130, "score":45, "power":10, "ram":"DDR4", "rep":2},
        {"name":"16 ГБ DDR5", "price":110, "score":40, "power":10, "ram":"DDR5", "rep":4},
        {"name":"32 ГБ DDR5", "price":210, "score":65, "power":12, "ram":"DDR5", "rep":6}
    ],
    "gpu": [
        {"name":"Встроенная графика", "price":0, "score":5, "power":0, "rep":0},
        {"name":"GTX 1650", "price":180, "score":50, "power":75, "rep":0},
        {"name":"RTX 3060", "price":340, "score":90, "power":170, "rep":1},
        {"name":"RTX 4070", "price":620, "score":150, "power":200, "rep":4},
        {"name":"RTX 4090", "price":1300, "score":260, "power":450, "rep":8}
    ],
    "storage": [
        {"name":"HDD 1 ТБ", "price":35, "score":5, "power":10, "rep":0},
        {"name":"SATA SSD 480 ГБ", "price":50, "score":12, "power":5, "rep":0},
        {"name":"NVMe 1 ТБ", "price":90, "score":25, "power":8, "rep":1},
        {"name":"NVMe 2 ТБ", "price":160, "score":40, "power":10, "rep":3}
    ],
    "psu": [
        {"name":"400 Вт Bronze", "price":35, "score":5, "watt":400, "rep":0},
        {"name":"550 Вт Bronze", "price":55, "score":8, "watt":550, "rep":0},
        {"name":"750 Вт Gold", "price":95, "score":15, "watt":750, "rep":1},
        {"name":"1000 Вт Platinum", "price":170, "score":25, "watt":1000, "rep":4}
    ]
}
