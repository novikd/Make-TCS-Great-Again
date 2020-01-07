package ru.ifmo.ctd.novik.phylogeny.utils

/**
 * @author Dmitry Novik ITMO University
 */

fun permutations(n: Int): Sequence<Array<Int>> = generateSequence(Array<Int>(n) { it }) {
    val next = it.copyOf()
    fun swap(i: Int, j: Int) {
        val temp = next[i]
        next[i] = next[j]
        next[j] = temp
    }

    var position = -1
    for (i in 0 until next.size - 1) {
        val pos = next.size - 1 - i
        if (next[pos - 1] < next[pos]) {
            position = pos - 1
            break
        }
    }

    if (position == -1)
        null
    else {
        for (i in next.indices) {
            val pos = next.size - 1 - i
            if (next[pos] > next[position]) {
                swap(position, pos)
                break
            }
        }
        var i = position + 1
        var j = next.size - 1
        while (i < j) {
            swap(i, j)
            ++i
            --j
        }
        next
    }
}