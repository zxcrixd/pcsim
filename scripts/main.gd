extends Node3D

const CAT = preload("res://scripts/catalog.gd")
const RENT := 40

var money := 1500
var day := 1
var reputation := 0
var profit := 0
var picks := {}
var part_nodes := {}
var model_root: Node3D
var camera: Camera3D
var pivot: Node3D
var money_label: Label
var day_label: Label
var order_label: Label
var status_label: Label
var score_label: Label
var buttons := {}
var dragging := false
var zoom := 3.4
var order_index := 0
var built := false
var built_cost := 0
var built_score := 0
var fans: Array[Node3D] = []
var orders := [
    {"title":"Офисный ПК", "score":55, "bonus":120},
    {"title":"Домашний игровой ПК", "score":130, "bonus":220},
    {"title":"ПК для стримера", "score":220, "bonus":360},
    {"title":"Рабочая станция", "score":300, "bonus":500}
]

func _ready() -> void:
    for slot in CAT.SLOTS:
        picks[slot] = -1
    _make_world()
    _make_ui()
    _refresh()

func _process(delta: float) -> void:
    if not dragging:
        pivot.rotation.y += delta * 0.16
    camera.position.z = lerpf(camera.position.z, zoom, minf(delta * 5.0, 1.0))
    for fan in fans:
        if is_instance_valid(fan):
            fan.rotate_z(delta * 8.0)

func _unhandled_input(event: InputEvent) -> void:
    if event is InputEventMouseButton:
        if event.button_index == MOUSE_BUTTON_LEFT:
            dragging = event.pressed
        elif event.pressed and event.button_index == MOUSE_BUTTON_WHEEL_UP:
            zoom = maxf(2.0, zoom - 0.25)
        elif event.pressed and event.button_index == MOUSE_BUTTON_WHEEL_DOWN:
            zoom = minf(5.5, zoom + 0.25)
    elif event is InputEventMouseMotion and dragging:
        pivot.rotation.y -= event.relative.x * 0.008
        pivot.rotation.x = clampf(pivot.rotation.x - event.relative.y * 0.005, -0.35, 0.35)
    elif event is InputEventScreenDrag:
        pivot.rotation.y -= event.relative.x * 0.008
        pivot.rotation.x = clampf(pivot.rotation.x - event.relative.y * 0.005, -0.35, 0.35)

func _mat(color: Color, glow := false) -> StandardMaterial3D:
    var material := StandardMaterial3D.new()
    material.albedo_color = color
    material.metallic = 0.55
    material.roughness = 0.32
    if glow:
        material.emission_enabled = true
        material.emission = color
        material.emission_energy_multiplier = 1.4
    return material

func _box(parent: Node3D, size: Vector3, pos: Vector3, color: Color, glow := false) -> MeshInstance3D:
    var mesh := BoxMesh.new()
    mesh.size = size
    var item := MeshInstance3D.new()
    item.mesh = mesh
    item.position = pos
    item.material_override = _mat(color, glow)
    parent.add_child(item)
    return item

