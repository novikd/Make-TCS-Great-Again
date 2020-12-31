package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface Genome : Iterable<GenomeOption> {
    val primary: GenomeOption
    val size: Int
    val isEmpty: Boolean

    fun mutate(mutations: List<SNP>): Genome
    fun contains(option: GenomeOption): Boolean
}