package ru.ifmo.ctd.novik.phylogeny.distance

import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator

fun hammingDistance(lhs: CharSequence, rhs: CharSequence): Int {
    assert(lhs.length == rhs.length) { "can't compute Hamming distance" }
    return lhs.zip(rhs).count { (left, right) -> left != right }
}

fun TaxonDistanceEvaluator.toCaching(): TaxonDistanceEvaluator {
    if (this is CachingTaxonDistanceEvaluator<*>)
        return this
    return CachingTaxonDistanceEvaluator(this)
}