package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Novik Dmitry ITMO University
 */
data class Taxon(val id: Int, val name: String = "intermediate$id", val genome: Genome = GenomeWithOptionSet()) {
    fun contains(genome: Genome): Boolean = this.genome.contains(genome.primary)
}
