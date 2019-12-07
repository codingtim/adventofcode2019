class Opcode(private val data: MutableList<Int>, private val input: OpcodeInput, private val output: OpcodeOutput) {

    fun execute(): String {
        var index = 0
        while (true) {
            index = executeValueOf(data, index)
            if (data[index] == 99) break
        }
        return data.joinToString(separator = ",")
    }

    private fun opcode(data: MutableList<Int>): String {
        var index = 0
        while (true) {
            index = executeValueOf(data, index)
            if (data[index] == 99) break
        }
        return data.joinToString(separator = ",")
    }

    private fun executeValueOf(data: MutableList<Int>, index: Int): Int {
        fun parameterValue(paramMode: Int, offset: Int): Int {
            return if (paramMode == 0) data[index + offset] else index + offset
        }
        val value = data[index]
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
            return if (data[parameterValue(param1Mode, 1)] != 0) data[parameterValue(param2Mode, 2)] else index + 3
        }
        if (operation == 6) {
            return if (data[parameterValue(param1Mode, 1)] == 0) data[parameterValue(param2Mode, 2)] else index + 3
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
    fun get(): Int
}


interface OpcodeOutput {
    fun receive(output: Int)
}

fun split(s: String): MutableList<Int> {
    return s.split(",").map { Integer.parseInt(it) }.toMutableList()
}
