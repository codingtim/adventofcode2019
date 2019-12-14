import org.junit.jupiter.api.Test

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

        val produce = produce(Chemical("FUEL"), reactionsMap)
        println(produce)
    }

    private fun produce(toProduce: Chemical, reactions: Map<Chemical, Reaction>): Int {
        val reaction = reactions[toProduce] ?: error("No reaction for $toProduce?")
        if(reaction.reagents.size == 1 && reaction.reagents[0].chemical == Chemical("ORE")) {
            return reaction.reagents[0].amount
        }
        return reaction.reagents.map { reagent -> produce(reagent.chemical, reactions) }.sum()
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