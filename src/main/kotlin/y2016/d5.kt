package aoc.area.y2016

import java.security.MessageDigest

val input = "abbhdwsy"

fun main() {
    val md5 = MessageDigest.getInstance("MD5")
    val zeros = generateSequence(0) { it + 1 }.map {
        md5.reset()
        md5.digest("$input$it".toByteArray())
    }.filter {
        it[0] == 0.toByte() && it[1] == 0.toByte() && it[2].toUByte() < 0x10.toUByte()
    }

    val pwd1 = zeros.take(8).map { (it[2].toUByte().toUInt() and 0x0F.toUInt()).toString(16) }
    println(pwd1.joinToString(""))

    var count = 8
    val pwd2 = Array(8) { ' ' }
    for(n in zeros) {
        val index = n[2].toUByte().toInt() and 0x0F
        if(index > 7)
            continue
        if(pwd2[index] == ' ') {
            pwd2[index] = n[3].toUByte().toUInt().shr(4).toString(16)[0]
            if(--count == 0)
                break
        }
    }
    println(pwd2.joinToString(""))
}
