import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class IntcodeTest {
    private val outputStore = OutputStore()

    @Test
    internal fun supportLargeValues() {
        runOpcode("104,1125899906842624,99", FixedInput(-1L))
        assertEquals(1125899906842624, outputStore.get())
    }

    @Test
    internal fun supportVeryBigValues() {
        runOpcode("1102,34915192,34915192,7,4,7,99,0", FixedInput(-1L))
        val result = outputStore.get()
        assertEquals(16, result.toString().length)
        assertEquals(1219070632396864, result)
    }

    @Test
    internal fun supportOutOfInputArrayIndexes() {
        runOpcode("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", FixedInput(-1L))
        assertEquals(
                listOf(109L,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99),
                outputStore.getAll()
        )
    }

    private fun runOpcode(data: String, input: IntcodeInput) = runBlocking { Intcode(splitOpcodeString(data), input, outputStore).execute() }

}

class OutputStore: IntcodeOutput {
    private var outputs = mutableListOf<Long>()
    override suspend fun receive(output: Long) {
        outputs.add(output)
    }
    fun get(): Long = outputs.last()
    fun getAll(): List<Long> = outputs
}

class FixedInput(private val input: Long): IntcodeInput {
    override suspend fun get(): Long {
        return input;
    }
}