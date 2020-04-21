package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import kotlin.math.min

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractTaxonDistanceEvaluator : TaxonDistanceEvaluator {
    protected fun evaluate(lhs: String, rhs: String): TaxonDistance {
        val length = min(lhs.length, rhs.length)

        hammingDistance(lhs, rhs)
        var result = 0
        for (i in 0 until length) {
            if (lhs[i] != rhs[i])
                ++result
        }
        return TaxonDistance(result, listOf(Pair(lhs, rhs)))
    }
}