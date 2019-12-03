import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.test.assertEquals

class Day03Tests {
    @Test
    internal fun closestCrossingManhatten() {
        val first = "R75,D30,R83,U83,L12,D49,R71,U7,L72"
        val second = "U62,R66,U55,R34,D71,R55,D58,R83"
        assertEquals(159, findClosestCrossingManhatten(first, second)!!)
    }

    @Test
    internal fun closestCrossingManhatten2() {
        val first = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
        val second = "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
        assertEquals(135, findClosestCrossingManhatten(first, second)!!)
    }

    @Test
    internal fun closestCrossingSteps() {
        val first = "R75,D30,R83,U83,L12,D49,R71,U7,L72"
        val second = "U62,R66,U55,R34,D71,R55,D58,R83"
        assertEquals(610, findClosestCrossingSteps(first, second)!!)
    }

    @Test
    internal fun closestCrossingSteps2() {
        val first = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
        val second = "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
        assertEquals(410, findClosestCrossingSteps(first, second)!!)
    }

    @Test
    internal fun part1() {
        val lines = Files.readAllLines(Paths.get("test/Day03"))
        println(findClosestCrossingManhatten(lines[0], lines[1]))
    }

    @Test
    internal fun part2() {
        val lines = Files.readAllLines(Paths.get("test/Day03"))
        println(findClosestCrossingSteps(lines[0], lines[1]))
    }

    private fun findClosestCrossingManhatten(first: String, second: String): Int? {
        val firstPoints = toPoints(toCommands(first))
        val secondPoints = toPoints(toCommands(second))
        val secondSet = secondPoints.toSet()
        return firstPoints
                .filter { p -> secondSet.contains(p) }
                .map { point -> point.manhatten() }
                .min()
    }

    private fun findClosestCrossingSteps(first: String, second: String): Int? {
        val firstPoints = toPoints(toCommands(first))
        val secondPoints = toPoints(toCommands(second))
        val firstSet = firstPoints.toSet()
        val secondSet = secondPoints.toSet()
        return firstSet.filter { p -> secondSet.contains(p) }
                .map { p -> Pair(p, secondSet.find { it == p }!!) }
                .map { (p1, p2) -> p1.stepsRequired + p2.stepsRequired }
                .min()
    }

    private fun toPoints(commands: List<Command>): MutableList<Point> {
        val list = mutableListOf<Point>()
        var current = Point(0, 0, 0)
        for (command in commands) {
            for (i in 1..command.steps) {
                current = current.of(command.direction)
                list.add(current)
            }
        }
        return list
    }

    private fun toCommands(s: String): List<Command> {
        return s.split(",").map { toCommand(it) }.toList()
    }

    private fun toCommand(s: String): Command = Command(Direction.valueOf(s.substring(0, 1)), Integer.parseInt(s.substring(1, s.length)))

    class Command(val direction: Direction, val steps: Int)

    data class Point(val x: Int, val y: Int, val stepsRequired: Int) {
        fun of(direction: Direction): Point = when (direction) {
            Direction.R -> Point(x + 1, y, stepsRequired + 1)
            Direction.L -> Point(x - 1, y, stepsRequired + 1)
            Direction.U -> Point(x, y + 1, stepsRequired + 1)
            Direction.D -> Point(x, y - 1, stepsRequired + 1)
        }

        fun manhatten(): Int {
            return abs(x) + abs(y)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Point

            if (x != other.x) return false
            if (y != other.y) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            return result
        }

    }

    enum class Direction {
        R, D, U, L
    }

}

