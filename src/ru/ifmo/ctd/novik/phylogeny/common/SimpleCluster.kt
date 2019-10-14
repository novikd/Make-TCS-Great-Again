package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleCluster(override val taxonList: List<Node>) : Cluster {
    constructor(taxon: Taxon) : this(listOf(Node(taxon)))
}