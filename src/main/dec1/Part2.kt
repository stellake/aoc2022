package dec1

// 197400
fun main() {
    val data = getDec1Data().sortedDescending()
    val sumOfThreeMostCals = data[0] + data[1] + data[2]
    println("Sum of three most cals: $sumOfThreeMostCals")
}
