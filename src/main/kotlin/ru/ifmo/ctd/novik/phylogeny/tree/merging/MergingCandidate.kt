package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric

/**
 * @author Dmitry Novik ITMO University
 */
interface MergingCandidate {
    val firstCandidate: Cluster
    val secondCandidate: Cluster
    val taxonDistanceEvaluator: TaxonDistanceEvaluator
    val mergeMetric: MergeMetric

    fun merge(): Cluster
}

