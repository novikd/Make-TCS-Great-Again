package ru.ifmo.ctd.novik.phylogeny.tree.merging

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingCandidate

internal class EmptyMergingCandidateTest {

    @Test
    fun `First candidate must not exist`() {
        assertThrows(MergingException::class.java) {
            emptyMergingCandidate().firstCluster
        }
    }

    @Test
    fun `Second candidate must not exist`() {
        assertThrows(MergingException::class.java) {
            emptyMergingCandidate().secondCluster
        }
    }

    @Test
    fun `Can't merge empty candidate`() {
        assertThrows(MergingException::class.java) {
            emptyMergingCandidate().merge()
        }
    }
}