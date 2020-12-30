package ru.ifmo.ctd.novik.phylogeny.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.utils.toGenomeOption
import ru.ifmo.ctd.novik.phylogeny.utils.toMutableGenome

/**
 * @author Dmitry Novik ITMO University
 */
internal class GenomeWithOptionSetTest {

    @Test
    fun getGenomeOptions() {
        val genome = "TC".toMutableGenome()
        genome.add("CA".toGenomeOption())
        assertEquals(2, genome.size)
    }

    @Test
    fun getPrimary() {
        val genome = "TC".toMutableGenome()
        assertEquals("TC", genome.primary.toString())
        assertEquals("TC".toGenomeOption(), genome.primary)
    }

    @Test
    fun process() {
        val genome = GenomeWithOptionSet()
        genome.addAll(listOf("AT", "TC", "GT", "AC").map(String::toGenomeOption))

        val set = mutableSetOf<String>()
        genome.forEach {
            set.add(it.toString())
        }
        assertEquals(4, set.size)
    }
}