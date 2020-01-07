package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Dmitry Novik ITMO University
 */
data class MergingMetaData(
        val firstRealTaxon: Node,
        val firstDistance: Int,
        val secondRealTaxon: Node,
        val secondDistance: Int,
        val bridgeLength: Int
)
