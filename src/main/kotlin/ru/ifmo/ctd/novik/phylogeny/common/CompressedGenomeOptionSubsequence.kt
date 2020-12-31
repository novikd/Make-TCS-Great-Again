package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class CompressedGenomeOptionSubsequence(
    override val polymorphism: List<SNP>,
    private val compressedOrigin: CompressedGenomeOption,
    override val from: Int,
    override val to: Int
) : GenomeOptionSubsequence, CompressedGenomeOption {
    override val reference: ReferenceSequence
        get() = compressedOrigin.reference
    override val origin: GenomeOption
        get() = compressedOrigin

    override fun toString(): String = origin.toString().substring(from, to)
}