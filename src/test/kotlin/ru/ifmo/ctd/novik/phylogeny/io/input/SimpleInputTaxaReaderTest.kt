package ru.ifmo.ctd.novik.phylogeny.io.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.utils.toTaxa

/**
 * @author Dmitry Novik ITMO University
 */
internal class SimpleInputTaxaReaderTest {
    private val sampleFile = "samples/sample01.txt"
    private val expectedTaxonList = listOf("AA", "AG", "AC").toTaxa()

    @Test
    fun `readFile on sample01`() {
        val reader = SimpleInputTaxaReader()
        val taxonList = reader.readFile(sampleFile)

        assertEquals(3, taxonList.size)
        assertIterableEquals(expectedTaxonList, taxonList)
    }
}