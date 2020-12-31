package ru.ifmo.ctd.novik.phylogeny.network.merging

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.metric.MergeMetric

/**
 * @author Dmitry Novik ITMO University
 */
interface MergingCandidate {
    val firstCluster: Cluster
    val secondCluster: Cluster
    val taxonDistanceEvaluator: TaxonDistanceEvaluator
    val mergeMetric: MergeMetric

    fun merge(): MergingResult
    operator fun compareTo(other: MergingCandidate): Int
}

