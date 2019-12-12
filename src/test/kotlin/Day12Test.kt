import Day12Test.Position.Companion.parse
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import kotlin.math.abs

class Day12Test {

    @Test
    internal fun smallNumberOfMoons() {
        val a = Moon(parse("<x=-1, y=0, z=2"), Velocity())
        val b = Moon(parse("<x=2, y=-10, z=-7>"), Velocity())
        val c = Moon(parse("<x=4, y=-8, z=8>"), Velocity())
        val d = Moon(parse("<x=3, y=5, z=-1>"), Velocity())

        val aAfterGravity = a.applyGravity(b).applyGravity(c).applyGravity(d)
        assertEquals(Velocity(3, -1, -1), aAfterGravity.velocity)
        assertEquals(Moon(Position(2, -1, 1), Velocity(3, -1, -1)), aAfterGravity.applyVelocity())


        val moons = applyGravity(listOf(a, b, c, d))
        assertEquals(applyVelocity(moons), listOf(
                Moon(Position(x = 2, y = -1, z = 1), Velocity(x = 3, y = -1, z = -1)),
                Moon(Position(x = 3, y = -7, z = -4), Velocity(x = 1, y = 3, z = 3)),
                Moon(Position(x = 1, y = -7, z = 5), Velocity(x = -3, y = 1, z = -3)),
                Moon(Position(x = 2, y = 2, z = 0), Velocity(x = -1, y = -3, z = 1))
        ))

        var moonsForSteps = listOf(a, b, c, d)
        repeat(10) { moonsForSteps = step(moonsForSteps) }
        assertEquals(moonsForSteps, listOf(
                Moon(Position(x = 2, y = 1, z = -3), Velocity(x = -3, y = -2, z = 1)),
                Moon(Position(x = 1, y = -8, z = 0), Velocity(x = -1, y = 1, z = 3)),
                Moon(Position(x = 3, y = -6, z = 1), Velocity(x = 3, y = 2, z = -3)),
                Moon(Position(x = 2, y = 0, z = 4), Velocity(x = 1, y = -1, z = -1))
        ))
        assertEquals(moonsForSteps.map { m -> m.energy }, listOf(36, 45, 80, 18))
        assertEquals(moonsForSteps.map { m -> m.energy }.sum(), 179)
    }

    @Test
    internal fun secondSmallNumberOfMoons() {
        val a = Moon(parse("<x=-8, y=-10, z=0>"), Velocity())
        val b = Moon(parse("<x=5, y=5, z=10>"), Velocity())
        val c = Moon(parse("<x=2, y=-7, z=3>"), Velocity())
        val d = Moon(parse("<x=9, y=-8, z=-3>"), Velocity())

        var moons = listOf(a, b, c, d)
        repeat(100) { moons = step(moons) }
        assertEquals(moons.map { m -> m.energy }, listOf(290, 608, 574, 468))
        assertEquals(moons.map { m -> m.energy }.sum(), 1940)
    }

    @Test
    internal fun task1() {
        val a = Moon(parse("<x=10, y=15, z=7>"), Velocity())
        val b = Moon(parse("<x=15, y=10, z=0>"), Velocity())
        val c = Moon(parse("<x=20, y=12, z=3>"), Velocity())
        val d = Moon(parse("<x=0, y=-3, z=13>"), Velocity())

        var moons = listOf(a, b, c, d)
        repeat(1000) { moons = step(moons) }
        assertEquals(moons.map { m -> m.energy }.sum(), 8362)
    }

    @Test
    internal fun stepsRequiredForSamePosition() {
        val a = Moon(parse("<x=-1, y=0, z=2"), Velocity())
        val b = Moon(parse("<x=2, y=-10, z=-7>"), Velocity())
        val c = Moon(parse("<x=4, y=-8, z=8>"), Velocity())
        val d = Moon(parse("<x=3, y=5, z=-1>"), Velocity())

        val steps = calculateStepsRequiredSamePosition(listOf(a, b, c, d))
        assertEquals(2772, steps)
    }

    private fun calculateStepsRequiredSamePosition(moons: List<Moon>): Long {
        val initPositions = moons.map { it.position }
        val initX = initPositions.map { it.x }
        val initY = initPositions.map { it.y }
        val initZ = initPositions.map { it.z }
        var stepsXSame = 0L
        var stepsYSame = 0L
        var stepsZSame = 0L
        var steps = 1L
        var innerMoons = moons
        var run = true
        while (run) {
            steps++
            innerMoons = step(innerMoons)
            val innerPosition = innerMoons.map { it.position }
            if (stepsXSame == 0L) {
                if (innerPosition.map { it.x } == initX) stepsXSame = steps
            }
            if (stepsYSame == 0L) {
                if (innerPosition.map { it.y } == initY) stepsYSame = steps
            }
            if (stepsZSame == 0L) {
                if (innerPosition.map { it.z } == initZ) stepsZSame = steps
            }
            if (stepsXSame != 0L && stepsYSame != 0L && stepsZSame != 0L) run = false
        }
        // https://en.wikipedia.org/wiki/Greatest_common_divisor#Least_common_multiple
        val leastCommonMultipleXY = stepsXSame * stepsYSame / gcm(stepsXSame, stepsYSame)
        val leastCommonMultipleXYZ = leastCommonMultipleXY * stepsZSame / gcm(leastCommonMultipleXY, stepsZSame)
        return leastCommonMultipleXYZ
    }

