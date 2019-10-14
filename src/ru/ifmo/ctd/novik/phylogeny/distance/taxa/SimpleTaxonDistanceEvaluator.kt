package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import kotlin.math.min

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleTaxonDistanceEvaluator : TaxonDistanceEvaluator {
    override fun evaluate(lhs: Taxon, rhs: Taxon): Int {
        val left = lhs.genome
        val right = rhs.genome
        val length = min(left.length, right.length)

        var result = 0
        for (i in 0 until length) {
            if (left[i] != right[i])
                ++result
        }
        return result
    }

    override fun preprocessTaxonList(taxonList: List<Taxon>) {}
}