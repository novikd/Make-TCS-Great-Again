package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Genome
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance

/**
 * @author Dmitry Novik ITMO University
 */
class PrimaryTaxonDistanceEvaluator : AbstractTaxonDistanceEvaluator() {
    override fun evaluate(lhs: Genome, rhs: Genome): TaxonDistance {
        val lhsPrimary = lhs.primary
        val rhsPrimary = rhs.primary
        return TaxonDistance(hammingDistance(lhsPrimary, rhsPrimary), listOf(lhsPrimary to rhsPrimary))
    }
}