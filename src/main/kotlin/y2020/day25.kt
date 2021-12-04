package `2020`

fun main() {
    val card = 7573546L
    val door = 17786549L
//    val card = 5764801L
//    val door = 17807724L

    val cLoop = crack(card)
//    val dLoop = crack(door)
    val key = encrypt(door, cLoop)
    println(key)

//    println(crack(5764801))
//    println(crack(17807724))
//    println(encrypt(7, 11))
//    println(exp(2,6, 512))
}

fun encrypt(subject: Long, loopSize: Long): Long {
    var value = 1L
    for(i in 1..loopSize) {
        value = value * subject % 20201227
    }
    return value
    return exp(subject, loopSize, 20201227)
}

fun crack(n: Long): Long {
    var loopSize = 1L
    while(true) {
        val c2 = encrypt(7, loopSize)
        if(c2 == n)
            return loopSize
        loopSize += 1
    }
}

fun exp(n: Long, p: Long, m: Long) : Long {
    var pp = p
    var nn = n
    var res = 1L
    while(pp != 0L) {
        if(pp and 1 != 0L)
            res = res * nn % m
        nn = nn * nn % m
        pp = pp shr 1
    }
    return res
}