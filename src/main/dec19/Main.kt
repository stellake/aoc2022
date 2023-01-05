package dec19

import common.readDraftLines
import common.readLines

const val DAY = 19

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 33
    solvePart1(input) // 1294
    solvePart2(input) // 13640
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val minutesWorked = 24

    val qualityLevels = data.map {
        println("Blueprint: ${it.id}")
        val maxGeodes = getMaxGeodesForBluePrint(it, minutesWorked)
        it.calculateQualityLevel(maxGeodes)
    }

    println("Part 1 answer: ${qualityLevels.sum()}")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input).take(3) // Only 3 first blueprints
    val maxGeodes = data.map {
        println("Blueprint: ${it.id}")
        getMaxGeodesForBluePrint(it, 32)
    }
    val answer = maxGeodes.reduce(Long::times)
    println("Part 2 answer: $answer")
}

private fun getMaxGeodesForBluePrint(bluePrint: BluePrint, minutesWorked: Int): Long {
    val visitedStates = mutableSetOf<TreeState>()
    val startState = TreeState(Resources(), Robots(ore = 1), 0)
    val maxGeodesByMinute = mutableMapOf<Int, Int>()
    return getMaxGeodes(startState, bluePrint, minutesWorked, maxGeodesByMinute, visitedStates)
}

private fun getMaxGeodes(
    currentState: TreeState,
    bluePrint: BluePrint,
    totalMinutesToWork: Int,
    maxGeodesByMinute: MutableMap<Int, Int>,
    seenStates: MutableSet<TreeState>
): Long {
    if (currentState.currentMinutes == totalMinutesToWork) {
        return currentState.resources.geode.toLong()
    }
    if (seenStates.contains(currentState)) return 0 else seenStates.add(currentState)

    val maxGeodes = maxGeodesByMinute[currentState.currentMinutes] ?: 0
    val turnsLeft = totalMinutesToWork - currentState.currentMinutes
    val geodesToEnd = currentState.resources.geode + currentState.robots.geode * turnsLeft
    if (geodesToEnd < maxGeodes) return 0

    if (maxGeodes < currentState.resources.geode) {
        maxGeodesByMinute[currentState.currentMinutes] = currentState.resources.geode
    }

    val possibleRobots = bluePrint.getPossibleRobotsToBuy(currentState.resources, currentState.robots)
    val itemsToAdd = mutableSetOf<TreeState>()
    possibleRobots.forEach {
        val currentMinutes = currentState.currentMinutes + 1
        val costs = it.getCost(bluePrint)
        val afterSpend = currentState.resources.afterCost(costs)
        val resourcesAfterRobotsWork = currentState.robots.resourcesAfterWorkDay(afterSpend)
        val newRobots = currentState.robots.afterAdding(it)
        itemsToAdd.add(TreeState(resourcesAfterRobotsWork, newRobots, currentMinutes))
    }
    return itemsToAdd.maxOf { item -> getMaxGeodes(item, bluePrint, totalMinutesToWork, maxGeodesByMinute, seenStates) }
}

private fun getData(input: List<String>): List<BluePrint> {
    return input.map {
        val parts = it.split(" Each ")
        val bluePrintId = parts[0].split(" ", ":")[1]
        BluePrint(bluePrintId.toInt(), parts[1].getRobotCost(), parts[2].getRobotCost(), parts[3].getRobotCost(), parts[4].getRobotCost())
    }
}

private fun String.getRobotCost(): RobotCost {
    val costs = this.split("costs ")[1].split(" and ", ".")
    val oreCostString = costs.find { it.contains("ore")}
    val oreCost = oreCostString?.substringBefore(" ore")?.toInt() ?: 0

    val clayCostString = costs.find { it.contains("clay")}
    val clayCost = clayCostString?.substringBefore(" clay")?.toInt() ?: 0

    val obsidianCostString = costs.find { it.contains("obsidian")}
    val obsidianCost = obsidianCostString?.substringBefore(" obsidian")?.toInt() ?: 0

    return RobotCost(oreCost, clayCost, obsidianCost)
}

