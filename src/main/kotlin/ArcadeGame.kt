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
    private var score = 0L

    private var paddle = ArcadeGameCoordinate(0L, 0L)
    private var ball = ArcadeGameCoordinate(0L, 0L)

    fun run(): Long {
        runBlocking { brain.execute() }
        print()
        return score;
    }

    override suspend fun get(): Long {
        print()
        if(paddle.left > ball.left) return -1
        if(paddle.left < ball.left) return 1
        return 0
    }

    override suspend fun receive(output: Long) {
        when (instruction) {
            LEFT -> left = output
            TOP -> top = output
            TYPE -> {
                if (left == -1L && top == 0L) {
                    score = output
                } else {
                    val tile = Tile.parse(output)
                    val arcadeGameCoordinate = ArcadeGameCoordinate(left, top)
                    if (tile == Tile.PADDLE) paddle = arcadeGameCoordinate
                    else if (tile == Tile.BALL) ball = arcadeGameCoordinate
                    board[arcadeGameCoordinate] = tile
                }
            }
        }
        instruction = instruction.next()
    }

    fun print() {
        println(score)
        val coordinates = board.keys
        val maxLeft = coordinates.map { coordinate -> coordinate.left }.max()!!
        val maxTop = coordinates.map { coordinate -> coordinate.top }.max()!!
        val lines = mutableListOf<String>()
        for (top in 0 until maxTop) {
            val rowArray = Array(maxLeft.toInt() + 1) { left ->
                (board[ArcadeGameCoordinate(left.toLong(), top)] ?: Tile.EMPTY).char
            }
            lines.add(rowArray.joinToString(separator = ""))
        }
        for (line in lines) {
            println(line)
        }
        println()
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