package ru.ifmo.ctd.novik.phylogeny.distance.taxa

/**
 * @author Dmitry Novik ITMO University
 */
data class TaxonDistance(val value: Int, val firstGenome: String, val secondGenome: String): Comparable<TaxonDistance> {
    override operator fun compareTo(other: TaxonDistance): Int = this.value.compareTo(other.value)
}
