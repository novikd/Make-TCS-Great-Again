package ru.ifmo.ctd.novik.phylogeny.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.utils.toMutableGenome

/**
 * @author Dmitry Novik ITMO University
 */
internal class GenomeWithOptionSetTest {

    @Test
    fun getGenomeOptions() {
        val genome = "TC".toMutableGenome()
        genome.add("CA")
        assertEquals(2, genome.size)
    }

    @Test
    fun getPrimary() {
        val genome = "TC".toMutableGenome()
        assertEquals("TC", genome.primary)
    }

    @Test
    fun isReal() {
        val genome = "AG".toMutableGenome()
        assertFalse(genome.isReal)
    }

    @Test
    fun process() {
        val genome = GenomeWithOptionSet()
        genome.addAll(listOf("AT", "TC", "GT", "AC"))

        val set = mutableSetOf<String>()
        genome.forEach {
            set.add(it)
        }
        assertEquals(4, set.size)
    }
}