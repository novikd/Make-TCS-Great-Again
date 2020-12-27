package ru.ifmo.ctd.novik.phylogeny.io.input

import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.utils.toTaxa
import java.io.File

/**
 * @author Dmitry Novik ITMO University
 */
internal class FastaInputTaxaReaderTest {
    val testData = ">taxon0\n" +
            "AA\n" +
            ">taxon1\n" +
            "AG\n" +
            ">taxon2\n" +
            "AC"
    private val expectedTaxonList = listOf("AA", "AG", "AC").toTaxa()

    @Test
    fun readFile() {
        val file = File("samples/temp")
        file.writeText(testData)

        val taxonList = FastaInputTaxaReader().readFile("samples/temp")
        file.delete()

        assertIterableEquals(expectedTaxonList, taxonList)
    }
}