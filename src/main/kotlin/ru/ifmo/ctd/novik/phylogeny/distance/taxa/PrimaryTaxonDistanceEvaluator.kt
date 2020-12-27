package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Novik Dmitry ITMO University
 */
class PrimaryTaxonDistanceEvaluator : AbstractTaxonDistanceEvaluator() {
    override fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance = evaluate(lhs.genome.primary, rhs.genome.primary)
}