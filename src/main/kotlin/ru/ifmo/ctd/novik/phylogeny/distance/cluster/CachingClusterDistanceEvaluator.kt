package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

class CachingClusterDistanceEvaluator(private val inner: ClusterDistanceEvaluator) : ClusterDistanceEvaluator {
    private val cache = mutableMapOf<Pair<Cluster, Cluster>, ClusterDistance>()

    override val taxonDistanceEvaluator: TaxonDistanceEvaluator
        get() = inner.taxonDistanceEvaluator

    override fun evaluate(lhs: Cluster, rhs: Cluster): ClusterDistance {
        return cache.computeIfAbsent(Pair(lhs, rhs)) { inner.evaluate(lhs, rhs) }
    }
}