import java.io.File

fun main() {
    val passwords = File("data/day2.txt").bufferedReader().lines().map {
        it.parse()
    }
        .filter {it.isValid2()}
        .count()

    println(passwords)
}

data class Password(
    val low: Int,
    val high: Int,
    val char: Char,
    val pwd: String
) {
    fun isValid() : Boolean {
        return pwd.count { it == this.char } in low..high
    }

    fun isValid2() : Boolean {
        return (pwd[low-1] == char) xor (pwd[high-1] == char)
    }
}

fun String.parse() : Password {
    val parts = this.split(" ")
    val range = parts[0].split("-")
    return Password(low = range[0].toInt(), high = range[1].toInt(), char = parts[1][0], pwd = parts[2])
}
