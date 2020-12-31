package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

/**
 * @author Dmitry Novik ITMO University
 */
interface ClusterDistanceEvaluator {
    val taxonDistanceEvaluator: TaxonDistanceEvaluator

    fun evaluate(lhs: Cluster, rhs: Cluster): ClusterDistance
}

