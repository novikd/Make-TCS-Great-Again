package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.Node

/**
 * Calculates distance between clusters. Distance is defined as distance between sets of filtered taxa.
 *
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractClusterDistanceEvaluator(
        override val taxonDistanceEvaluator: TaxonDistanceEvaluator
) : ClusterDistanceEvaluator {

    protected abstract fun filteredCluster(cluster: Cluster): Iterable<Node>

    override fun evaluate(lhs: Cluster, rhs: Cluster): ClusterDistance {
        var result = Int.MAX_VALUE
        val distanceMinima = mutableListOf<DistanceMinimumPoint>()
        for (left in filteredCluster(lhs)) {
            for (right in filteredCluster(rhs)) {
                val distance = taxonDistanceEvaluator.evaluate(left.taxon, right.taxon)
                if (distance.value < result) {
                    distanceMinima.clear()
                    distanceMinima.add(DistanceMinimumPoint(left, right, distance))
                    result = distance.value
                } else if (distance.value == result)
                    distanceMinima.add(DistanceMinimumPoint(left, right, distance))
            }
        }
        return ClusterDistance(result, distanceMinima)
    }
}