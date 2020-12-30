package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface MutableGenome : Genome {
    fun add(option: GenomeOption): Boolean
    fun addAll(collection: Collection<GenomeOption>): Boolean
    fun remove(option: GenomeOption): Boolean
    fun removeIf(predicate: (GenomeOption) -> Boolean): Boolean
    fun replace(newOptions: List<GenomeOption>)
}