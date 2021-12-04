package `2020`

import java.io.File
import java.math.BigInteger

/**
 *
a: 5 b: 9: delta: 0
ka | t/t1
-------
9 45/45
18 90/90
27 135/135
36 180/180
45 225/225
54 270/270
63 315/315
a: 5 b: 9: delta: 1
ka | t/t1
-------
7 35/36
16 80/81
25 125/126
34 170/171
43 215/216
52 260/261
61 305/306

Step: 495
a: 5 b: 9 c: 11
ka | t/t1
-------
70 350/351/352
169 845/846/847
268 1340/1341/1342
367 1835/1836/1837
466 2330/2331/2332
565 2825/2826/2827

 */
fun main() {
    val lines = File("data/day13.1.txt").readLines()
    val ts = lines[0].toInt()
    val buses = lines[1].split(',').filter { it != "x" }.map { it.toInt() }

    part1(ts, buses)
    part2()
//    test2(5,9)
//    test3(5, 9, 11)
}

fun test3(a: Int, b: Int, c: Int) {
    println("a: $a b: $b c: $c")
    println("ka | t/t1")
    println("-------")
    var aa = a
    var bb = b
    var cc = c
    for(i in 1..1000) {
        if(aa + 1 == bb && aa + 2 == cc) {
            println("${aa / a} $aa/$bb/$cc")
            aa += a
            bb += b
            cc += c
        }
        val min = arrayOf(aa,bb,cc).minOrNull()!!
        if(aa == min)
            aa += a
        if(bb == min)
            bb += b
        if(cc == min)
            cc += c
    }
}

fun test2(a: Int, b: Int) {
    println("a: $a b: $b")
    println("ka | t/t1")
    println("-------")
    var aa = a
    var bb = b
    for(i in 1..1000) {
        if(aa + 1 == bb) {
            println("${aa / a} $aa/$bb")
            aa += a
            bb += b
        }
        val min = arrayOf(aa,bb).minOrNull()!!
        if(aa == min)
            aa += a
        if(bb == min)
            bb += b
    }
}


fun part1(ts: Int, buses: List<Int>) {
    val waits = buses.map { bus ->
        val wait = bus - (ts % bus)
        Pair(bus, wait)
    }

    val min = waits.minByOrNull { it.second }!!
    println(min.first*min.second)
}

data class State(val delay: BigInteger, val bus: BigInteger)

fun part2() {
    val lines = File("data/day13.txt").readLines()
    // sort to start with biggest values and start with steps of max size
    val buses = lines[1].split(',').withIndex().filter { it.value != "x" }
        .map { State(it.index.toBigInteger(), it.value.toBigInteger()) }
        .sortedByDescending { it.bus }

    //
    var delay = buses[0].delay
    var t = buses[0].bus - delay
    var step = buses[0].bus
    var i = 1
    var counter = 0
    println("step $step -> t:$t, ${buses[0]}")
    while(i < buses.size) {
        counter += 1
        if((t + buses[i].delay) % buses[i].bus == BigInteger.ZERO) {
            println("step $step -> t:$t, ${buses[i]}")
            step *= buses[i].bus
            i += 1
            if(i == buses.size)
                println("part2: $t")
            // re-try the same step with the next bus, it might match
            continue
        }
        t += step
    }

    println("counter: $counter")
}

