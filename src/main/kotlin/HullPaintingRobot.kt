import kotlinx.coroutines.runBlocking

class HullPaintingRobot(
        memory: IntcodeMemory,
        private val hull: Hull
) : IntcodeInput, IntcodeOutput {
    private val brain: Intcode = Intcode("HullPaintingRobot", memory, this, this)

    private var position = Coordinate(0, 0)
    private var direction = Direction.UP
    private var instruction = Instruction.PAINTING

    fun run() = runBlocking { brain.execute() }

    override suspend fun get(): Long {
        return hull.colorOf(position).value
    }

    override suspend fun receive(output: Long) {
        if (instruction == Instruction.PAINTING) {
            hull.setColorOf(position, Color.parse(output))
        } else {
            direction = direction.turn(output)
            position = position.move(direction)
        }
        instruction = instruction.switch()
    }

}

class Hull(private val startColor: Color, val map: MutableMap<Coordinate, Color> = mutableMapOf()) {
    fun colorOf(coordinate: Coordinate): Color {
        return map[coordinate] ?: if(coordinate == Coordinate(0, 0)) startColor else Color.BLACK
    }
    fun setColorOf(coordinate: Coordinate, color: Color) {
        map[coordinate] = color
    }
    fun size() = map.size
}

data class Coordinate(val x: Int, val y: Int) {
    fun move(direction: Direction) = Coordinate(x + direction.x, y + direction.y)
}

enum class Color(val value: Long) {
    BLACK(0), WHITE(1);

    companion object {
        fun parse(i: Long) = values().first { it.value == i }
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    fun turn(turnDirection: Long): Direction {
        return when (this) {
            UP -> if (turnDirection == 0L) LEFT else RIGHT
            DOWN -> if (turnDirection == 0L) RIGHT else LEFT
            LEFT -> if (turnDirection == 0L) DOWN else UP
            RIGHT -> if (turnDirection == 0L) UP else DOWN
        }
    }

}

enum class Instruction {
    PAINTING,
    TURNING;

    fun switch(): Instruction = if (this == PAINTING) TURNING else PAINTING
}