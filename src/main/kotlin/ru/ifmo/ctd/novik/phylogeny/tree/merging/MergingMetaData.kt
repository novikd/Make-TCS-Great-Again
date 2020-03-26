package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.utils.Path

/**
 * @author Dmitry Novik ITMO University
 */
data class MergingMetaData(
        val firstClusterPart: Path,
        val secondClusterPart: Path,
        val bridgeLength: Int
) {
    val firstRealTaxon = firstClusterPart[0].taxon
    val secondRealTaxon = secondClusterPart[0].taxon
}
