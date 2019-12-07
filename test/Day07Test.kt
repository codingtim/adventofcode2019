import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class Day07Test {

    @Test
    internal fun example1() {
        val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
        val split = splitOpcodeString(input)

        val runSignal = runSignal(InputOutput(listOf(4, 3, 2, 1, 0)), split)
        assertEquals(43210, runSignal.value())
    }

    @Test
    internal fun calculateExample1() {
        val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
        val split = splitOpcodeString(input)

        val (highestValue, bestSignal) = bestSignal(split)
        assertEquals(43210, highestValue)
        assertEquals(listOf(4, 3, 2, 1, 0), bestSignal)
    }

    @Test
    internal fun task1() {
        val input = Files.readAllLines(Paths.get("test/Day07"))[0]
        val split = splitOpcodeString(input)

        val (highestValue, bestSignal) = bestSignal(split)
        println(highestValue)
        println(bestSignal)
    }

    private fun bestSignal(split: MutableList<Int>): Pair<Int, List<Int>> {
        val permutations = Files.readAllLines(Paths.get("test/Day07Signals")).map { it.split(" ").map { it -> Integer.parseInt(it) } }

        var highestValue = -1
        var bestSignal = listOf<Int>()
        for (signal in permutations) {
            val inputOutput = runSignal(InputOutput(signal), split)
            if (inputOutput.value() > highestValue) {
                bestSignal = signal
                highestValue = inputOutput.value()
            }
        }
        return Pair(highestValue, bestSignal)
    }

    private fun runSignal(inputOutput: InputOutput, split: MutableList<Int>): InputOutput {
        for (i in 0..4) {
            Opcode(split.toMutableList(), inputOutput, inputOutput).execute()
        }
        return inputOutput
    }

    class InputOutput(private val inputs: List<Int>) : OpcodeInput, OpcodeOutput {

        private var indexOfInputs = 0
        private var receivedValue = 0
        private var returns = 0;

        override fun get(): Int {
            return if (returns == 0) {
                returns = 1
                inputs[indexOfInputs++]
            } else {
                returns = 0
                receivedValue
            }
        }

        override fun receive(output: Int) {
            receivedValue = output
        }

        fun value(): Int = receivedValue!!
    }

}