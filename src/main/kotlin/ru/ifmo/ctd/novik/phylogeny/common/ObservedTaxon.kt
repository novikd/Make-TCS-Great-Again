package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class ObservedTaxon(
    override val id: Int,
    override val name: String,
    override val genome: Genome
) : AbstractTaxon() {
    override val isReal: Boolean = true

    override fun clone(genome: Genome): Taxon = copy(genome = genome)
}
