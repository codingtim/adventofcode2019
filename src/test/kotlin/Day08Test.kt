import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

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

    @Test
    internal fun smallImageResult() {
        val rows = 2
        val columns = 2
        val input = "0222112222120000"
        val pixelsPerLayer = rows * columns
        val numberOfLayers = input.length / pixelsPerLayer

        println(getMessage(pixelsPerLayer, numberOfLayers, input))
    }

    @Test
    internal fun task2() {
        val rows = 25
        val columns = 6
        val input = Files.readAllLines(Paths.get("src/test/resources/Day08"))[0]
        val pixelsPerLayer = rows * columns
        val numberOfLayers = input.length / pixelsPerLayer

        val message = getMessage(pixelsPerLayer, numberOfLayers, input)
        for (i in 0 until pixelsPerLayer) {
            if (i % rows == 0) print('\n')
            if (message[i] == '1') print('x')
            else print(' ')
        }
    }

    private fun getMessage(pixelsPerLayer: Int, numberOfLayers: Int, input: String): CharArray {
        return IntRange(0, pixelsPerLayer - 1)
                .map { pixel ->
                    IntRange(0, numberOfLayers - 1)
                            .map { (it * pixelsPerLayer) + pixel }
                            .map { pixelIndex -> input[pixelIndex] }
                            .dropWhile { it == '2' }
                            .first()
                }
                .toCharArray()
    }
}