func _make_world() -> void:
    var world := WorldEnvironment.new()
    var environment := Environment.new()
    environment.background_mode = Environment.BG_COLOR
    environment.background_color = Color(0.025, 0.035, 0.07)
    environment.ambient_light_source = Environment.AMBIENT_SOURCE_COLOR
    environment.ambient_light_color = Color(0.45, 0.5, 0.7)
    environment.ambient_light_energy = 1.1
    world.environment = environment
    add_child(world)

    var sun := DirectionalLight3D.new()
    sun.rotation_degrees = Vector3(-48, -30, 0)
    sun.light_energy = 1.25
    add_child(sun)
    var lamp := OmniLight3D.new()
    lamp.position = Vector3(-1.2, 2.4, 1.8)
    lamp.light_color = Color(0.35, 0.55, 1.0)
    lamp.light_energy = 3.0
    lamp.omni_range = 7.0
    add_child(lamp)

    _box(self, Vector3(12, 0.12, 12), Vector3(0, -0.12, 0), Color(0.07, 0.09, 0.14))
    _box(self, Vector3(4.8, 0.12, 2.0), Vector3(0, 0.72, 0), Color(0.22, 0.16, 0.13))
    _box(self, Vector3(0.12, 0.75, 0.12), Vector3(-2.0, 0.32, -0.75), Color(0.12, 0.1, 0.1))
    _box(self, Vector3(0.12, 0.75, 0.12), Vector3(2.0, 0.32, -0.75), Color(0.12, 0.1, 0.1))

    model_root = Node3D.new()
    model_root.position = Vector3(0.45, 0.8, 0)
    add_child(model_root)
    _box(model_root, Vector3(1.08, 1.75, 0.08), Vector3(0, 0.88, -0.5), Color(0.13, 0.16, 0.23))
    _box(model_root, Vector3(1.08, 0.08, 1.0), Vector3(0, 0.04, 0), Color(0.1, 0.12, 0.18))
    _box(model_root, Vector3(0.08, 1.75, 1.0), Vector3(-0.5, 0.88, 0), Color(0.12, 0.14, 0.2))
    _box(model_root, Vector3(0.08, 1.75, 1.0), Vector3(0.5, 0.88, 0), Color(0.12, 0.14, 0.2))
    _box(model_root, Vector3(0.78, 0.95, 0.035), Vector3(0, 1.0, -0.43), Color(0.06, 0.28, 0.19))
    _box(self, Vector3(1.15, 0.68, 0.07), Vector3(-1.3, 1.15, -0.48), Color(0.025, 0.04, 0.08), true)
    _box(self, Vector3(0.1, 0.38, 0.1), Vector3(-1.3, 0.82, -0.45), Color(0.16, 0.18, 0.24))
    var screen := Label3D.new()
    screen.text = "PC CREATOR\nSIMULATOR 3D"
    screen.font_size = 48
    screen.pixel_size = 0.003
    screen.modulate = Color(0.35, 0.85, 1.0)
    screen.position = Vector3(-1.3, 1.17, -0.42)
    add_child(screen)

    pivot = Node3D.new()
    pivot.position = Vector3(0, 1.45, 0)
    add_child(pivot)
    camera = Camera3D.new()
    camera.position = Vector3(0, 0.45, zoom)
    camera.fov = 52
    camera.current = true
    pivot.add_child(camera)
    camera.look_at(Vector3(0, 1.0, 0))

func _part_color(slot: String, index: int) -> Color:
    var colors := {
        "cpu":Color(0.2,0.65,1.0), "board":Color(0.12,0.52,0.3),
        "ram":Color(0.85,0.22,0.38), "gpu":Color(0.55,0.25,0.95),
        "storage":Color(0.2,0.8,0.78), "psu":Color(0.72,0.74,0.8)
    }
    return colors[slot].lerp(Color(1.0, 0.68, 0.18), minf(float(index) * 0.13, 0.48))

func _draw_part(slot: String, index: int) -> void:
    if part_nodes.has(slot):
        part_nodes[slot].queue_free()
    var root := Node3D.new()
    model_root.add_child(root)
    part_nodes[slot] = root
    var color := _part_color(slot, index)
    match slot:
        "board":
            _box(root, Vector3(0.72,0.82,0.035), Vector3(0,0.98,-0.34), color, true)
            for i in range(5):
                _box(root, Vector3(0.06,0.06,0.025), Vector3(-0.28+i*0.14,0.72,-0.30), Color(0.85,0.68,0.25))
        "cpu":
            _box(root, Vector3(0.22,0.22,0.08), Vector3(-0.12,1.18,-0.26), color, true)
            _box(root, Vector3(0.3,0.06,0.3), Vector3(-0.12,1.32,-0.12), Color(0.3,0.34,0.42))
        "ram":
            for x in [0.18,0.31]:
                _box(root, Vector3(0.07,0.42,0.08), Vector3(x,1.05,-0.26), color, true)
        "gpu":
            _box(root, Vector3(0.82,0.16,0.28), Vector3(0.02,0.62,0.02), color, true)
            for x in [-0.2,0.15]:
                var fan := Node3D.new()
                fan.position = Vector3(x,0.62,0.18)
                root.add_child(fan)
                var cylinder := CylinderMesh.new()
                cylinder.top_radius = 0.105
                cylinder.bottom_radius = 0.105
                cylinder.height = 0.035
                var fan_mesh := MeshInstance3D.new()
                fan_mesh.mesh = cylinder
                fan_mesh.rotation_degrees.x = 90
                fan_mesh.material_override = _mat(Color(0.08,0.1,0.16))
                fan.add_child(fan_mesh)
                fans.append(fan)
        "storage":
            _box(root, Vector3(0.34,0.1,0.22), Vector3(-0.25,0.31,-0.12), color, true)
        "psu":
            _box(root, Vector3(0.78,0.27,0.62), Vector3(0,0.2,0.12), color)

