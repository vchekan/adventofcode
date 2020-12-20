import java.io.File

fun main() {
    val ids = File("data/day5.txt")
        .readLines()
        .map { decode(it) }

    println("Solution 1: ${ids.maxOrNull()}")

    val sort = ids.sorted()
    val r = sort.zip(sort.drop(1)).find { (a,b) -> a + 2 == b }
    println("Solution 2: ${r!!.first + 1}")
}

fun decode(code: String) : Int {
    val bin = code
        .replace('F', '0')
        .replace('B', '1')
        .replace('R', '1')
        .replace('L', '0')
    return bin.toInt(2)
}