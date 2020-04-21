package ru.ifmo.ctd.novik.phylogeny.distance

fun hammingDistance(lhs: CharSequence, rhs: CharSequence): Int {
    assert(lhs.length == rhs.length) { "can't compute Hamming distance" }
    return lhs.zip(rhs).count { (left, right) -> left != right }
}