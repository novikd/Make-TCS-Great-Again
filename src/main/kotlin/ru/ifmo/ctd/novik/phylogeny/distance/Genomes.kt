package ru.ifmo.ctd.novik.phylogeny.distance

fun hammingDistance(lhs: String, rhs: String): Int {
    assert(lhs.length == rhs.length) { "can't compute Hamming distance" }
    return lhs.zip(rhs).count { (left, right) -> left != right }
}