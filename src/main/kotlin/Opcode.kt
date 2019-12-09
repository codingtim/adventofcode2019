class Opcode(private val name: String, private val data: OpcodeData, private val input: OpcodeInput, private val output: OpcodeOutput) {
    constructor(name: String, data: MutableList<Long>, input: OpcodeInput, output: OpcodeOutput) : this (name, OpcodeData.fromList(data), input, output)
    constructor(data: MutableList<Long>, input: OpcodeInput, output: OpcodeOutput) : this("", data, input, output)

    private var relativeBase = 0

    suspend fun execute(): String {
        var index = 0
        while (true) {
            index = executeValueOf(index)
            if (data[index] == 99L) break
        }
        println("Computer $name done")
        return data.joinToString()
    }

    private suspend fun executeValueOf(index: Int): Int {
        fun parameterValue(paramMode: Int, offset: Int): Int {
            return if (paramMode == 0) {
                //position mode
                data[index + offset].toInt()
            } else if (paramMode == 1) {
                //direct mode
                index + offset
            } else {
                //relative mode
                data[index + offset].toInt() + relativeBase
            }
        }

        val value = data[index].toInt()
        val operation = value % 10
        val param1Mode = value / 100 % 10
        val param2Mode = value / 1000 % 10
        val param3Mode = value / 10000 % 10
        if (operation == 1) {
            data[parameterValue(param3Mode, 3)] = data[parameterValue(param1Mode, 1)] + data[parameterValue(param2Mode, 2)]
            return index + 4
        }
        if (operation == 2) {
            data[parameterValue(param3Mode, 3)] = data[parameterValue(param1Mode, 1)] * data[parameterValue(param2Mode, 2)]
            return index + 4
        }
        if (operation == 3) {
            data[parameterValue(param1Mode, 1)] = input.get()
            return index + 2
        }
        if (operation == 4) {
            output.receive(data[parameterValue(param1Mode, 1)])
            return index + 2
        }
        if (operation == 5) {
            return if (data[parameterValue(param1Mode, 1)] != 0L) data[parameterValue(param2Mode, 2)].toInt() else (index + 3)
        }
        if (operation == 6) {
            return if (data[parameterValue(param1Mode, 1)] == 0L) data[parameterValue(param2Mode, 2)].toInt() else index + 3
        }
        if (operation == 7) {
            if (data[parameterValue(param1Mode, 1)] < data[parameterValue(param2Mode, 2)]) {
                data[parameterValue(param3Mode, 3)] = 1
            } else {
                data[parameterValue(param3Mode, 3)] = 0
            }
            return index + 4
        }
        if (operation == 8) {
            if (data[parameterValue(param1Mode, 1)] == data[parameterValue(param2Mode, 2)]) {
                data[parameterValue(param3Mode, 3)] = 1
            } else {
                data[parameterValue(param3Mode, 3)] = 0
            }
            return index + 4
        }
        if (operation == 9) {
            relativeBase += data[parameterValue(param1Mode, 1)].toInt()
            return index + 2
        }
        throw IllegalStateException("Unknown operation $operation")
    }

}

class OpcodeData(private val data: MutableMap<Int, Long>) {
    operator fun get(index: Int): Long = data[index]?:0
    operator fun set(index: Int, value: Long) {
        data[index] = value
    }

    fun joinToString(): String {
        return data.keys.sorted().map { data[it] }.joinToString(",")
    }

    companion object OpcodeDatas {
        fun fromList(inputData: List<Long>): OpcodeData {
            val map = mutableMapOf<Int, Long>()
            for (i in 0 until inputData.size) {
                map[i] = inputData[i]
            }
            return OpcodeData(map)
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