func _make_ui() -> void:
    var layer := CanvasLayer.new()
    add_child(layer)
    var root := Control.new()
    root.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
    layer.add_child(root)
    var hud := PanelContainer.new()
    hud.position = Vector2(16,14)
    hud.custom_minimum_size = Vector2(330,0)
    root.add_child(hud)
    var hud_box := VBoxContainer.new()
    hud.add_child(hud_box)
    var title := Label.new()
    title.text = "PC CREATOR SIMULATOR 3D"
    title.add_theme_font_size_override("font_size",20)
    hud_box.add_child(title)
    money_label = Label.new()
    hud_box.add_child(money_label)
    day_label = Label.new()
    hud_box.add_child(day_label)
    order_label = Label.new()
    order_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
    hud_box.add_child(order_label)
    var panel := PanelContainer.new()
    panel.set_anchors_preset(Control.PRESET_TOP_RIGHT)
    panel.position = Vector2(-370,14)
    panel.custom_minimum_size = Vector2(354,0)
    root.add_child(panel)
    var list := VBoxContainer.new()
    panel.add_child(list)
    var hint := Label.new()
    hint.text = "КОМПЛЕКТУЮЩИЕ · нажми для выбора"
    list.add_child(hint)
    for slot in CAT.SLOTS:
        var button := Button.new()
        button.custom_minimum_size = Vector2(0,42)
        button.size_flags_horizontal = Control.SIZE_EXPAND_FILL
        button.pressed.connect(_cycle_part.bind(slot))
        list.add_child(button)
        buttons[slot] = button
    status_label = Label.new()
    status_label.set_anchors_preset(Control.PRESET_CENTER_BOTTOM)
    status_label.position = Vector2(-310,-112)
    status_label.custom_minimum_size = Vector2(620,36)
    status_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
    root.add_child(status_label)
    score_label = Label.new()
    score_label.set_anchors_preset(Control.PRESET_CENTER_BOTTOM)
    score_label.position = Vector2(-260,-80)
    score_label.custom_minimum_size = Vector2(520,30)
    score_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
    root.add_child(score_label)
    var assemble := Button.new()
    assemble.text = "СОБРАТЬ ПК"
    assemble.set_anchors_preset(Control.PRESET_CENTER_BOTTOM)
    assemble.position = Vector2(-155,-44)
    assemble.custom_minimum_size = Vector2(150,48)
    assemble.pressed.connect(_assemble)
    root.add_child(assemble)
    var sell := Button.new()
    sell.text = "ПРОДАТЬ ПК"
    sell.set_anchors_preset(Control.PRESET_CENTER_BOTTOM)
    sell.position = Vector2(5,-44)
    sell.custom_minimum_size = Vector2(150,48)
    sell.pressed.connect(_sell)
    root.add_child(sell)
    var reset := Button.new()
    reset.text = "ЗАНОВО"
    reset.set_anchors_preset(Control.PRESET_BOTTOM_LEFT)
    reset.position = Vector2(16,-58)
    reset.custom_minimum_size = Vector2(100,40)
    reset.pressed.connect(_reset_game)
    root.add_child(reset)
    var controls := Label.new()
    controls.text = "Зажми и тяни — вращение · колесо — масштаб"
    controls.set_anchors_preset(Control.PRESET_BOTTOM_RIGHT)
    controls.position = Vector2(-320,-38)
    controls.custom_minimum_size = Vector2(300,24)
    controls.horizontal_alignment = HORIZONTAL_ALIGNMENT_RIGHT
    root.add_child(controls)

func _cycle_part(slot: String) -> void:
    if built:
        _message("Сначала продай готовый ПК", true)
        return
    var items: Array = CAT.PARTS[slot]
    var start: int = int(picks[slot])
    for step in range(1,items.size()+1):
        var candidate: int = (start+step)%items.size()
        if int(items[candidate].get("rep",0)) <= reputation:
            picks[slot] = candidate
            _draw_part(slot,candidate)
            break
    _refresh()

func _selected(slot: String) -> Dictionary:
    var index: int = int(picks[slot])
    if index < 0:
        return {}
    return CAT.PARTS[slot][index]

