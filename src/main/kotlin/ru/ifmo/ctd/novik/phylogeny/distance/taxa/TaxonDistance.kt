package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.GenomeOption

/**
 * @author Dmitry Novik ITMO University
 */
data class TaxonDistance(
        val value: Int,
        val genomes: List<Pair<GenomeOption, GenomeOption>>
) : Comparable<TaxonDistance>, Iterable<Pair<GenomeOption, GenomeOption>> {
    override fun iterator(): Iterator<Pair<GenomeOption, GenomeOption>> = genomes.iterator()

    override operator fun compareTo(other: TaxonDistance): Int = this.value.compareTo(other.value)
}
