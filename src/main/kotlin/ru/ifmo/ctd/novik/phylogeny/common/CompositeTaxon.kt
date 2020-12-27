package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class CompositeTaxon(val parts: List<Taxon>): Taxon {
    override val id: Int = parts.first().id

    override val name: String = parts.joinToString(separator = "|") { it.name }

    override val genome: Genome
        get() = parts.first().genome

    override val isReal: Boolean = true

    override fun clone(genome: Genome): Taxon = copy(parts = parts.map { it.clone(genome) })

    override fun contains(genome: Genome): Boolean = parts.any { it.contains(genome) }
}