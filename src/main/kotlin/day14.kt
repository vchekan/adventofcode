package day14

import java.io.File
import java.lang.Exception

fun main() {
    val program = File("data/day14.txt").readLines()
    part1(program)
    part2(program)
}

fun part1(program: List<String>) {
    val comp = Computer()
    for(cmd in program)
        comp.exec(cmd)

    println("part1: ${comp.mem.values.sum()}")
}

fun part2(program: List<String>) {
    val comp = Computer()
    for(cmd in program)
        comp.execV2(cmd)

    println("part2: ${comp.mem.values.sum()}")
}

class Computer {
    var orMask: Long = Long.MAX_VALUE
    var andMask: Long = 0
    var mask: String = ""
    val mem = mutableMapOf<Long,Long>()

    fun exec(cmd: String) {
        if(cmd.startsWith("mask = ")) {
            orMask = 0
            andMask = Long.MAX_VALUE
            val mask = cmd.substring(7)
            for(i in mask.indices) {
                val i2 = mask.length - 1 - i
                when(mask[i]) {
                    '0' -> andMask = andMask and (1L shl i2).inv()
                    '1' -> orMask = orMask or (1L shl i2)
                    'X' -> { /* no-op */ }
                    else -> throw Exception("Unknown mask")
                }
            }
        } else if(cmd.startsWith("mem[")) {
            val (_, addr, value) = Regex("mem\\[(\\d+)\\] = (\\d+)").find(cmd)?.groupValues!!
            val valueMasked = (value.toLong() and andMask) or orMask
            mem[addr.toLong()] = valueMasked
        }
    }

    fun execV2(cmd: String) {
        if(cmd.startsWith("mask = ")) {
            mask = cmd.substring(7)
        } else if(cmd.startsWith("mem[")) {
            val (_, addrStr, valueStr) = Regex("mem\\[(\\d+)\\] = (\\d+)").find(cmd)?.groupValues!!
            var addr = addrStr.toLong()
            val value = valueStr.toLong()

            // set fixed parts of the address ('1' only)
            for(i in mask.indices) {
                val i2 = mask.length - 1 - i
                if(mask[i] == '1') {
                    addr = addr or (1L shl i2)
                }
            }

            //
            // The main idea is to get permutations of all floating bits in their "compact representation.
            // I build from mask=0X00X0X a binary mask bits=00000111 (3 bits set, one for every X in mask)
            // and get all permutations as simple as `for(i in 0..bits)`. Now I just need to map i-th bit of "compact representation into
            // appropriate bit in "mask".
            //

            // Indexes in addr, which are floating
            val xmap = mask.withIndex().filter { it.value == 'X' }.map { mask.length - 1 - it.index }
            assert(xmap.isNotEmpty())
            // Compact representation of all floating bits
            var bits = (1L shl xmap.size) - 1
            // i: running mask to be overridden
            for(i in 0..bits) {
                // set every bit from mask into addr
                var bitPtr = 1L
                // bitPtr: running bit in compact representation to be overridden
                for(xPosition in xmap) {
                    val overrideBit = (i and bitPtr) != 0L
                    addr = setBit(addr, xPosition, overrideBit)
                    bitPtr = bitPtr shl 1
                }
                mem[addr] = value
            }
        }
    }

}

inline fun setBit(v: Long, index: Int, bitValue: Boolean) : Long {
    if(!bitValue) {
        return (1L shl index).inv() and v
    }
    return (1L shl index) or v
}