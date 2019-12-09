import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class Day09Test {

    private val outputStore = OutputStore()

    @Test
    internal fun task1() {
        val input = Files.readAllLines(Paths.get("src/test/resources/Day09"))[0]
        runOpcode(input, FixedInput(1L))
        print(outputStore.getAll())
    }

    @Test
    internal fun task2() {
        val input = Files.readAllLines(Paths.get("src/test/resources/Day09"))[0]
        runOpcode(input, FixedInput(2L))
        print(outputStore.getAll())
    }

    private fun runOpcode(data: String, input: OpcodeInput) = runBlocking { Opcode(splitOpcodeString(data), input, outputStore).execute() }

}