import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.abs

class Day10Test {

    @Test
    internal fun testBigInput() {
        val input = "......#.#.\n" +
                "#..#.#....\n" +
                "..#######.\n" +
                ".#.#.###..\n" +
                ".#..#.....\n" +
                "..#....#.#\n" +
                "#..#....#.\n" +
                ".##.#..###\n" +
                "##...#..#.\n" +
                ".#....####"
        val asteroidBelt = parseBelt(input)
        val bestAsteroid = calculateBestAsteroid(asteroidBelt)
        assertEquals(bestAsteroid, Pair(Coordinate(5, 8), 33))
    }

    @Test
    internal fun testGiantInput() {
        val input = ".#..##.###...#######\n" +
                "##.############..##.\n" +
                ".#.######.########.#\n" +
                ".###.#######.####.#.\n" +
                "#####.##.#.##.###.##\n" +
                "..#####..#.#########\n" +
                "####################\n" +
                "#.####....###.#.#.##\n" +
                "##.#################\n" +
                "#####.##.###..####..\n" +
                "..######..##.#######\n" +
                "####.##.####...##..#\n" +
                ".#####..#.######.###\n" +
                "##...#.##########...\n" +
                "#.##########.#######\n" +
                ".####.#.###.###.#.##\n" +
                "....##.##.###..#####\n" +
                ".#.#.###########.###\n" +
                "#.#.#.#####.####.###\n" +
                "###.##.####.##.#..##"
        val asteroidBelt = parseBelt(input)
        val bestAsteroid = calculateBestAsteroid(asteroidBelt)
        assertEquals(bestAsteroid, Pair(Coordinate(11, 13), 210))
    }

    @Test
    internal fun task1() {
        val asteroidBelt = parseBelt(taskInput)
        val bestAsteroid = calculateBestAsteroid(asteroidBelt)
        println(bestAsteroid)
    }

    @Test
    internal fun task2() {
        val base = Coordinate(28, 29)

        val asteroidBelt = parseBelt(taskInput)

        val myBelt = asteroidBelt.copy(asteroids = asteroidBelt.asteroids.filterNot { base == it })
        val asteroidsWithSmallestStep = myBelt.asteroids.map { asteroid -> Pair(asteroid, smallestStep(base, asteroid)) }
        val sortedSteps = asteroidsWithSmallestStep.map { it.second }.distinct().sortedWith(StepComparator())

        val asteroidsByStepMap: MutableMap<Step, MutableList<Coordinate>> = mutableMapOf()
        for (pair in asteroidsWithSmallestStep) {
            val list = asteroidsByStepMap.getOrDefault(pair.second, mutableListOf())
            list.add(pair.first)
            list.sortBy { coordinate -> abs(coordinate.x - base.x) + abs(coordinate.y - base.y) }
            asteroidsByStepMap[pair.second] = list
        }

        val killList = mutableListOf<Coordinate>()
        var laserIndex = 0
        while (asteroidsByStepMap.isNotEmpty()) {
            val stepToShootAt = sortedSteps[laserIndex % sortedSteps.size]
            val asteroidsToDie = asteroidsByStepMap[stepToShootAt]
            if (asteroidsToDie != null) {
                val alderaan = asteroidsToDie.removeAt(0)
                if (asteroidsToDie.isEmpty()) asteroidsByStepMap.remove(stepToShootAt)
                killList.add(alderaan)
            }
            laserIndex++
        }
        print(killList[198])
    }

    class StepComparator : Comparator<Step> {
        override fun compare(o1: Step?, o2: Step?): Int {
            val step = o1!!
            val otherStep = o2!!
            if (step.x == 0 && step.y == -1) return 1;
            if (otherStep.x == 0 && otherStep.y == -1) return -1;
            if (step.x > 0 && otherStep.x > 0) {
                val rico1 = if (step.y == 0) 0.0 else step.y.toDouble() / step.x
                val rico2 = if (otherStep.y == 0) 0.0 else otherStep.y.toDouble() / otherStep.x
                return rico1.compareTo(rico2)
            }
            if (step.x < 0 && otherStep.x < 0) {
                val rico1 = if (step.y == 0) 0.0 else step.y.toDouble() / step.x
                val rico2 = if (otherStep.y == 0) 0.0 else otherStep.y.toDouble() / otherStep.x
                return rico1.compareTo(rico2)
            }
            if (step.x == 0 && step.y == 1) {
                return otherStep.x
            }
            if (otherStep.x == 0 && otherStep.y == -1) {
                return step.x
            }
            return -step.x.compareTo(otherStep.x)
        }

    }