data class Resources(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
) {
    fun canBuy(robotCost: RobotCost): Boolean {
        return this.ore >= robotCost.ore && this.clay >= robotCost.clay && this.obsidian >= robotCost.obsidian
    }

    fun afterCost(robotCost: RobotCost): Resources {
        if (this.ore - robotCost.ore < 0) throw Error("Negative COST!!!")
        return Resources(
            this.ore - robotCost.ore,
            this.clay - robotCost.clay,
            this.obsidian - robotCost.obsidian,
            this.geode
        )
    }
}

data class Robots(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
) {
    fun resourcesAfterWorkDay(resources: Resources): Resources {
        return Resources(
            resources.ore + this.ore,
            resources.clay + this.clay,
            resources.obsidian + this.obsidian,
            resources.geode + this.geode
        )
    }

    fun getCost(bluePrint: BluePrint): RobotCost {
        val oreCost = bluePrint.oreRobotCost.getForNRobots(ore)
        val clayCost = bluePrint.clayRobotCost.getForNRobots(clay)
        val obsCost = bluePrint.obsidianRobotCost.getForNRobots(obsidian)
        val geodeCost = bluePrint.geodeRobotCost.getForNRobots(geode)
        return RobotCost(
            oreCost.ore + clayCost.ore + obsCost.ore + geodeCost.ore,
            obsCost.clay + geodeCost.clay,
            geodeCost.obsidian
        )
    }

    fun afterAdding(robots: Robots): Robots {
        return Robots(
            this.ore + robots.ore,
            this.clay + robots.clay,
            this.obsidian + robots.obsidian,
            this.geode + robots.geode
        )
    }
}

data class TreeState(
    val resources: Resources,
    val robots: Robots,
    val currentMinutes: Int,
)

data class RobotCost(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0
) {
    fun getForNRobots(numberOfRobots: Int): RobotCost {
        return RobotCost(
            this.ore * numberOfRobots,
            this.clay * numberOfRobots,
            this.obsidian * numberOfRobots
        )
    }
}

data class BluePrint(
    val id: Int,
    val oreRobotCost: RobotCost,
    val clayRobotCost: RobotCost,
    val obsidianRobotCost: RobotCost,
    val geodeRobotCost: RobotCost
) {
    fun calculateQualityLevel(geodeCount: Long): Long {
        return geodeCount * id
    }

    fun getPossibleRobotsToBuy(resources: Resources, workingRobots: Robots): List<Robots> {
        val robots = mutableListOf<Robots>()

        if (resources.canBuy(this.geodeRobotCost)) return listOf(Robots(geode = 1))

        if (resources.canBuy(this.obsidianRobotCost)) {
            if (workingRobots.obsidian == 0) return listOf(Robots(obsidian = 1))
            if (this.geodeRobotCost.obsidian > workingRobots.obsidian) {
                robots.add(Robots(obsidian = 1))
            }
        }

        if (resources.canBuy(this.clayRobotCost) && this.obsidianRobotCost.clay > workingRobots.clay) {
            robots.add(Robots(clay = 1))
        }

        val maxOreCost = maxOf(this.oreRobotCost.ore, this.clayRobotCost.ore, this.obsidianRobotCost.ore, this.geodeRobotCost.ore)
        if (resources.canBuy(this.oreRobotCost) && maxOreCost > workingRobots.ore) {
            robots.add(Robots(ore = 1))
        }

        val shouldWaitForGeode = !resources.canBuy(this.geodeRobotCost) && workingRobots.obsidian > 0
        val shouldWaitForObs = !resources.canBuy(this.obsidianRobotCost) && workingRobots.clay > 0
        val shouldWaitForClay = !resources.canBuy(this.clayRobotCost)
        val shouldWaitForOre = !resources.canBuy(this.oreRobotCost)
        if (shouldWaitForClay || shouldWaitForObs || shouldWaitForGeode || shouldWaitForOre) {
            robots.add(Robots())
        }

        return robots
    }
}
