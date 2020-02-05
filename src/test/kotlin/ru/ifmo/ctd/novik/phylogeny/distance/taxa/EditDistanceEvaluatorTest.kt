package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome

/**
 * @author Dmitry ITMO University
 */
internal class EditDistanceEvaluatorTest {

    @Test
    fun `equal taxa`() {
        val first = Taxon(0, genome = "ACGT".toGenome())
        val second = Taxon(1, genome = "ACGT".toGenome())
        val evaluator: TaxonDistanceEvaluator = EditDistanceEvaluator()
        assertEquals(0, evaluator.evaluate(first, second))
    }

    @Test
    fun `simple substitution in taxon`() {
        val first = Taxon(0, genome = "AGGT".toGenome())
        val second = Taxon(1, genome = "ACGT".toGenome())
        val evaluator: TaxonDistanceEvaluator = EditDistanceEvaluator()
        assertEquals(1, evaluator.evaluate(first, second))
    }

}