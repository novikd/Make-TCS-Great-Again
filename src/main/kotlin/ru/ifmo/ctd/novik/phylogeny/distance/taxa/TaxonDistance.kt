package ru.ifmo.ctd.novik.phylogeny.distance.taxa

/**
 * @author Dmitry Novik ITMO University
 */
data class TaxonDistance(
        val value: Int,
        val genomes: List<Pair<String, String>>
) : Comparable<TaxonDistance>, Iterable<Pair<String, String>> {
    override fun iterator(): Iterator<Pair<String, String>> = genomes.iterator()

    override operator fun compareTo(other: TaxonDistance): Int = this.value.compareTo(other.value)
}
