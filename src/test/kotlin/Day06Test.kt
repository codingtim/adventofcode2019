import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class Day06Test {

    @Test
    internal fun checksumTest() {
        val input = ("COM)B\n" +
                "B)C\n" +
                "C)D\n" +
                "D)E\n" +
                "E)F\n" +
                "B)G\n" +
                "G)H\n" +
                "D)I\n" +
                "E)J\n" +
                "J)K\n" +
                "K)L").split("\n").map { Orbit.fromInput(it) }
        println(getOrbitCount(input))
    }

    @Test
    internal fun task1() {
        val lines = Files.readAllLines(Paths.get("src/test/resources/Day06"))
        val input = (lines).map { Orbit.fromInput(it) }
        println(getOrbitCount(input))
        //253104
    }

    private fun getOrbitCount(input: List<Orbit>): Int {
        val planetMap = input.map { it.planet to it.orbits }.toMap()
        return planetMap.map { getOrbitCount(it.key, planetMap) }.sum()
    }

    private fun getOrbitCount(planet: String, planetMap: Map<String, String>): Int {
        val orbits = planetMap[planet]
        return if (orbits != null) 1 + getOrbitCount(orbits, planetMap) else 0
    }

    @Test
    internal fun moveTest() {
        val input = ("COM)B\n" +
                "B)C\n" +
                "C)D\n" +
                "D)E\n" +
                "E)F\n" +
                "B)G\n" +
                "G)H\n" +
                "D)I\n" +
                "E)J\n" +
                "J)K\n" +
                "K)L\n" +
                "K)YOU\n" +
                "I)SAN").split("\n").map { Orbit.fromInput(it) }
        println(getJumpCountToSan(input))
    }

    @Test
    internal fun task2() {
        val lines = Files.readAllLines(Paths.get("test/Day06"))
        val input = (lines).map { Orbit.fromInput(it) }
        println(getJumpCountToSan(input))
    }

    private fun getJumpCountToSan(input: List<Orbit>): Int {
        val planetMap = input.map { it.planet to it.orbits }.toMap()
        val connectedToYou = getConnectedPlanets("YOU", planetMap, mutableListOf())
        val connectedToSan = getConnectedPlanets("SAN", planetMap, mutableListOf())
        val closestMatchingPlanet = connectedToYou.first { connectedToSan.contains(it) }
        val jumpCountYouToShared = getJumpCountTo("YOU", closestMatchingPlanet, planetMap)
        val jumpCountSanToShared = getJumpCountTo("SAN", closestMatchingPlanet, planetMap)
        return jumpCountYouToShared + jumpCountSanToShared
    }

    private fun getJumpCountTo(planet: String, toPlanet: String, planetMap: Map<String, String>): Int {
        val orbits = planetMap[planet]
        return if (orbits != null && orbits != toPlanet) 1 + getJumpCountTo(orbits, toPlanet, planetMap) else 0
    }

    private fun getConnectedPlanets(planet: String, planetMap: Map<String, String>, result: MutableList<String>): List<String> {
        val orbits = planetMap[planet]
        return if (orbits != null) {
            result.add(orbits)
            getConnectedPlanets(orbits, planetMap, result)
        } else {
            result
        }
    }

    data class Orbit(val planet: String, val orbits: String) {
        companion object Orbits {
            fun fromInput(input: String): Orbit {
                val parts = input.split(')')
                return Orbit(parts[1], parts[0])
            }
        }
    }
}