class Opcode(private val name: String, private val memory: OpcodeMemory, private val input: OpcodeInput, private val output: OpcodeOutput) {
    constructor(name: String, data: MutableList<Long>, input: OpcodeInput, output: OpcodeOutput) : this (name, OpcodeMemory.fromList(data), input, output)
    constructor(data: MutableList<Long>, input: OpcodeInput, output: OpcodeOutput) : this("", data, input, output)

    private var relativeBase = 0
    private var memoryPointer = 0

    suspend fun execute(): String {
        while (true) {
            memoryPointer = executeCurrentMemoryPointer()
            if (memory[memoryPointer] == 99L) break
        }
        println("Computer $name done")
        return memory.joinToString()
    }

    private suspend fun executeCurrentMemoryPointer(): Int {
        fun parameterValue(paramMode: Int, offset: Int): Int {
            return when (paramMode) {
                0 -> memory[memoryPointer + offset].toInt()                     //position mode
                1 -> memoryPointer + offset                                     //direct mode
                else -> memory[memoryPointer + offset].toInt() + relativeBase   //relative mode
            }
        }

        val value = memory[memoryPointer].toInt()
        val operation = value % 10
        val param1Mode = value / 100 % 10
        val param2Mode = value / 1000 % 10
        val param3Mode = value / 10000 % 10
        if (operation == 1) {
            memory[parameterValue(param3Mode, 3)] = memory[parameterValue(param1Mode, 1)] + memory[parameterValue(param2Mode, 2)]
            return memoryPointer + 4
        }
        if (operation == 2) {
            memory[parameterValue(param3Mode, 3)] = memory[parameterValue(param1Mode, 1)] * memory[parameterValue(param2Mode, 2)]
            return memoryPointer + 4
        }
        if (operation == 3) {
            memory[parameterValue(param1Mode, 1)] = input.get()
            return memoryPointer + 2
        }
        if (operation == 4) {
            output.receive(memory[parameterValue(param1Mode, 1)])
            return memoryPointer + 2
        }
        if (operation == 5) {
            return if (memory[parameterValue(param1Mode, 1)] != 0L) memory[parameterValue(param2Mode, 2)].toInt() else (memoryPointer + 3)
        }
        if (operation == 6) {
            return if (memory[parameterValue(param1Mode, 1)] == 0L) memory[parameterValue(param2Mode, 2)].toInt() else memoryPointer + 3
        }
        if (operation == 7) {
            if (memory[parameterValue(param1Mode, 1)] < memory[parameterValue(param2Mode, 2)]) {
                memory[parameterValue(param3Mode, 3)] = 1
            } else {
                memory[parameterValue(param3Mode, 3)] = 0
            }
            return memoryPointer + 4
        }
        if (operation == 8) {
            if (memory[parameterValue(param1Mode, 1)] == memory[parameterValue(param2Mode, 2)]) {
                memory[parameterValue(param3Mode, 3)] = 1
            } else {
                memory[parameterValue(param3Mode, 3)] = 0
            }
            return memoryPointer + 4
        }
        if (operation == 9) {
            relativeBase += memory[parameterValue(param1Mode, 1)].toInt()
            return memoryPointer + 2
        }
        throw IllegalStateException("Unknown operation $operation")
    }
}

class OpcodeMemory(private val data: MutableMap<Int, Long>) {
    operator fun get(index: Int): Long = data[index]?:0
    operator fun set(index: Int, value: Long) {
        data[index] = value
    }

    fun joinToString(): String {
        return data.keys.sorted().map { data[it] }.joinToString(",")
    }

    companion object OpcodeDatas {
        fun fromList(inputData: List<Long>): OpcodeMemory {
            val map = mutableMapOf<Int, Long>()
            for (i in inputData.indices) {
                map[i] = inputData[i]
            }
            return OpcodeMemory(map)
        }
    }
}

interface OpcodeInput {
    suspend fun get(): Long
}

interface OpcodeOutput {
    suspend fun receive(output: Long)
}

fun splitOpcodeString(s: String): MutableList<Long> {
    return s.split(",").map { it.toLong() }.toMutableList()
}

