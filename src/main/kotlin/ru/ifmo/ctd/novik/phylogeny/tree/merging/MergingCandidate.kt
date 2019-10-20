package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.emptyCluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric

/**
 * @author Novik Dmitry ITMO University
 */
interface MergingCandidate {
    val distance: Int
    val firstCandidate: Cluster
    val secondCandidate: Cluster

    fun merge(evaluator: TaxonDistanceEvaluator, mergeMetric: MergeMetric): Cluster
}

internal object EmptyMergingCandidate : MergingCandidate {
    override val distance: Int = Int.MAX_VALUE
    override val firstCandidate: Cluster =
        emptyCluster()
    override val secondCandidate: Cluster =
        emptyCluster()

    override fun merge(evaluator: TaxonDistanceEvaluator, mergeMetric: MergeMetric): Cluster =
        emptyCluster()
}

fun emptyMergingCandidate(): MergingCandidate =
    EmptyMergingCandidate