    @Test
    internal fun testSmallInput() {
        val input = ".#..#\n" +
                ".....\n" +
                "#####\n" +
                "....#\n" +
                "...##"
        val asteroidBelt = parseBelt(input)
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(1, 0)))
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(4, 0)))
        assertEquals(6, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(0, 2)))
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(1, 2)))
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(2, 2)))
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(3, 2)))
        assertEquals(5, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(4, 2)))
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(4, 3)))
        assertEquals(8, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(3, 4)))
        assertEquals(7, calculateNumberOfVisibleAsteroids(asteroidBelt, Coordinate(4, 4)))
    }

    private fun calculateBestAsteroid(asteroidBelt: AsteroidBelt): Pair<Coordinate, Int>? {
        return asteroidBelt.asteroids.map { currentAsteroid ->
            Pair(currentAsteroid, calculateNumberOfVisibleAsteroids(asteroidBelt, currentAsteroid))
        }.maxBy { it.second }
    }

    private fun calculateNumberOfVisibleAsteroids(asteroidBelt: AsteroidBelt, coordinate: Coordinate): Int {
        val myBelt = asteroidBelt.copy(asteroids = asteroidBelt.asteroids.filterNot { coordinate == it })
        val mappedToSteps = myBelt.asteroids.map { asteroid -> smallestStep(coordinate, asteroid) }
        return mappedToSteps.distinct().count()
    }

    @Test
    internal fun testSmallestStep() {
        assertEquals(Pair(1, 1), smallestStep(Coordinate(2, 2), Coordinate(4, 4)))
        assertEquals(Pair(1, 1), smallestStep(Coordinate(2, 2), Coordinate(5, 5)))
        assertEquals(Pair(0, 1), smallestStep(Coordinate(2, 2), Coordinate(2, 4)))
        assertEquals(Pair(0, 1), smallestStep(Coordinate(2, 2), Coordinate(2, 5)))
        assertEquals(Pair(3, 2), smallestStep(Coordinate(2, 2), Coordinate(5, 4)))
        assertEquals(Pair(3, 2), smallestStep(Coordinate(2, 2), Coordinate(8, 6)))
        assertEquals(Pair(2, 1), smallestStep(Coordinate(2, 2), Coordinate(12, 7)))
        assertEquals(Pair(-1, 0), smallestStep(Coordinate(1, 2), Coordinate(0, 2)))
    }

    fun gcm(a: Int, b: Int): Int = if (b == 0) a else gcm(b, a % b)

    private fun smallestStep(c1: Coordinate, c2: Coordinate): Step {
        var step = Step(c2.x - c1.x, c2.y - c1.y)
        //reduce to smallest step
        val gcm = abs(gcm(step.x, step.y))
        step = Step(step.x / gcm, step.y / gcm)
        return step
    }

    private fun parseBelt(input: String): AsteroidBelt {
        val rows = input.split("\n")
        val yMax = rows.size - 1
        val xMax = rows[0].length - 1
        val asteroids = IntRange(0, xMax)
                .flatMap { x ->
                    IntRange(0, yMax)
                            .map { y -> Pair(y, rows[y][x]) }
                            .filter { (_, c) -> c == '#' }
                            .map { (y, _) -> Coordinate(x, y) }
                }
        return AsteroidBelt(asteroids, xMax, yMax)
    }

    data class AsteroidBelt(val asteroids: List<Coordinate>, val xMax: Int, val yMax: Int) {
        fun contains(coordinate: Coordinate): Boolean {
            return asteroids.contains(coordinate)
        }
    }

    data class Step(val x: Int, val y: Int)

    data class Coordinate(val x: Int, val y: Int)

    val taskInput = ".#....#.###.........#..##.###.#.....##...\n" +
            "...........##.......#.#...#...#..#....#..\n" +
            "...#....##..##.......#..........###..#...\n" +
            "....#....####......#..#.#........#.......\n" +
            "...............##..#....#...##..#...#..#.\n" +
            "..#....#....#..#.....#.#......#..#...#...\n" +
            ".....#.#....#.#...##.........#...#.......\n" +
            "#...##.#.#...#.......#....#........#.....\n" +
            "....##........#....#..........#.......#..\n" +
            "..##..........##.....#....#.........#....\n" +
            "...#..##......#..#.#.#...#...............\n" +
            "..#.##.........#...#.#.....#........#....\n" +
            "#.#.#.#......#.#...##...#.........##....#\n" +
            ".#....#..#.....#.#......##.##...#.......#\n" +
            "..#..##.....#..#.........#...##.....#..#.\n" +
            "##.#...#.#.#.#.#.#.........#..#...#.##...\n" +
            ".#.....#......##..#.#..#....#....#####...\n" +
            "........#...##...#.....#.......#....#.#.#\n" +
            "#......#..#..#.#.#....##..#......###.....\n" +
            "............#..#.#.#....#.....##..#......\n" +
            "...#.#.....#..#.......#..#.#............#\n" +
            ".#.#.....#..##.....#..#..............#...\n" +
            ".#.#....##.....#......##..#...#......#...\n" +
            ".......#..........#.###....#.#...##.#....\n" +
            ".....##.#..#.....#.#.#......#...##..#.#..\n" +
            ".#....#...#.#.#.......##.#.........#.#...\n" +
            "##.........#............#.#......#....#..\n" +
            ".#......#.............#.#......#.........\n" +
            ".......#...##........#...##......#....#..\n" +
            "#..#.....#.#...##.#.#......##...#.#..#...\n" +
            "#....##...#.#........#..........##.......\n" +
            "..#.#.....#.....###.#..#.........#......#\n" +
            "......##.#...#.#..#..#.##..............#.\n" +
            ".......##.#..#.#.............#..#.#......\n" +
            "...#....##.##..#..#..#.....#...##.#......\n" +
            "#....#..#.#....#...###...#.#.......#.....\n" +
            ".#..#...#......##.#..#..#........#....#..\n" +
            "..#.##.#...#......###.....#.#........##..\n" +
            "#.##.###.........#...##.....#..#....#.#..\n" +
            "..........#...#..##..#..##....#.........#\n" +
            "..#..#....###..........##..#...#...#..#.."
}