    fun gcm(a: Long, b: Long): Long = if (b == 0L) a else gcm(b, a % b)

    @Test
    internal fun stepsRequiredForSamePositionLarge() {
        val a = Moon(parse("<x=-8, y=-10, z=0>"), Velocity())
        val b = Moon(parse("<x=5, y=5, z=10>"), Velocity())
        val c = Moon(parse("<x=2, y=-7, z=3>"), Velocity())
        val d = Moon(parse("<x=9, y=-8, z=-3>"), Velocity())

        assertEquals(4686774924, calculateStepsRequiredSamePosition(listOf(a, b, c, d)))
    }

    @Test
    internal fun task2() {
        val start = Instant.now()
        val a = Moon(parse("<x=10, y=15, z=7>"), Velocity())
        val b = Moon(parse("<x=15, y=10, z=0>"), Velocity())
        val c = Moon(parse("<x=20, y=12, z=3>"), Velocity())
        val d = Moon(parse("<x=0, y=-3, z=13>"), Velocity())

        println(calculateStepsRequiredSamePosition(listOf(a, b, c, d)))
        val end = Instant.now()
        println(Duration.between(start, end))
    }

    @Test
    internal fun task2Async() {
        val start = Instant.now()
        val a = Moon(parse("<x=10, y=15, z=7>"), Velocity())
        val b = Moon(parse("<x=15, y=10, z=0>"), Velocity())
        val c = Moon(parse("<x=20, y=12, z=3>"), Velocity())
        val d = Moon(parse("<x=0, y=-3, z=13>"), Velocity())

        val moons = listOf(a, b, c, d)
        runBlocking {
            val stepsXSame = async { calculateStepsRequiredSamePosition(moons) { position -> position.x } }
            val stepsYSame = async { calculateStepsRequiredSamePosition(moons) { position -> position.y } }
            val stepsZSame = async { calculateStepsRequiredSamePosition(moons) { position -> position.z } }
            val leastCommonMultipleXY = stepsXSame.await() * stepsYSame.await() / gcm(stepsXSame.await(), stepsYSame.await())
            val leastCommonMultipleXYZ = leastCommonMultipleXY * stepsZSame.await() / gcm(leastCommonMultipleXY, stepsZSame.await())
            println(leastCommonMultipleXYZ)
        }
        println()
        val end = Instant.now()
        println(Duration.between(start, end))
    }

    private fun calculateStepsRequiredSamePosition(moons: List<Moon>, axisValueExtractor: (Position) -> Int): Long {
        val initPositions = moons.map { it.position }
        val initialValue = initPositions.map(axisValueExtractor)
        var steps = 1L
        var innerMoons = moons
        while (true) {
            steps++
            innerMoons = step(innerMoons)
            if (innerMoons.map { it.position }.map(axisValueExtractor) == initialValue) return steps
        }
    }


    private fun step(moons: List<Moon>): List<Moon> = applyVelocity(applyGravity(moons))

    private fun applyGravity(moons: List<Moon>): List<Moon> {
        var internalMoons = moons
        for (moon in internalMoons) {
            internalMoons = internalMoons.map { m -> if (m != moon) m.applyGravity(moon) else moon }
        }
        return internalMoons
    }

    private fun applyVelocity(moons: List<Moon>): List<Moon> {
        return moons.map { moon -> moon.applyVelocity() }
    }

    data class Moon(val position: Position, val velocity: Velocity) {

        val energy = position.potentialEnergy() * velocity.kineticEnergy()

        fun applyGravity(moon: Moon): Moon {
            return this.copy(velocity = Velocity(
                    velocity.x + moon.position.x.compareTo(position.x),
                    velocity.y + moon.position.y.compareTo(position.y),
                    velocity.z + moon.position.z.compareTo(position.z)
            ))
        }

        fun applyVelocity(): Moon {
            return this.copy(position = Position(
                    position.x + velocity.x,
                    position.y + velocity.y,
                    position.z + velocity.z
            ))
        }

        override fun toString(): String {
            return "pos=$position, vel=$velocity)"
        }

    }

    data class Position(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String {
            return "<x=$x, y=$y, z=$z>"
        }

        companion object {
            fun parse(input: String): Position {
                val parts = input.removePrefix("<").removeSuffix(">").split(",")
                return Position(
                        Integer.parseInt(parts[0].trim().drop(2).trim()),
                        Integer.parseInt(parts[1].trim().drop(2).trim()),
                        Integer.parseInt(parts[2].trim().drop(2).trim())
                )
            }
        }

        fun potentialEnergy(): Int {
            return abs(x) + abs(y) + abs(z)
        }
    }

    data class Velocity(val x: Int = 0, val y: Int = 0, val z: Int = 0) {
        override fun toString(): String {
            return "<x=$x, y=$y, z=$z>"
        }

        fun kineticEnergy(): Int {
            return abs(x) + abs(y) + abs(z)
        }
    }

}