func _check_build() -> Dictionary:
    var errors: Array[String] = []
    var cost := 0
    var score := 0
    var power := 0
    for slot in CAT.SLOTS:
        var part: Dictionary = _selected(slot)
        if part.is_empty():
            errors.append("Выбери: " + str(CAT.LABELS[slot]))
        else:
            cost += int(part.get("price",0))
            score += int(part.get("score",0))
            power += int(part.get("power",0))
    var cpu: Dictionary = _selected("cpu")
    var board: Dictionary = _selected("board")
    var ram: Dictionary = _selected("ram")
    var psu: Dictionary = _selected("psu")
    if not cpu.is_empty() and not board.is_empty() and cpu.get("socket","") != board.get("socket",""):
        errors.append("Сокет процессора не подходит к плате")
    if not ram.is_empty() and not board.is_empty() and ram.get("ram","") != board.get("ram",""):
        errors.append("Тип памяти не подходит к материнской плате")
    if not psu.is_empty() and int(psu.get("watt",0)) < power:
        errors.append("БП слабый: нужно %d Вт" % power)
    if cost > money:
        errors.append("Не хватает денег: нужно %d" % cost)
    return {"errors":errors,"cost":cost,"score":score,"power":power}

func _assemble() -> void:
    if built:
        _message("ПК уже собран — продай его", true)
        return
    var check: Dictionary = _check_build()
    var errors: Array = check["errors"]
    if not errors.is_empty():
        _message(str(errors[0]), true)
        return
    var order: Dictionary = orders[order_index%orders.size()]
    if int(check["score"]) < int(order["score"]):
        _message("Заказ требует %d очков. Улучши комплектующие!" % order["score"], true)
        return
    built_cost = int(check["cost"])
    built_score = int(check["score"])
    money -= built_cost
    built = true
    _message("ПК собран! Проверь RGB и нажми ПРОДАТЬ ПК", false)
    var tween := create_tween()
    tween.tween_property(model_root, "scale", Vector3(1.06,1.06,1.06), 0.18)
    tween.tween_property(model_root, "scale", Vector3.ONE, 0.22)
    _refresh()

func _sell() -> void:
    if not built:
        _message("Сначала собери совместимый ПК", true)
        return
    var order: Dictionary = orders[order_index%orders.size()]
    var revenue := int(float(built_cost)*1.35) + int(built_score / 2) + int(order["bonus"])
    money += revenue - RENT
    profit += revenue - built_cost - RENT
    reputation += 2
    day += 1
    order_index += 1
    built = false
    _message("Заказ выполнен! Продано за %d ₽ · аренда −%d ₽" % [revenue,RENT], false)
    for slot in CAT.SLOTS:
        picks[slot] = -1
        if part_nodes.has(slot):
            part_nodes[slot].queue_free()
            part_nodes.erase(slot)
    fans.clear()
    _refresh()

func _message(text: String, is_error: bool) -> void:
    status_label.text = text
    status_label.modulate = Color(1,0.4,0.4) if is_error else Color(0.4,1,0.65)

func _refresh() -> void:
    money_label.text = "Баланс: %d ₽ · прибыль: %d ₽" % [money,profit]
    day_label.text = "День %d · репутация %d" % [day,reputation]
    var order: Dictionary = orders[order_index%orders.size()]
    order_label.text = "Заказ: %s\nНужно %d очков · бонус %d ₽ · аренда %d ₽" % [order["title"],order["score"],order["bonus"],RENT]
    var check: Dictionary = _check_build()
    score_label.text = "Сборка: %d очков · детали %d ₽ · мощность %d Вт" % [check["score"],check["cost"],check["power"]]
    for slot in CAT.SLOTS:
        var button: Button = buttons[slot]
        var part: Dictionary = _selected(slot)
        if part.is_empty():
            button.text = "%s · нажми для выбора" % CAT.LABELS[slot]
        else:
            button.text = "%s: %s · %d ₽" % [CAT.LABELS[slot],part["name"],part["price"]]

func _reset_game() -> void:
    money = 1500
    day = 1
    reputation = 0
    profit = 0
    built = false
    order_index = 0
    for slot in CAT.SLOTS:
        picks[slot] = -1
        if part_nodes.has(slot):
            part_nodes[slot].queue_free()
            part_nodes.erase(slot)
    fans.clear()
    _message("Новая смена. Собери ПК!", false)
    _refresh()
