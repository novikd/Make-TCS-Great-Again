package ru.ifmo.ctd.novik.phylogeny.tree.merging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.metric.TCSMergeMetric

internal class EmptyMergingCandidateTest {

    @Test
    fun getDistance() {
        assertEquals(Int.MAX_VALUE, emptyMergingCandidate().distance)
    }

    @Test
    fun `First candidate must not exist`() {
        assertThrows(MergingException::class.java) {
            emptyMergingCandidate().firstCandidate
        }
    }

    @Test
    fun `Second candidate must not exist`() {
        assertThrows(MergingException::class.java) {
            emptyMergingCandidate().secondCandidate
        }
    }

    @Test
    fun `Can't merge empty candidate`() {
        assertThrows(MergingException::class.java) {
            val taxonDistanceEvaluator = SimpleTaxonDistanceEvaluator()
            emptyMergingCandidate().merge(
                    taxonDistanceEvaluator,
                    TCSMergeMetric(0, taxonDistanceEvaluator))
        }
    }
}