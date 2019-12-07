import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day04Test {
    @Test
    internal fun testMatches() {
        assertEquals(true, matches("111111"))
        assertEquals(false, matches("223450"))
        assertEquals(true, matches("223455"))
        assertEquals(true, matches("223456"))
        assertEquals(false, matches("123789"))
        assertEquals(true, matches("123389"))
    }

    @Test
    internal fun task1() {
        val count = IntRange(147981, 691423).map { it.toString() }.filter { matches(it) }.count()
        println(count)
    }

    @Test
    internal fun testMatchesNoRepetition() {
        assertEquals(true, matchesNoRepetition("112233"))
        assertEquals(false, matchesNoRepetition("123444"))
        assertEquals(true, matchesNoRepetition("111122"))
    }

    @Test
    internal fun task2() {
        val count = IntRange(147981, 691423).map { it.toString() }.filter { matchesNoRepetition(it) }.count()
        println(count)
    }

    private fun matchesNoRepetition(regex: String): Boolean {
        val split = regex.toCharArray().map { Integer.parseInt(it.toString()) }
        if (allIncreasing(split)) {
            if (containsSameOnce(split)) {
                return true
            }
        }
        return false
    }
    private fun matches(regex: String): Boolean {
        val split = regex.toCharArray().map { Integer.parseInt(it.toString()) }
        if (allIncreasing(split)) {
            if (containsSame(split)) {
                return true
            }
        }
        return false
    }

    private fun containsSameOnce(split: List<Int>): Boolean {
        var i = 0;
        while (i < 5) {
            val same = IntRange(i, 5).map { split[it] }.filter { it == split[i] }.count()
            if(same == 2) {
                return true
            }
            i += same
        }
        return false
    }

    private fun containsSame(split: List<Int>): Boolean {
        for (i in 0..4) {
            if (split[i + 1] == split[i]) {
                return true
            }
        }
        return false
    }

    private fun allIncreasing(split: List<Int>): Boolean {
        for (i in 0..4) {
            if (split[i + 1] < split[i]) {
                return false
            }
        }
        return true
    }
}