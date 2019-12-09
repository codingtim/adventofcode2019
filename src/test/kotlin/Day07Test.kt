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

        val output = runSignal(listOf(4L, 3L, 2L, 1L, 0L), split)
        assertEquals(43210, output)
    }

    @Test
    internal fun calculateExample1() {
        val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
        val split = splitOpcodeString(input)
        val possibleSignals = Files.readAllLines(Paths.get("src/test/resources/Day07Signals")).map { it.split(" ").map { s -> s.toLong() } }

        val highestValue = bestSignal(split, possibleSignals)
        assertEquals(43210, highestValue)
    }

    @Test
    internal fun task1() {
        val input = Files.readAllLines(Paths.get("src/test/resources/Day07"))[0]
        val split = splitOpcodeString(input)
        val possibleSignals = Files.readAllLines(Paths.get("src/test/resources/Day07Signals")).map { it.split(" ").map { s -> s.toLong() } }

        val highestValue = bestSignal(split, possibleSignals)
        println(highestValue)
    }

    @Test
    internal fun example2() {
        val input = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
        val split = splitOpcodeString(input)
        val signal = listOf(9L, 8L, 7L, 6L, 5L)

        val result = runSignal(signal, split)
        assertEquals(139629729, result)
    }

    @Test
    internal fun calculateExample2() {
        val input = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
        val split = splitOpcodeString(input)
        val possibleSignals = Files.readAllLines(Paths.get("src/test/resources/Day07Signals02")).map { it.split(" ").map { s -> s.toLong() } }

        val result = bestSignal(split, possibleSignals)
        assertEquals(139629729, result)
    }

    @Test
    internal fun example2_2() {
        val input = "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"
        val split = splitOpcodeString(input)
        val signal = listOf(9L, 7L, 8L, 5L, 6L)

        val result = runSignal(signal, split)
        assertEquals(18216, result)
    }

    @Test
    internal fun task2() {
        val input = Files.readAllLines(Paths.get("src/test/resources/Day07"))[0]
        val split = splitOpcodeString(input)
        val possibleSignals = Files.readAllLines(Paths.get("src/test/resources/Day07Signals02")).map { it.split(" ").map { s -> s.toLong() } }

        val highestValue = bestSignal(split, possibleSignals)
        println(highestValue)
    }

    private fun bestSignal(split: MutableList<Long>, possibleSignals: List<List<Long>>): Long {
        var highestValue = -1L
        for (signal in possibleSignals) {
            val value = runSignal(signal, split)
            if (value > highestValue) {
                highestValue = value
            }
        }
        return highestValue
    }

    private fun runSignal(signal: List<Long>, input: MutableList<Long>): Long {
        val ampEIO = InputOutput("E", mutableListOf(signal[4]))
        val ampDIO = InputOutput("D", mutableListOf(signal[3]))
        val ampCIO = InputOutput("C", mutableListOf(signal[2]))
        val ampBIO = InputOutput("B", mutableListOf(signal[1]))
        val ampAIO = InputOutput("A", mutableListOf(signal[0], 0))

        val ampE = Opcode("E", input.toMutableList(), ampEIO, ampAIO)
        val ampD = Opcode("D", input.toMutableList(), ampDIO, ampEIO)
        val ampC = Opcode("C", input.toMutableList(), ampCIO, ampDIO)
        val ampB = Opcode("B", input.toMutableList(), ampBIO, ampCIO)
        val ampA = Opcode("A", input.toMutableList(), ampAIO, ampBIO)

        return runBlocking {
            val amps = listOf(ampA, ampB, ampC, ampD).map { amp -> async { amp.execute() } }
            async { ampE.execute() }
            amps.awaitAll()
            val receive = ampAIO.channel.receive()
            receive
        }
    }

    class InputOutput(private val amp: String, private val inputs: MutableList<Long>) : OpcodeInput, OpcodeOutput {
        val channel: Channel<Long> = Channel()

        override suspend fun get(): Long {
            val res = if (inputs.isEmpty()) {
                println("$amp waiting for value")
                channel.receive();
            } else {
                val i = inputs[0]
                inputs.removeAt(0)
                i
            }
            println("Getting from $amp $res")
            return res;
        }

        override suspend fun receive(output: Long) {
            println("Receiving on $amp $output")
            channel.send(output)
            println("Done receiving on $amp $output")
        }
    }

}