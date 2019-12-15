import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.max

class Day14Test {

    @Test
    internal fun simple() {
        val input = (
                "10 ORE => 10 A\n" +
                        "1 ORE => 1 B\n" +
                        "7 A, 1 B => 1 C\n" +
                        "7 A, 1 C => 1 D\n" +
                        "7 A, 1 D => 1 E\n" +
                        "7 A, 1 E => 1 FUEL"
                ).split("\n")
        val reactions = input.map { parseReaction(it) }
        val reactionsMap = reactions.map { it.produces.chemical to it }.toMap()

        val cost = produce(Chemical("FUEL"), reactionsMap, mutableMapOf())
        assertEquals(31, cost)
    }

    @Test
    internal fun simple2() {
        val input = (
                "9 ORE => 2 A\n" +
                        "8 ORE => 3 B\n" +
                        "7 ORE => 5 C\n" +
                        "3 A, 4 B => 1 AB\n" +
                        "5 B, 7 C => 1 BC\n" +
                        "4 C, 1 A => 1 CA\n" +
                        "2 AB, 3 BC, 4 CA => 1 FUEL"
                ).split("\n")
        val reactions = input.map { parseReaction(it) }
        val reactionsMap = reactions.map { it.produces.chemical to it }.toMap()

        val cost = produce(Chemical("FUEL"), reactionsMap, mutableMapOf())
        assertEquals(165, cost)
    }

    @Test
    internal fun complex() {
        val input = (
                "171 ORE => 8 CNZTR\n" +
                        "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL\n" +
                        "114 ORE => 4 BHXH\n" +
                        "14 VRPVC => 6 BMBT\n" +
                        "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL\n" +
                        "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT\n" +
                        "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW\n" +
                        "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW\n" +
                        "5 BMBT => 4 WPTQ\n" +
                        "189 ORE => 9 KTJDG\n" +
                        "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP\n" +
                        "12 VRPVC, 27 CNZTR => 2 XDBXC\n" +
                        "15 KTJDG, 12 BHXH => 5 XCVML\n" +
                        "3 BHXH, 2 VRPVC => 7 MZWV\n" +
                        "121 ORE => 7 VRPVC\n" +
                        "7 XCVML => 6 RJRHP\n" +
                        "5 BHXH, 4 VRPVC => 5 LTCX"
                ).split("\n")
        val reactions = input.map { parseReaction(it) }
        val reactionsMap = reactions.map { it.produces.chemical to it }.toMap()

        val cost = produce(Chemical("FUEL"), reactionsMap, mutableMapOf())
        assertEquals(2210736, cost)
    }

    @Test
    internal fun task1() {
        val input = Files.readAllLines(Paths.get("src/test/resources/Day14"))
        val reactions = input.map { parseReaction(it) }
        val reactionsMap = reactions.map { it.produces.chemical to it }.toMap()

        val cost = produce(Chemical("FUEL"), reactionsMap, mutableMapOf())
        println(cost)
    }

    @Test
    internal fun simpleTask2() {
        val input = Files.readAllLines(Paths.get("src/test/resources/Day14"))
        val reactions = input.map { parseReaction(it) }
        val reactionsMap = reactions.map { it.produces.chemical to it }.toMap()

        val maxProcurable = maxProcurable(reactionsMap)
        println(maxProcurable)
        assertEquals(82892753, maxProcurable)
    }

    private fun maxProcurable(reactions: Map<Chemical, Reaction>): Int {
        val stock = mutableMapOf<Chemical, Int>()
        var oreAmount = 1000000000000L
        var fuelAmount = 0
        oreAmount -= produce(Chemical("FUEL"), reactions, stock)
        while (oreAmount > 0) {
            fuelAmount++
            if(fuelAmount % 10000 == 0) {
                println("$fuelAmount $oreAmount")
            }
            oreAmount -= produce(Chemical("FUEL"), reactions, stock)
        }
        return fuelAmount
    }

    private fun produce(toProduce: Chemical, reactions: Map<Chemical, Reaction>, stock: MutableMap<Chemical, Int>): Int {
        val reaction = reactions[toProduce] ?: error("No reaction for $toProduce?")
        return if (reaction.reagents.size == 1 && reaction.reagents[0].chemical == Chemical("ORE")) {
            stock[toProduce] = (stock[toProduce] ?: 0) + reaction.produces.amount
            //println("Added ${reaction.produces.amount} of $toProduce")
            reaction.reagents[0].amount
        } else {
            //println(reaction)
            val sum = reaction.reagents.map { reagent -> get(reagent.chemical, reagent.amount, reactions, stock) }.sum()
            stock[toProduce] = (stock[toProduce] ?: 0) + reaction.produces.amount
            //println("Added ${reaction.produces.amount} of $toProduce")
            sum
        }
    }

    private fun get(toGet: Chemical, amount: Int, reactions: Map<Chemical, Reaction>, stock: MutableMap<Chemical, Int>): Int {
        //println("Getting $amount $toGet stock: $stock")
        val amountInStock = stock[toGet] ?: 0
        return if (amountInStock >= amount) {
            //println("Enough $toGet in stock")
            stock[toGet] = amountInStock - amount
            0
        } else {
            //println("Not enough $toGet in stock, producing")
            val oreCost = produce(toGet, reactions, stock)
            oreCost + get(toGet, amount, reactions, stock)
        }
    }

    private fun parseReaction(reaction: String): Reaction {
        val split = reaction.split(" => ")
        val reagents = split[0].split(",").map { it.trim() }
        val produces = split[1].trim()
        return Reaction(
                reagents.map { parseReagent(it) },
                parseReagent(produces)
        )
    }

    private fun parseReagent(reagent: String): ReagentAmount {
        val split = reagent.split(" ")
        return ReagentAmount(Integer.parseInt(split[0]), Chemical(split[1]))
    }

    data class Reaction(val reagents: List<ReagentAmount>, val produces: ReagentAmount)
    data class ReagentAmount(val amount: Int, val chemical: Chemical)
    data class Chemical(val name: String)
}