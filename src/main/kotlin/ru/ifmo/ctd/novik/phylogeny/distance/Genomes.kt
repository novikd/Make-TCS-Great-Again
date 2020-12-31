package ru.ifmo.ctd.novik.phylogeny.distance

import ru.ifmo.ctd.novik.phylogeny.common.CompressedGenomeOption
import ru.ifmo.ctd.novik.phylogeny.common.GenomeOption
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.CachingClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

fun hammingDistance(lhs: CompressedGenomeOption, rhs: CompressedGenomeOption): Int {
    var i = 0
    var j = 0

    var result = 0
    val leftMutations = lhs.polymorphism
    val rightMutations = rhs.polymorphism
    while (i < leftMutations.size && j < rightMutations.size) {
        val leftSNP = leftMutations[i]
        val rightSNP = rightMutations[j]

        if (leftSNP.index == rightSNP.index) {
            if (leftSNP.value != rightSNP.value)
                ++result
            ++i
            ++j
        } else {
            ++result
            if (leftSNP.index < rightSNP.index)
                ++i
            else
                ++j
        }
    }
    result += leftMutations.size - i
    result += rightMutations.size - j
    return result
}

fun hammingDistance(lhs: GenomeOption, rhs: GenomeOption): Int {
    if (lhs is CompressedGenomeOption && rhs is CompressedGenomeOption)
        return hammingDistance(lhs, rhs)

    return hammingDistance(lhs.toString(), rhs.toString())
}

fun hammingDistance(lhs: CharSequence, rhs: CharSequence): Int {
    assert(lhs.length == rhs.length) { "can't compute Hamming distance" }
    var result = 0
    for (i in lhs.indices) {
        if (lhs[i] != rhs[i])
            ++result
    }
    return result
}

fun TaxonDistanceEvaluator.toCaching(): TaxonDistanceEvaluator {
    if (this is CachingTaxonDistanceEvaluator<*>)
        return this
    return CachingTaxonDistanceEvaluator(this)
}

fun ClusterDistanceEvaluator.toCaching(): ClusterDistanceEvaluator {
    if (this is CachingClusterDistanceEvaluator)
        return this
    return CachingClusterDistanceEvaluator(this)
}
