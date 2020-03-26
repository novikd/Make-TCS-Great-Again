package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Novik Dmitry ITMO University
 */
class CachingTaxonDistanceEvaluator<T : TaxonDistanceEvaluator>(
    private val nestedEvaluator: T
) : TaxonDistanceEvaluator {
    private val cache = mutableMapOf<Pair<Taxon, Taxon>, TaxonDistance>()

    override fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance {
        return cache.computeIfAbsent(Pair(lhs, rhs)) { (first, second) ->
            nestedEvaluator.evaluate(first, second)
        }
    }
}