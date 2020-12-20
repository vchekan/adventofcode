import java.io.File

fun main() {
    val plan = File("data/day4.txt").readText().trim().split("\n\n")
    println(plan.count { isValid(it) })
}

fun isValid(lines: String): Boolean {
    val fields = setOf(
            "byr",
            "iyr",
            "eyr",
            "hgt",
            "hcl",
            "ecl",
            "pid")

    val kv = kv(lines)
    if((fields - kv.map { it[0] }.toSet()).isNotEmpty())
        return false

    return kv.all {
        when(it[0]) {
            "byr" -> it[1].toInt() in 1920..2002
            "iyr" -> it[1].toInt() in 2010..2020
            "eyr" -> it[1].toInt() in 2020..2030
            "hgt" -> if(it[1].endsWith("cm"))
                    it[1].substring(0, it[1].length-2).toInt() in 150..193
                else if(it[1].endsWith("in"))
                    it[1].substring(0, it[1].length-2).toInt() in 59..76
                else
                    false
            "hcl" ->
                Regex("#[0-9a-f]{6}").matches(it[1])
            "ecl" ->
                setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(it[1])
            "pid" ->
                Regex("\\d{9}").matches(it[1])
            "cid" -> true
            else -> false
        }
    }

}

fun kv(lines: String): List<List<String>> {
    return lines.split(' ', '\n')
        .map { it.split(':') }
}
