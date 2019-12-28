package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Dmitry ITMO University
 */
internal class EditDistanceEvaluatorTest {

    @Test
    fun `equal taxa`() {
        val first = Taxon(0, genome = "ACGT")
        val second = Taxon(1, genome = "ACGT")
        val evaluator: TaxonDistanceEvaluator = EditDistanceEvaluator()
        assertEquals(0, evaluator.evaluate(first, second))
    }

    @Test
    fun `simple substitution in taxon`() {
        val first = Taxon(0, genome = "AGGT")
        val second = Taxon(1, genome = "ACGT")
        val evaluator: TaxonDistanceEvaluator = EditDistanceEvaluator()
        assertEquals(1, evaluator.evaluate(first, second))
    }

}