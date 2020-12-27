package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class ReconstructedTaxon(
    override val id: Int,
    override val genome: Genome = GenomeWithOptionSet()
) : AbstractTaxon() {
    override val name: String = "intermediate$id"
    override val isReal: Boolean = false

    override fun clone(genome: Genome): Taxon = copy(genome = genome)
}
