package ru.ifmo.ctd.novik.phylogeny.common

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome
import ru.ifmo.ctd.novik.phylogeny.utils.toMutableGenome

/**
 * @author Dmitry Novik ITMO University
 */
internal class GenomeTest {

    @Test
    fun isReal() {
        val genome = "AT".toGenome()
        assertTrue(genome.isReal)
    }

    @Test
    fun process() {
        val genome = "GC".toGenome()
        var count = 0
        var result = false

        genome.forEach {
            count++
            result = result || (it == "GC")
        }
        assertEquals(1, count)
        assertTrue(result)
    }

    @Test
    fun testEquals() {
        val a = "AT".toGenome()
        val b = "AT".toGenome()
        val c = "AT".toMutableGenome()

        assertTrue(a == b)
        assertTrue(a.equals(c))
    }
}