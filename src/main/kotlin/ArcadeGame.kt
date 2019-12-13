import ArcadeGameInstruction.*
import kotlinx.coroutines.runBlocking

class ArcadeGame(
        memory: IntcodeMemory,
        private val board: MutableMap<ArcadeGameCoordinate, Tile>
) : IntcodeInput, IntcodeOutput {
    private val brain: Intcode = Intcode("ArcadeGame", memory, this, this)
    private var instruction = LEFT
    private var left = 0L
    private var top = 0L

    fun run() {
        runBlocking { brain.execute() }
    }

    override suspend fun get(): Long {
        return -1
    }

    override suspend fun receive(output: Long) {
        when (instruction) {
            LEFT -> left = output
            TOP -> top = output
            TYPE -> board[ArcadeGameCoordinate(left, top)] = Tile.parse(output)
        }
        instruction = instruction.next()
    }

}

data class ArcadeGameCoordinate(val left: Long, val top: Long)

enum class ArcadeGameInstruction {
    LEFT, TOP, TYPE;

    fun next(): ArcadeGameInstruction {
        return when (this) {
            LEFT -> TOP
            TOP -> TYPE
            TYPE -> LEFT
        }
    }
}

enum class Tile(val char: Char) {
    EMPTY('.'),
    WALL('W'),
    BLOCK('B'),
    PADDLE('-'),
    BALL('*');

    companion object {
        fun parse(input: Long): Tile {
            return when (input) {
                1L -> WALL
                2L -> BLOCK
                3L -> PADDLE
                4L -> BALL
                else -> EMPTY
            }
        }
    }
}