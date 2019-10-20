package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import kotlin.math.min

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleClusterDistanceEvaluator(private val distanceEvaluator: TaxonDistanceEvaluator) : ClusterDistanceEvaluator,
    TaxonDistanceEvaluator by distanceEvaluator {

    override val taxonDistanceEvaluator: TaxonDistanceEvaluator
        get() = distanceEvaluator

    override fun evaluate(lhs: Cluster, rhs: Cluster): Int {
        var result = Int.MAX_VALUE
        for (left in lhs.taxonList) {
            for (right in rhs.taxonList) {
                result = min(result, evaluate(left.taxon!!, right.taxon!!))
            }
        }
        return result
    }
}