package y2021.d16

import y2021.d3.toInt
import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val line =
//        "38006F45291200"
        File("data/2021/d16.txt").readLines()[0]

    var versionSum = 0
    val eval = Eval(line) {versionSum += it}

    println(versionSum)
    println(eval.result)
    assert(versionSum == 920)
    assert(eval.result == 10185143721112)
}

class Eval(line: String, private val onVersion: (Int) -> Unit) {
    private var offset = 0
    private val bits = line.toBits()
    val result: Long = parsePacket()

    companion object {
        private fun String.toBits(): BooleanArray {
            val ret = BooleanArray(this.length * 4)
            this.map { if (it.isDigit()) it - '0' else it - 'A' + 10 }
                .forEachIndexed { index, i ->
                    ret[index * 4] = (i and 0b1000) != 0
                    ret[index * 4 + 1] = (i and 0b0100) != 0
                    ret[index * 4 + 2] = (i and 0b0010) != 0
                    ret[index * 4 + 3] = (i and 0b0001) != 0
                }
            return ret
        }
    }

    private fun parsePacket(): Long {
        val version = readIntegral(3)
        onVersion(version)

        val type = readIntegral(3)
        println("version: $version type: $type")

        val eval = {a: Long, b: Long -> when(type) {
            0 -> a + b
            1 -> a * b
            2 -> min(a, b)
            3 -> max(a, b)
            5 -> if(a > b) 1 else 0
            6 -> if(a < b) 1 else 0
            7 -> if(a == b) 1 else 0
            else -> throw Exception("Unknown operator: $type")
        }}

        return when(type) {
            4 -> bits.parseLiteral()
            else -> parseOperator().reduce(eval)
        }
    }

    private fun BooleanArray.parseLiteral(): Long {
        var res = 0L
        var hasMore: Boolean
        do {
            hasMore = this[offset++]
            res = (res shl 4) or readIntegral(4).toLong()
        } while(hasMore)
        println("literal: $res")

        return res
    }

    private fun parseOperator(): List<Long> {
        val values = mutableListOf<Long>()
        when(bits[offset++]) {
            // bit length based subpackets
            false -> {
                val subpacketsLength = readIntegral(15)
                println("subpackets len: $subpacketsLength bits")
                val end = offset + subpacketsLength
                while(offset < end) {
                    values.add(parsePacket())
                }
            }
            // Count-based subpackets
            true -> {
                val subpacketCount = readIntegral(11)
                println("subpacket count: $subpacketCount")
                repeat(subpacketCount) {
                    values.add(parsePacket())
                }
            }
        }

        return values
    }

    private fun readIntegral(count: Int): Int {
        var n = 0
        for(i in offset until offset+count) {
            n = (n shl 1) or bits[i].toInt()
        }
        offset += count
        return n
    }
}