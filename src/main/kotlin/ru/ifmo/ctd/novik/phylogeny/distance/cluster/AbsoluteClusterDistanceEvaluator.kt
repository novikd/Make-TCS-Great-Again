package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.Node

/**
 * @author Dmitry Novik ITMO University
 */
class AbsoluteClusterDistanceEvaluator(
        distanceEvaluator: TaxonDistanceEvaluator
) : AbstractClusterDistanceEvaluator(distanceEvaluator) {
    override fun filteredCluster(cluster: Cluster): Iterable<Node> = cluster.filter { node -> !node.taxon.genome.isEmpty }
}