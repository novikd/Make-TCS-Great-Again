package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.utils.toGenomeOption

/**
 * @author Dmitry Novik ITMO University
 */
data class StringGenomeOption(val value: String) : GenomeOption {
    override val length: Int
        get() = value.length

    override fun get(index: Int): Char = value[index]

    override fun subSequence(from: Int, to: Int): GenomeOptionSubsequence =
        StringGenomeOptionSubsequence(this, from, to)

    override fun mutate(snp: SNP): GenomeOption = buildString(length) {
        append(value)
        setCharAt(snp.index, snp.value)
    }.toGenomeOption()

    override fun toString(): String = value
}