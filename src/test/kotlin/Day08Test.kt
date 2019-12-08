import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class Day08Test {

    @Test
    internal fun smallImageChecksum() {
        val rows = 3
        val columns = 2
        val input = "123456789012"
        val pixelsPerLayer = rows * columns
        val numberOfLayers = input.length / pixelsPerLayer

        val layerWithLeastAmountOfZeroes = IntRange(0, numberOfLayers - 1)
                .map { layerNumber -> Pair(layerNumber, input.substring(layerNumber * pixelsPerLayer, layerNumber * pixelsPerLayer + pixelsPerLayer)) }
                .map { (layerNumber, layerData) -> Pair(layerNumber, layerData.filter { c -> c == '0' }.length) }
                .minBy { (_, numberOfZeroes) -> numberOfZeroes }!!

        assertEquals(Pair(0, 0), layerWithLeastAmountOfZeroes)

        val layer = layerWithLeastAmountOfZeroes.first
        val layerData = input.substring(layer * pixelsPerLayer, layer * pixelsPerLayer + pixelsPerLayer)
        val numberOfOnes = layerData.filter { c -> c == '1' }.length
        val numberOfTwos = layerData.filter { c -> c == '2' }.length
        println(numberOfOnes * numberOfTwos)
    }

    @Test
    internal fun task1() {
        val rows = 25
        val columns = 6
        val input = Files.readAllLines(Paths.get("src/test/resources/Day08"))[0]
        val pixelsPerLayer = rows * columns
        val numberOfLayers = input.length / pixelsPerLayer

        val layerWithLeastAmountOfZeroes = IntRange(0, numberOfLayers - 1)
                .map { layerNumber -> Pair(layerNumber, input.substring(layerNumber * pixelsPerLayer, layerNumber * pixelsPerLayer + pixelsPerLayer)) }
                .map { (layerNumber, layerData) -> Pair(layerNumber, layerData.filter { c -> c == '0' }.length) }
                .minBy { (_, numberOfZeroes) -> numberOfZeroes }!!

        val layer = layerWithLeastAmountOfZeroes.first
        val layerData = input.substring(layer * pixelsPerLayer, layer * pixelsPerLayer + pixelsPerLayer)
        val numberOfOnes = layerData.filter { c -> c == '1' }.length
        val numberOfTwos = layerData.filter { c -> c == '2' }.length
        println(numberOfOnes * numberOfTwos)
    }


}