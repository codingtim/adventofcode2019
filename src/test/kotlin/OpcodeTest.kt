import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OpcodeTest {
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

    private fun runOpcode(data: String, input: OpcodeInput) = runBlocking { Opcode(splitOpcodeString(data), input, outputStore).execute() }

}

class OutputStore: OpcodeOutput {
    private var lastOutput = -1L
    override suspend fun receive(output: Long) {
        lastOutput = output;
    }
    fun get(): Long {
        val ret = lastOutput;
        lastOutput = -1
        return ret
    }
}

class FixedInput(private val input: Long): OpcodeInput {
    override suspend fun get(): Long {
        return input;
    }
}