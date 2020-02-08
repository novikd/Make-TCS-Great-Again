package ru.ifmo.ctd.novik.phylogeny.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.utils.toMutableGenome

/**
 * @author Dmitry Novik ITMO University
 */
internal class MutableGenomeTest {

    @Test
    fun getGenomeOptions() {
        val genome = "TC".toMutableGenome()
        genome.genomeOptions.add("CA")
        assertEquals(2, genome.genomeOptions.size)
    }

    @Test
    fun getPrimary() {
        val genome = "TC".toMutableGenome()
        assertEquals("TC", genome.primary)
    }

    @Test
    fun isReal() {
        val genome = "AG".toMutableGenome()
        assertFalse(genome.isReal())
    }

    @Test
    fun process() {
        val genome = MutableGenome()
        genome.genomeOptions.addAll(listOf("AT", "TC", "GT", "AC"))

        val set = mutableSetOf<String>()
        genome.process {
            set.add(this)
        }
        assertEquals(4, set.size)
    }
}