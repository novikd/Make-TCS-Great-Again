package ru.ifmo.ctd.novik.phylogeny.io.input

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.common.ObservedTaxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome

/**
 * @author Dmitry Novik ITMO University
 */
internal class SimpleInputTaxaReaderTest {
    private val sampleFile = "samples/sample01.txt"
    private val expectedTaxonList = listOf("AA", "AG", "AC")
        .mapIndexed { id, genome -> ObservedTaxon(id, name = "taxon$id", genome = genome.toGenome()) }

    @Test
    fun `readFile on sample01`() {
        val reader = SimpleInputTaxaReader()
        val taxonList = reader.readFile(sampleFile)

        assertEquals(3, taxonList.size)
        assertIterableEquals(expectedTaxonList, taxonList)
    }
}