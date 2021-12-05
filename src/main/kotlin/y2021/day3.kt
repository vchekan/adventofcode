package y2021.d3

import java.io.File

val bits = 12
fun main() {
    val lines = File("data/2021/d3.txt").readLines()

    var nums = lines.map { it.toInt(2) }
    val gamma = nums.makeMajorBitMap().reversed().fold(0) { acc, i -> (acc shl 1) or i.toInt() }

    val epsilon = gamma xor ((1 shl bits) - 1)
    val powerConsumption = gamma * epsilon
    println("Part 1: gamma: $gamma, epsilon: $epsilon, power consumption: $powerConsumption")
    assert(powerConsumption == 1092896)

    // part 2
    val ox = nums.findRating(false)
    val co2 = nums.findRating(true)
    val lifeSupportRating = ox * co2
    println("Part2: ox: $ox co2: $co2 life support rating: $lifeSupportRating")
    assert(lifeSupportRating == 4672151)
}

fun List<Int>.filterByBitAt(bit: Int, value: Boolean): List<Int> = this.filter { num -> num and (1 shl bit) == value.toInt() shl bit }
fun Boolean.toInt() = if(this) 1 else 0

fun List<Int>.makeMajorBitMap(): List<Boolean> =
    (0 until bits)
        .map { bit -> this.count { n -> (n and (1 shl bit) != 0) } }
        .map { oneCount -> oneCount >= this.size - oneCount }

fun List<Int>.findRating(inverse: Boolean): Int {
    var nums = this
    for(bit in bits-1 downTo 0) {
        val map = nums.makeMajorBitMap()
        val value = map[bit] xor inverse
        nums = nums.filterByBitAt(bit, value)
        if(nums.size == 1)
            return nums.first()
    }
    throw Exception()
}