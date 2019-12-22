package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.emptyMergingCandidate

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