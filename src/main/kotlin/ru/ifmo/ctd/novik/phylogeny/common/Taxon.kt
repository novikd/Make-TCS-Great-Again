package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Novik Dmitry ITMO University
 */
data class Taxon(val id: Int, val name: String = "intermediate$id", val genome: IGenome = MutableGenome()) {
    fun contains(genome: IGenome): Boolean = this.genome.contains(genome.primary)
}
