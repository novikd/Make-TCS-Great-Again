package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

/**
 * @author Novik Dmitry ITMO University
 */
interface ClusterDistanceEvaluator {
    val taxonDistanceEvaluator: TaxonDistanceEvaluator

    fun evaluate(lhs: Cluster, rhs: Cluster): ClusterDistance
}

