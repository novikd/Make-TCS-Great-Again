package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface IGenome : Iterable<String> {
    val primary: String

    val isEmpty: Boolean
    val isReal: Boolean
    fun contains(genome: String): Boolean
}