import java.io.File
import java.util.*

val rx = Regex("(.*)bags contain ((\\d+.+) bag(s?)[,.])+")

fun main() {
    val lines = File("data/day7.txt").readLines()

    val child2parent = lines.map {line ->
        val(bag, bagsAll) = line.split(" bags contain ")
        val bags = bagsAll.split(Regex(" bag(s?)[,.]( ?)")).filter { it.isNotEmpty() }
            .map {
                if(it == "no other")
                    null
                else {
                    val num: Int = Regex("^\\d+").find(it)?.value?.toInt()!!
                    val child: String = Regex("\\d+ (.+)").find(it)?.groupValues?.get(1)!!
                    Pair(child, Pair(bag, num))
                }
            }.filterNotNull()
        bags
    }.flatten()

    val child2parentMap: Map<String,List<Pair<String,Int>>> = child2parent.groupBy({pair -> pair.first}, {pair -> pair.second})

    var stack = Stack<String>()
//    val visited = mutableSetOf<String>()
//    stack.push("shiny gold")
//    while(stack.isNotEmpty()) {
//        val node = stack.pop()
//        visited.add(node)
//        child2parentMap[node]?.forEach{ stack.push(it.first)}
//    }
//
//    println("Part 1: ${visited.size - 1}")

    //
    // Part 2
    //
    val parent2childMap = child2parent.groupBy( { pair -> pair.second.first}, {pair -> Pair(pair.first, pair.second.second)})
    val stack2 = Stack<Pair<String,Int>>()
    var sum = 0
    stack2.push(Pair("shiny gold", 1))
    while(stack2.isNotEmpty()) {
        val (node, count) = stack2.pop()
        sum += count
        val children = parent2childMap[node]
        children?.forEach {stack2.push(Pair(it.first, count*it.second))}
    }

    println(sum-1)
}