import org.junit.jupiter.api.Test
import java.lang.System.exit
import kotlin.test.assertEquals

class Day02Test {
    @Test
    internal fun opcodeTest() {
        assertEquals("2,0,0,0,99", opcode(split("1,0,0,0,99")))
        assertEquals("2,3,0,6,99", opcode(split("2,3,0,3,99")))
        assertEquals("2,4,4,5,99,9801", opcode(split("2,4,4,5,99,0")))
        assertEquals("30,1,1,4,2,5,6,0,99", opcode(split("1,1,1,4,99,5,6,0,99")))
    }

    @Test
    internal fun part1() {
        val result = opcode(split("1,12,2,3,1,1,2,3,1,3,4,3,1,5,0,3,2,6,1,19,1,19,10,23,2,13,23,27,1,5,27,31,2,6,31,35,1,6,35,39,2,39,9,43,1,5,43,47,1,13,47,51,1,10,51,55,2,55,10,59,2,10,59,63,1,9,63,67,2,67,13,71,1,71,6,75,2,6,75,79,1,5,79,83,2,83,9,87,1,6,87,91,2,91,6,95,1,95,6,99,2,99,13,103,1,6,103,107,1,2,107,111,1,111,9,0,99,2,14,0,0"))
        println(result)
    }

    @Test
    internal fun part2() {
        val input = "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,6,1,19,1,19,10,23,2,13,23,27,1,5,27,31,2,6,31,35,1,6,35,39,2,39,9,43,1,5,43,47,1,13,47,51,1,10,51,55,2,55,10,59,2,10,59,63,1,9,63,67,2,67,13,71,1,71,6,75,2,6,75,79,1,5,79,83,2,83,9,87,1,6,87,91,2,91,6,95,1,95,6,99,2,99,13,103,1,6,103,107,1,2,107,111,1,111,9,0,99,2,14,0,0"
        for (noun in 0..99) {
            for(verb in 0..99) {
                testVerb(input, noun, verb)
             }
        }
    }

    private fun testVerb(input: String, noun: Int, verb: Int) {
        val inputArray = split(input)
        inputArray[1] = noun
        inputArray[2] = verb
        val result = opcode(inputArray)
        if (result.split(",")[0] == "19690720") {
            println("$noun $verb")
            exit(0)
        }

    }

    private fun split(s: String): MutableList<Int> {
        return s.split(",").map { Integer.parseInt(it) }.toMutableList()
    }

    private fun opcode(input: MutableList<Int>): String {
        var index = 0
        while (true) {
            index = executeValueOf(input, index)
            if (input[index] == 99) break
        }
        return input.joinToString(separator = ",")
    }

    private fun executeValueOf(split: MutableList<Int>, i: Int): Int {
        val value = split[i]
        if (value == 1) {
            split[split[i + 3]] = split[split[i + 1]] + split[split[i + 2]]
        }
        if (value == 2) {
            split[split[i + 3]] = split[split[i + 1]] * split[split[i + 2]]
        }
        return i + 4
    }
}