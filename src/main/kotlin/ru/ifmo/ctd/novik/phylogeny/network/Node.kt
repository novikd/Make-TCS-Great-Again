package ru.ifmo.ctd.novik.phylogeny.network

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.createTaxon

/**
 * @author Dmitry Novik ITMO University
 */
data class Node(val taxon: Taxon) {
    val isRealTaxon = taxon.isReal
    var nodeName: String = taxon.name
    val neighbors: MutableList<Node> = mutableListOf()
    val next: MutableList<Node> = mutableListOf()

    constructor() : this(createTaxon())

    fun connect(node: Node) {
        if (neighbors.contains(node))
            error("Already existed neighbor")
        neighbors.add(node)
    }

    override fun toString(): String {
        return nodeName
    }
}
