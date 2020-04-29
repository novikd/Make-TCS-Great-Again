package ru.ifmo.ctd.novik.phylogeny.distance

import ru.ifmo.ctd.novik.phylogeny.distance.cluster.CachingClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

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
