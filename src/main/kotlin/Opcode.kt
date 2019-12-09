class Opcode(private val name: String, private val data: MutableList<Long>, private val input: OpcodeInput, private val output: OpcodeOutput) {
    constructor(data: MutableList<Long>, input: OpcodeInput, output: OpcodeOutput) : this("", data, input, output)

    suspend fun execute(): String {
        var index = 0
        while (true) {
            index = executeValueOf(index)
            if (data[index] == 99L) break
        }
        println("Computer $name done")
        return data.joinToString(separator = ",")
    }

    private suspend fun executeValueOf(index: Int): Int {
        fun parameterValue(paramMode: Int, offset: Int): Int {
            return if (paramMode == 0) {
                //position mode
                data[index + offset].toInt()
            } else {
                //direct mode
                index + offset
            }
        }

        val value = data[index].toInt()
        val operation = value % 10
        val param1Mode = value / 100 % 10
        val param2Mode = value / 1000 % 100
        val param3Mode = value / 10000 % 1000
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
        throw IllegalStateException("Unknown operation $operation")
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

