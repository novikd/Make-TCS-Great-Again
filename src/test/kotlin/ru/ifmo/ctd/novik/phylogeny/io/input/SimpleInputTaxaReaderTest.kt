package ru.ifmo.ctd.novik.phylogeny.io.input

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome

/**
 * @author Novik Dmitry ITMO University
 */
internal class SimpleInputTaxaReaderTest {
    private val sampleFile = "samples/sample01.txt"
    private val expectedTaxonList = listOf("AA", "AG", "AC")
        .mapIndexed { id, genome -> Taxon(id, name = "taxon$id", genome = genome.toGenome()) }

    @Test
    fun `readFile on sample01`() {
        val reader = SimpleInputTaxaReader()
        val taxonList = reader.readFile(sampleFile)

        assertEquals(3, taxonList.size)
        assertIterableEquals(expectedTaxonList, taxonList)
    }
}