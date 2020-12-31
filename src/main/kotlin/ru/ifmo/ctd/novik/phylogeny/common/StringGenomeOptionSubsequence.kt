package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class StringGenomeOptionSubsequence(
    override val origin: GenomeOption,
    override val from: Int,
    override val to: Int
) : GenomeOptionSubsequence {
    override fun toString(): String = origin.toString().substring(from, to)
}