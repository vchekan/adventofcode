package y2023.d6

val test = """Time:      7  15   30
Distance:  9  40  200"""

val data = """Time:        59     79     65     75
Distance:   597   1234   1032   1328"""

fun main() {
    val times = parse(data)
    println(solve(times))
    println(solve(parse(data.replace(" ", ""))))
}

fun solve(records: List<Pair<Long,Long>>): Long =
    records.map { (time, record) ->
        (1..time).filter {charge ->
            val dist = charge * (time - charge)
            dist > record
        }.fold(0L) {acc, _ -> acc + 1 }
    }.reduce { acc, i -> acc * i }



fun parse(data: String): List<Pair<Long,Long>> =
    data.lines().take(2)
        .map { Regex("\\d+").findAll(it).map { it.value.toLong() } }
        .let { it[0].zip(it[1]) }.toList()
