package `2020`

import java.io.File

fun main() {
    val allergyToIngidientsMap = mutableMapOf<String,MutableSet<String>>()
    val allProducts = File("data/day21.txt").readLines().map { line ->
        val (_, prodsStr, allergiesStr) = Regex("((?:\\w+ ?)+) \\(contains ([\\w+, ]+)\\)").find(line)!!.groupValues
        val prods = prodsStr.split(" ").toMutableSet()
        val allergies = allergiesStr.split(", ").toSet()
        Pair(allergies, prods)
    }



    for((allergies, prods) in allProducts) {
        for (alg in allergies) {
            allergyToIngidientsMap.merge(alg, prods) { p1, p2 -> p1.intersect(p2).toMutableSet() }
        }
    }
    val prodsWithAllergy = allergyToIngidientsMap.values.flatten().toSet()
    val part1 = allProducts.map { it.second }.flatten().filter { p -> !prodsWithAllergy.contains(p) }.count()

    println("part1: $part1")

    // If given allergen is in single product, then remove the product from being suspect for other allergens
    var changed: Boolean
    do {
        changed = false
        for(singleEntry in allergyToIngidientsMap.entries.filter { it.value.size == 1 }) {
            val single = singleEntry.value.first()
            for(otherEntry in allergyToIngidientsMap.filter { it != singleEntry }) {
                var other = otherEntry.value
                if(other.contains(single)) {
                    other.remove(single)
                    changed = true
                }
            }
        }
    } while (changed)

    // qhvz,kbcpn,fzsl,mjzrj,bmj,mksmf,gptv,kgkrhg
    println("part2: ${allergyToIngidientsMap.entries.sortedBy { it.key }.map { it.value.first() }.joinToString(",")}")
}