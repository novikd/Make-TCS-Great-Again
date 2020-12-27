package ru.ifmo.ctd.novik.phylogeny.network.metric

import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

/**
 * @author Dmitry Novik ITMO University
 */
class TCSMergeMetric(
    private val clusterDistance: Int,
    taxonDistanceEvaluator: TaxonDistanceEvaluator
) : AbstractMergeMetric(taxonDistanceEvaluator) {

    override fun compareDistances(graphDistance: Int, realDistance: Int): Int {
        if (graphDistance == realDistance) return 20
        if (graphDistance in clusterDistance until realDistance) return -10
        return if (graphDistance < clusterDistance) Int.MIN_VALUE else -5
    }
}