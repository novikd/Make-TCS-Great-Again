package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Dmitry Novik ITMO University
 */
interface TaxonDistanceEvaluator {
    fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance
}