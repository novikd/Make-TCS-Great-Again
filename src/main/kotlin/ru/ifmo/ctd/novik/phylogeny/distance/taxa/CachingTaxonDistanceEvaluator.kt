package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Novik Dmitry ITMO University
 */
class CachingTaxonDistanceEvaluator<T : TaxonDistanceEvaluator>(
    private val nestedEvaluator: T
) : TaxonDistanceEvaluator {
    private var distances: Array<Array<Int>> = emptyArray()

    override fun evaluate(lhs: Taxon, rhs: Taxon): Int = distances[lhs.id][rhs.id]

    override fun preprocessTaxonList(taxonList: List<Taxon>) {
        distances = Array(taxonList.size) { Array(taxonList.size) { 0 } }
        for (i in taxonList.indices) {
            for (j in i + 1 until taxonList.size) {
                val distance = nestedEvaluator.evaluate(taxonList[i], taxonList[j])
                distances[i][j] = distance
                distances[j][i] = distance
            }
        }
    }
}