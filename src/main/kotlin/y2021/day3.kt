package y2021.d3

import java.io.File

fun main() {
    val bits = 12
    val lines = File("data/2021/d3.txt").readLines()

    val bitMap = lines.map { it.toInt(2) }.makeMajorBitMap(bits)
    val gamma = bitMap.reversed().fold(0) { acc, i -> (acc shl 1) or i.toInt() }

    val epsilon = gamma.inv() and ((1 shl bits) - 1)
    val powerConsumption = gamma * epsilon
    println("Part 1: gamma: $gamma, epsilon: $epsilon, power consumption: $powerConsumption")
    assert(powerConsumption == 1092896)

    // part 2
    var nums = lines.map { it.toInt(2) }
    val ox = nums.findRating(bits, false)
    val co2 = nums.findRating(bits, true)
    val lifeSupportRating = ox * co2
    println("Part2: ox: $ox co2: $co2 life support rating: $lifeSupportRating")
    assert(lifeSupportRating == 4672151)
}

fun List<Int>.filterByBitAt(bit: Int, value: Int): List<Int> = this.filter { it and (1 shl bit) == value shl bit }
fun Boolean.toInt() = if(this) 1 else 0

fun List<Int>.makeMajorBitMap(bits: Int): List<Boolean> =
    (0 until bits).map { bit ->
        val ones = this.count { n -> (n and (1 shl bit) != 0) }
        val zeros = this.size - ones
        ones >= zeros
    }

fun List<Int>.findRating(bits: Int, inverse: Boolean): Int {
    var nums = this
    for(bit in bits-1 downTo 0) {
        val map = nums.makeMajorBitMap(bits)
        val value = (map[bit] xor inverse).toInt()
        nums = nums.filterByBitAt(bit, value)
        if(nums.size == 1)
            return nums.first()
    }
    throw Exception()
}