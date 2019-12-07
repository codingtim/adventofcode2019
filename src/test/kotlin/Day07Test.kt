import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class Day07Test {

    @Test
    internal fun example1() {
        val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
        val split = splitOpcodeString(input)

        val output = runSignal(listOf(4, 3, 2, 1, 0), split)
        assertEquals(43210, output.value())
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
         val input = Files.readAllLines(Paths.get("src/test/resources/Day07"))[0]
         val split = splitOpcodeString(input)

         val (highestValue, bestSignal) = bestSignal(split)
         println(highestValue)
         println(bestSignal)
     }

    private fun bestSignal(split: MutableList<Int>): Pair<Int, List<Int>> {
        val permutations = Files.readAllLines(Paths.get("src/test/resources/Day07Signals")).map { it.split(" ").map { it -> Integer.parseInt(it) } }

        var highestValue = -1
        var bestSignal = listOf<Int>()
        for (signal in permutations) {
            val inputOutput = runSignal(signal, split)
            if (inputOutput.value() > highestValue) {
                bestSignal = signal
                highestValue = inputOutput.value()
            }
        }
        return Pair(highestValue, bestSignal)
    }

    private fun runSignal(signal: List<Int>, split: MutableList<Int>): ResultOutput {
        val resultIO = ResultOutput()
        val ampEIO = InputOutput("E", mutableListOf(signal[4]))
        val ampDIO = InputOutput("D", mutableListOf(signal[3]))
        val ampCIO = InputOutput("C", mutableListOf(signal[2]))
        val ampBIO = InputOutput("B", mutableListOf(signal[1]))
        val ampAIO = InputOutput("A", mutableListOf(signal[0], 0))

        val ampE = Opcode07("E", split.toMutableList(), ampEIO, resultIO)
        val ampD = Opcode07("D", split.toMutableList(), ampDIO, ampEIO)
        val ampC = Opcode07("C", split.toMutableList(), ampCIO, ampDIO)
        val ampB = Opcode07("B", split.toMutableList(), ampBIO, ampCIO)
        val ampA = Opcode07("A", split.toMutableList(), ampAIO, ampBIO)

        runBlocking {
            val amps = listOf(ampA, ampB, ampC, ampD, ampE).map { amp -> async { amp.execute() } }
            amps.awaitAll()
        }
        return resultIO
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

    class ResultOutput : OpcodeOutput07 {
        private var result: Int = -1
        override suspend fun receive(output: Int) {
            result = output
        }

        fun value(): Int = result
    }

    class InputOutput(private val amp: String, private val inputs: MutableList<Int>) : OpcodeInput07, OpcodeOutput07 {

        private val channel: Channel<Int> = Channel()

        override suspend fun get(): Int {
            val res = if (inputs.isEmpty()) {
                channel.receive();
            } else {
                val i = inputs[0]
                inputs.removeAt(0)
                i
            }
            println("Getting from $amp $res")
            return res;
        }

        override suspend fun receive(output: Int) {
            println("Receiving on $amp $output")
            channel.send(output)
        }
    }

}