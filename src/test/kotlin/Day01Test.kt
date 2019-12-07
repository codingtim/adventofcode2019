import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class Tests {
    @Test
    internal fun fuelCost() {
        assertEquals(2, fuelCostOf(12))
        assertEquals(2, fuelCostOf(14))
        assertEquals(654, fuelCostOf(1969))
        assertEquals(33583, fuelCostOf(100756))
    }

    @Test
    internal fun fuelCostRecursive() {
        assertEquals(50346, fuelCostOfRecursive(100756))
    }

    @Test
    internal fun part1() {
        val lines = Files.readAllLines(Paths.get("src/test/resources/Day01"))
        val sum = lines
                .map { line -> Integer.parseInt(line) }
                .map { mass -> fuelCostOf(mass) }
                .sum()
        println(sum)
    }

    @Test
    internal fun part2() {
        val lines = Files.readAllLines(Paths.get("src/test/resources/Day01"))
        val sum = lines
                .map { line -> Integer.parseInt(line) }
                .map { mass -> fuelCostOfRecursive(mass) }
                .sum()
        println(sum)
    }

    private fun fuelCostOf(moduleMass: Int): Int = moduleMass / 3 - 2

    private fun fuelCostOfRecursive(moduleMass: Int): Int {
        val cost = moduleMass / 3 - 2
        if (cost <= 0) return 0
        return cost + fuelCostOfRecursive(cost)
    }
}