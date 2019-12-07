import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class Day07Test {

    @Test
    internal fun example1() {
        val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
        val split = splitOpcodeString(input)

        val runSignal = runSignal(listOf(4, 3, 2, 1, 0), split)
        println(runSignal)
        //assertEquals(43210, runSignal)
    }

   /* @Test
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
    }*/

   /* private fun bestSignal(split: MutableList<Int>): Pair<Int, List<Int>> {
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
    }*/

    private fun runSignal(signal: List<Int>, split: MutableList<Int>): OpcodeOutput {
        val ampEIO = InputOutput(mutableListOf(signal[4]))
        val ampDIO = InputOutput(mutableListOf(signal[3]))
        val ampCIO = InputOutput(mutableListOf(signal[2]))
        val ampBIO = InputOutput(mutableListOf(signal[1]))
        val ampAIO = InputOutput(mutableListOf(signal[0], 0))

        val ampE = Opcode(split.toMutableList(), ampEIO, ampEIO)
        val ampD = Opcode(split.toMutableList(), ampDIO, ampEIO)
        val ampC = Opcode(split.toMutableList(), ampCIO, ampDIO)
        val ampB = Opcode(split.toMutableList(), ampBIO, ampCIO)
        val ampA = Opcode(split.toMutableList(), ampAIO, ampBIO)

        return ampEIO
    }

//    @Test
//    internal fun calculateExample2() {
//        val input = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
//        val split = splitOpcodeString(input)
//
//        val amp1 = Opcode(split.toMutableList(), inputOutput, inputOutput)
//        val amp2 = Opcode(split.toMutableList(), inputOutput, inputOutput)
//        val amp3 = Opcode(split.toMutableList(), inputOutput, inputOutput)
//        val amp4 = Opcode(split.toMutableList(), inputOutput, inputOutput)
//        val amp5 = Opcode(split.toMutableList(), inputOutput, inputOutput)
//        val amps = listOf(amp1, amp2, amp3, amp4, amp5)
//        var amp = 0;
//        while(true) {
//            amps[amp % 5].execute()
//            amp++
//        }
//
//        assertEquals(139629729, inputOutput.value())
//    }

    class InputOutput(private val inputs: MutableList<Int>) : OpcodeInput, OpcodeOutput {

        //private val channel: kotlinx.coroutines.channels.Channel<Int>
        override fun get(): Int {
            if(inputs.isEmpty()) {

            }
            val ret = inputs[0]
            inputs.removeAt(0)
            return ret;
        }

        override fun receive(output: Int) {
            inputs.add(output)
        }

        fun value(): Int = inputs[0]
    }

}