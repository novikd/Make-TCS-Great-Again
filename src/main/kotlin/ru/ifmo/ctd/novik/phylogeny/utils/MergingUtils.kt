package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.merging.IMergingBridge
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingException
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric

internal const val BRUTE_FORCE_DISTANCE_THRESHOLD = 6

internal object EmptyMergingCandidate : MergingCandidate {
    override val firstCandidate: Cluster
        get() = throw MergingException("Doesn't have any candidate")
    override val secondCandidate: Cluster
        get() = throw MergingException("Doesn't have any candidate")
    override val taxonDistanceEvaluator: TaxonDistanceEvaluator
        get() = throw MergingException("EmptyCandidate doesn't have distance evaluator")
    override val mergeMetric: MergeMetric
        get() = throw MergingException("EmptyCandidate doesn't have metric")

    override fun merge(): Cluster =
            throw MergingException("Can't merge empty candidate")
}

internal object EmptyMergingBridge : IMergingBridge {
    override val metric: Int = Int.MIN_VALUE

    override fun build() = Unit
}

fun emptyMergingCandidate(): MergingCandidate = EmptyMergingCandidate

fun emptyMergingBridge(): IMergingBridge = EmptyMergingBridge


enum class ComparisonResult {
    BETTER,
    WORSE,
    INDETERMINATE
}

fun compareCandidates(first: MergingCandidate, secondCandidate: MergingCandidate): ComparisonResult {
    if (first == emptyMergingCandidate())
        return ComparisonResult.WORSE
    if (secondCandidate == emptyMergingCandidate())
        return ComparisonResult.BETTER
    if (first is SimpleMergingCandidate && secondCandidate is SimpleMergingCandidate)
        return if (first.distance < secondCandidate.distance) ComparisonResult.BETTER else ComparisonResult.WORSE
    return ComparisonResult.INDETERMINATE
}