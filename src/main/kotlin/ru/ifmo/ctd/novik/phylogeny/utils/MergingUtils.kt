package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.merging.*
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric

internal const val BRUTE_FORCE_DISTANCE_THRESHOLD = 6

internal object EmptyMergingCandidate : MergingCandidate {
    override val firstCluster: Cluster
        get() = throw MergingException("Doesn't have any candidate")
    override val secondCluster: Cluster
        get() = throw MergingException("Doesn't have any candidate")
    override val taxonDistanceEvaluator: TaxonDistanceEvaluator
        get() = throw MergingException("EmptyCandidate doesn't have distance evaluator")
    override val mergeMetric: MergeMetric
        get() = throw MergingException("EmptyCandidate doesn't have metric")

    override fun merge(): MergingResult =
            throw MergingException("Can't merge empty candidate")

    override fun compareTo(other: MergingCandidate): Int = if (other == this) 0 else -1

    override fun toString(): String = "EMPTY_MERGING_CANDIDATE"
}

internal object EmptyMergingBridge : IMergingBridge {
    override val metric: Int = Int.MIN_VALUE

    override fun build(nodeList: MutableList<Node>) = Unit
}

fun emptyMergingCandidate(): MergingCandidate = EmptyMergingCandidate

fun emptyMergingBridge(): IMergingBridge = EmptyMergingBridge


enum class ComparisonResult {
    BETTER,
    WORSE,
    INDETERMINATE
}
