import java.io.File

val target = 2020

fun main() {
    val nums = File("data/day1.txt")
        .readLines()
        .map(String::toInt)
        .sorted()
    println(nums.size)

    println("find2: ${find2(nums)}")

    val hash = nums.mapIndexed { i, n -> Pair(n, i) }.toMap()
    for(n in nums.withIndex()) {
        val compliment = target - n.value
        val idx = hash[compliment]
        if(idx != null) {
            println("Found ${n.value * compliment}")
            break
        }
    }

    println("Result: ${find(nums, 0, nums.size-1)}")

    for((i,n) in nums.withIndex()) {
        for(i2 in i+1 until nums.size) {
            val compliment = target - n - nums[i2]
            val idx = hash[compliment]
            if(idx != null) {
                println("Found 3-sum ${n * compliment * nums[i2]} at $i, $i2, $idx")
                break
            }
        }
    }

}

fun find2(nums: List<Int>): Int {
    var p1 = 0
    var p2 = nums.size-1
    while(p1 < p2) {
        val sum = nums[p1] + nums[p2]
        if(sum == target)
            return nums[p1] * nums[p2]
        if(sum < target)
            p1 += 1
        else
            p2 -= 1
    }

    return -1
}

fun find(nums: List<Int>, p1: Int, p2: Int) : Int {
    if(p1 == p2)
        return -1

    val sum = nums[p1] + nums[p2]
    if(sum == target)
        return nums[p1] * nums[p2]

    if(sum < target) {
        val sum1 = find(nums, p1+1, p2)
        if(sum1 > 0)
            return sum1
    }

    if(sum > target) {
        val sum1 = find(nums, p1, p2 -1)
        if(sum1 > 0)
            return sum1
    }

    return -1
}

