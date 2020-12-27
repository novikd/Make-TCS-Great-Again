package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface Taxon {
    val id: Int
    val name: String
    val genome: Genome
    val isReal: Boolean

    fun clone(genome: Genome = this.genome): Taxon //TODO: Get rid of this method
    fun contains(genome: Genome): Boolean
}