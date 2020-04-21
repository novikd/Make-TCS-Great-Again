package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance

/**
 * @author Dmitry Novik ITMO University
 */
class AbsoluteTaxonDistanceEvaluator : AbstractTaxonDistanceEvaluator() {
    override fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance {
        var currentDistance = Int.MAX_VALUE
        val genomePairs = mutableListOf<Pair<String, String>>()
        //TODO: compute it in parallel
        lhs.genome.forEach { lhsGenome ->
            rhs.genome.forEach { rhsGenome ->
                val distance = hammingDistance(lhsGenome, rhsGenome)
                if (distance < currentDistance) {
                    currentDistance = distance
                    genomePairs.clear()
                    genomePairs.add(Pair(lhsGenome, rhsGenome))
                } else if (distance == currentDistance) {
                    genomePairs.add(Pair(lhsGenome, rhsGenome))
                }
            }
        }
        return TaxonDistance(currentDistance, genomePairs)
    }
}