package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Dmitry Novik ITMO University
 */
class AbsoluteTaxonDistanceEvaluator : AbstractTaxonDistanceEvaluator() {
    override fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance {
        return lhs.genome.flatMap {
            val first = it
            rhs.genome.map {
                val second = it
                evaluate(first, second)
            }
        }.minWith(naturalOrder())!!
    }
}