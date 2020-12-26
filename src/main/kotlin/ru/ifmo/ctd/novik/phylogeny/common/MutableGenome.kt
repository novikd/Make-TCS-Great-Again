package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface MutableGenome : Genome {
    fun add(genome: String): Boolean
    fun addAll(collection: Collection<String>): Boolean
    fun remove(genome: String): Boolean
    fun removeIf(predicate: String.() -> Boolean): Boolean
    fun replace(newOptions: List<String>)
}