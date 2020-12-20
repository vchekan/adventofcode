import java.io.File

fun main() {
    val plan = File("data/day3.txt").bufferedReader().readLines()

    println(
        find(plan, 1,1)*
        find(plan, 3, 1)*
        find(plan, 5,1)*
        find(plan,7,1)*
        find(plan, 1,2)
    )
}

fun find(plan: List<String>, dx: Int, dy: Int): Int {
    var x = 0
    var y = 0
    var count = 0

    while(y<plan.size) {
        if(plan[y][x] == '#')
            count += 1

        x = (x + dx) % plan[0].length
        y+=dy
    }

    return count
}