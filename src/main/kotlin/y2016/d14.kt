package aoc.area.y2016

import java.util.HexFormat

fun main() {
//    keys("abc").withIndex()
//        .map { it.index to it.value.repeats3and5() }
//        .filter {(i, key) ->
//            val key5 = key shr 16
//            if(key5 == 0u)
//                return@filter false
//
//            for(j in i-1000 until i) {
//
//            }
//
//            key5 != 0u
//
//        }
//        .filter { (i, key5) ->
//
//        }


    var seen = ArrayDeque<Pair<Int,UInt>>()
    val part1 = keys("abc").withIndex()
        .map { Triple(it.index, it.value.repeats3and5(), it.value) }
        .filter { it.second != 0u }
        .filter { (i, key, str) ->
            val mask3 = key and 0xFFFFu
            val mask5 = key shr 16

//            if(mask3 != 0u)
//                println(">>3: $i $str")
//            if(mask5 != 0u)
//                println(">>5: $i $str")

            // leave last 1000
            while(seen.isNotEmpty() && i - seen.first().first > 1000)
                seen.removeFirst()

            if(seen.any {
                if(it.second and mask5 != 0u) {
                    println("found $it")
                    true
                } else {
                    false
                }
            })
                return@filter true

            seen.addLast(i to mask3)

            false
        }.elementAt(63).first
    println(part1)
}

fun keys(seed: String): Sequence<String> = sequence {
    var i = 0
    val md5 = java.security.MessageDigest.getInstance("MD5")
    while (true) {
        val key = HexFormat.of().formatHex(md5.digest("$seed$i".toByteArray()))
        yield(key)
        i++
    }
}

/** key5 map, key3 map */
fun String.repeats3and5(): UInt {
    var res = 0u
    val counts = Array(16) {0}
    for(i in 1 until  this.length) {
        val n = this[i].digitToInt(16)
        if(this[i] == this[i-1])
            counts[n]++
        else
            counts[n] = 0

        if(counts[n] == 2)
            res = res or 1u.shl(n)
        if(counts[n] == 4)
            res = res or 1u.shl(16 + n)
    }

    return res
}