package day23

import kotlin.system.measureNanoTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
fun main() {
    // 586439172
    val buf = "586439172".toCharArray().map { it.toByte() - 48 }
//    val idx = calcPart1(buf, 100)
//    println(idxIter(idx, 1).drop(1).joinToString(""))

//    val t = measureNanoTime {
//        val idx2 = calcPart2(buf, 1_000_000, 10_000_000)
//        val part2 = idx2[1].toLong() * idx2[idx2[1]].toLong()
//        println("part2: $part2")
//    }
//    println("time: ${t.toDuration(DurationUnit.NANOSECONDS)}")
    
//    val one = calc(buf, 100)
//    val part1 = one.iter().drop(1).map { it.label!!.toString() }.joinToString("")
//    println("part1: $part1")

    val t = measureNanoTime {
        val max = buf.maxOrNull()!!
        val buf2 = buf + (max + 1..1_000_000)
        val one2 = calc(buf2, 10_000_000)
        val part2 = one2.next!!.label.toLong() * one2.next!!.next!!.label.toLong()
        println("part2: $part2")
    }
    println("time: ${t.toDuration(DurationUnit.NANOSECONDS)}")
}

fun calc(buf: List<Int>, count: Int): Node {
    var label2node = mutableMapOf<Int,Node>()
    val last = buf.fold(Node(buf.last(), null)) { prev, n ->
        val node = Node(n, null)
        prev.next = node
        label2node[n] = node
        node
    }
    var head = label2node[buf.first()]!!
    last.connect_next(head)

    var current = head
    for(i in 1..count) {
        val n1 = current.next!!
        val n2 = n1.next!!
        val n3 = n2.next!!
        var destination = current
        do {
            var label = destination.label - 1
            if (label == 0)
                label = buf.size
            destination = label2node[label]!!
        } while(destination === n1 || destination === n2 || destination === n3)

        val spanStart = current.next!!
        val spanEnd = current.next!!.next!!.next!!
        val tail = destination.next!!
        val gap = spanEnd.next!!

        current.connect_next(gap)
        destination.connect_next(spanStart)
        spanEnd.connect_next(tail)

        current = current.next!!
    }

    return label2node[1]!!
}

fun calcPart1(data: List<Int>, count: Int): List<Int> {
    var idx = MutableList(data.size + 1) { 0 }
    for(label in idx.indices.drop(1)) {
        var pos = data.indexOf(label)
        val next = data[(pos + 1) % data.size]
        idx[label] = next
    }
    return calcIndex(idx, data[0], count)
}

fun calcPart2(data: List<Int>, size: Int, runs: Int): List<Int> {
    var idx = MutableList(size + 1) { it + 1 }
    for(label in 1..data.size) {
        var pos = data.indexOf(label)
        val next = data[(pos + 1) % data.size]
        idx[label] = next
    }
    idx[idx.size-1] = data[0]
    idx[data.last()] = data.maxOrNull()!! + 1
    return calcIndex(idx, data[0], runs)
}

fun calcIndex(/*data: List<Int>*/ idx: MutableList<Int>, startLabel: Int, runs: Int): List<Int> {
//    var idx = MutableList(data.size + 1) { 0 }
//    for(label in idx.indices.drop(1)) {
//        var pos = data.indexOf(label)
//        val next = data[(pos + 1) % data.size]
//        idx[label] = next
//    }

    var currentLabel = startLabel // data[0]
    for(i in 1..runs) {
//        println("-- move $i --")
//        println("cups: ${idxIter(idx, currentLabel).joinToString(" ")}")
        val n1 = idx[currentLabel]
        val n2 = idx[n1]
        val n3 = idx[n2]
//        println("pick up $n1 $n2 $n3")
        var destination = currentLabel
        do {
//            var label = destination - 1
//            if (label == 0)
//                label = data.size
//            println(">$label destination: $destination")
            //destination = idx[label]
            destination -= 1
            if(destination == 0)
                destination = idx.size - 1 //data.size
        } while(destination == n1 || destination == n2 || destination == n3)
//        println("destination: $destination")

        val spanStart = idx[currentLabel]
        val spanEnd = idx[idx[spanStart]]
        val tail = idx[destination]
        val gap = idx[spanEnd]

//        current.connect_next(gap)
//        destination.connect_next(spanStart)
//        spanEnd.connect_next(tail)
        idx[currentLabel] = gap
        idx[destination] = spanStart
        idx[spanEnd] = tail

//        println("currentLabel: $currentLabel -> ${idx[currentLabel]}")
        currentLabel = idx[currentLabel]
//        println()
    }

//    println(idxIter(idx, 1).drop(1).joinToString(""))
    return idx
}

class Node(
    val label: Int,
    var next: Node?,
) {
    inline fun connect_next(other: Node) {
        this.next = other
    }

    fun iter() : Sequence<Node> {
        var start = this
        return sequence {
            var node = start
            do {
                yield(node)
                node = node.next!!
            } while(node != start)
        }
    }

    fun toStringAll(): String {
        return iter().map { if(it == this) "(${it.label})" else "${it.label}" }.joinToString(" ")
    }

    override fun toString(): String {
        return this.label.toString()
    }
}

fun idxIter(idx: List<Int>, current: Int): Sequence<Int> {
    var ptr = current
    return sequence {
        for (i in 1 until idx.size) {
            yield(ptr)
            ptr = idx[ptr]
        }
    }
}