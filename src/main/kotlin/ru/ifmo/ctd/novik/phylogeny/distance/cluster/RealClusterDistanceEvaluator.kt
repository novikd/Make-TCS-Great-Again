package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.Node

/**
 * Calculates distance between clusters. Only taxa from input data is taken into account.
 *
 * @author Dmitry Novik ITMO University
 */
class RealClusterDistanceEvaluator(
        distanceEvaluator: TaxonDistanceEvaluator
) : AbstractClusterDistanceEvaluator(distanceEvaluator) {
    override fun filteredCluster(cluster: Cluster): Iterable<Node> = cluster.terminals
}