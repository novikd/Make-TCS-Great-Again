package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleCluster(override val nodes: List<Node>) : Cluster {
    override val terminals: List<Node>
        get() = nodes.filter(Node::isRealTaxon)

    constructor(taxon: Taxon) : this(listOf(Node(taxon)))

    override fun iterator(): Iterator<Node> = nodes.iterator()
}