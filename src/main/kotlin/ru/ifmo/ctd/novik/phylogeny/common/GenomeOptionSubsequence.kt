package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface GenomeOptionSubsequence : GenomeOption {
    val origin: GenomeOption
    val from: Int
    val to: Int

    override val length: Int
        get() = to - from

    override fun get(index: Int): Char {
        assert(index < length) { "index must be in range [0, subsequence length)" }
        return origin[from + index]
    }

    override fun subSequence(from: Int, to: Int): GenomeOptionSubsequence = error("Can't take a subsequence of another subsequence")

    override fun mutate(snp: SNP): GenomeOption = error("Subsequence must not be modified")
}