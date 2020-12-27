package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractTaxon : Taxon {
    override fun contains(genome: Genome): Boolean = this.genome.contains(genome.primary)
}