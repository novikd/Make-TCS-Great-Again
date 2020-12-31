package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface GenomeOption {
    val length: Int

    operator fun get(index: Int): Char

    fun subSequence(from: Int, to: Int): GenomeOptionSubsequence

    fun mutate(snp: SNP): GenomeOption
}